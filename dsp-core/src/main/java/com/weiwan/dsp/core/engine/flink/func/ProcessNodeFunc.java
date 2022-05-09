package com.weiwan.dsp.core.engine.flink.func;

import com.weiwan.dsp.api.config.flow.NodeConfig;
import com.weiwan.dsp.api.context.EngineContext;
import com.weiwan.dsp.api.plugin.PluginContainer;
import com.weiwan.dsp.api.pojo.DataRecord;
import com.weiwan.dsp.core.engine.ext.DefaultEngineExtSupport;
import com.weiwan.dsp.core.engine.ext.EngineExtSupport;
import com.weiwan.dsp.core.engine.flink.FlinkEngineExtInfo;
import com.weiwan.dsp.core.engine.flink.FlinkRuntimeUtil;
import com.weiwan.dsp.core.engine.flink.checkpoint.CheckpointInterface;
import com.weiwan.dsp.core.engine.flink.checkpoint.RecoveryState;
import com.weiwan.dsp.core.engine.metric.MetricCenter;
import com.weiwan.dsp.core.engine.snapshot.*;
import com.weiwan.dsp.core.plugin.container.*;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: xiaozhennan
 * @Date: 2021/6/13 22:23
 * @Package: com.weiwan.dsp.core.engine.flink
 * @ClassName: ProcessNodeFunc
 * @Description: Flink数据处理算子
 **/
public class ProcessNodeFunc extends ProcessFunction<DataRecord, DataRecord> implements CheckpointInterface {
    private static final Logger logger = LoggerFactory.getLogger(ProcessNodeFunc.class);

    private PluginRuningContainer pluginContainer;
    private EngineContext engineContext;
    private NodeConfig nodeConfig;

    //open
    private int totalSubTask = -1;
    private int indexOfThisSubtask = -1;

    //state
    private RecoveryState recoveryState;

    //ext
    private EngineExtSupport engineExtSupport;

    //snapshot
    private EngineSnapshot engineSnapshot;
    private MetricCenter metricCenter;
    private volatile boolean isRuning;

    public ProcessNodeFunc(EngineContext context, PluginContainer container) {
        this.engineContext = context;
        this.nodeConfig = ((PluginRuningContainer) container).getNodeConfig();
        this.pluginContainer = (PluginRuningContainer) container;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        RuntimeContext runtimeContext = getRuntimeContext();
        this.totalSubTask = runtimeContext.getNumberOfParallelSubtasks();
        this.indexOfThisSubtask = runtimeContext.getIndexOfThisSubtask();

        //设置context
        this.pluginContainer.setContext(engineContext);
        //1. 获取EngineExtInfo
        FlinkEngineExtInfo flinkEngineExtInfo = FlinkRuntimeUtil.createFlinkEngineExtInfo(engineContext, indexOfThisSubtask, totalSubTask);

        //2. egnineStateFacde 设置
        this.engineSnapshot = (EngineSnapshot) new DefaultEngineSnapshot();

        //3. 创建一个EngineExtSupport, 这个类实际上是框架暴露给用户的接口对象
        this.engineExtSupport = new DefaultEngineExtSupport(flinkEngineExtInfo, (EngineSnapshotFacade) engineSnapshot);

        //4. 设置用户接口
        this.pluginContainer.setEngineSupport(engineExtSupport);

        //5. recovery设置
        if (recoveryState != null && recoveryState.isRestore() && this.pluginContainer instanceof ProcessPluginContainer) {
            FlinkRuntimeUtil.stateRecoverySetting(recoveryState.getCacheMapStates(), engineSnapshot, indexOfThisSubtask);
        }else{
            engineSnapshot.initSnapshot(null, false);
        }

        //6. metric设置
        this.metricCenter = (MetricCenter) engineContext.getMetricManager(nodeConfig.getNodeId());
        this.metricCenter.open(runtimeContext.getMetricGroup());

        //7. 初始化插件容器
        this.pluginContainer.init();

        if (this.pluginContainer instanceof ProcessPluginContainer) {
            //do nothing
        }
        this.isRuning = true;
    }


    @Override
    public void close() throws Exception {
        try {
            this.isRuning = false;
            this.pluginContainer.close();
            this.metricCenter.close();
        } catch (Exception e) {
            logger.warn("An exception occurred while the process node was shutting down, which can be ignored in general", e);
        }
    }

    @Override
    public void processElement(DataRecord value, Context ctx, Collector<DataRecord> out) throws Exception {
        DataRecord process = pluginContainer.process(value);
        if (process != null) {
            out.collect(process);
        }
    }


    @Override
    public void notifyCheckpointComplete(long checkpointId) throws Exception {
        engineSnapshot.notifySuccess(checkpointId);
    }

    @Override
    public void notifyCheckpointAborted(long checkpointId) throws Exception {
        engineSnapshot.notifyFail(checkpointId);
    }

    @Override
    public void snapshotState(FunctionSnapshotContext context) throws Exception {
        if (this.pluginContainer != null) {
            Snapshot snapshot = engineSnapshot.getSnapshot();
            if (snapshot != null) {
                NodeState nodeState = new NodeState(this.indexOfThisSubtask, snapshot);
                this.recoveryState.clearState();
                this.recoveryState.addState(nodeState);
            }
        }
    }

    @Override
    public void initializeState(FunctionInitializationContext context) throws Exception {
        this.recoveryState = FlinkRuntimeUtil.initStateSetting(context, engineContext, nodeConfig);
    }


}
