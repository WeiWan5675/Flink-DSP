package com.weiwan.dsp.core.engine.flink.func;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.weiwan.dsp.api.config.flow.NodeConfig;
import com.weiwan.dsp.api.context.EngineContext;
import com.weiwan.dsp.api.enums.MetricKey;
import com.weiwan.dsp.api.enums.UnresolvedType;
import com.weiwan.dsp.api.metrics.CalculationFormula;
import com.weiwan.dsp.api.metrics.MetricCounter;
import com.weiwan.dsp.api.metrics.MetricGauge;
import com.weiwan.dsp.api.plugin.Plugin;
import com.weiwan.dsp.api.plugin.PluginContainer;
import com.weiwan.dsp.api.resolve.UnresolvedDataCollector;
import com.weiwan.dsp.api.pojo.DataRecord;
import com.weiwan.dsp.common.constants.Constants;
import com.weiwan.dsp.core.engine.ext.DefaultEngineExtSupport;
import com.weiwan.dsp.core.engine.ext.EngineExtSupport;
import com.weiwan.dsp.core.engine.flink.FlinkEngineExtInfo;
import com.weiwan.dsp.core.engine.flink.FlinkRuntimeUtil;
import com.weiwan.dsp.core.engine.flink.checkpoint.CheckpointInterface;
import com.weiwan.dsp.core.engine.flink.checkpoint.RecoveryState;
import com.weiwan.dsp.core.engine.metric.MetricCenter;
import com.weiwan.dsp.core.engine.snapshot.*;
import com.weiwan.dsp.core.plugin.container.SplitPluginContainr;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: xiaozhennan
 * @date: 2021/6/17 13:58
 * @description: 数据流拆分算子
 */
public class DataStreamSpliter extends ProcessFunction<DataRecord, DataRecord> implements CheckpointInterface {
    private static final Logger logger = LoggerFactory.getLogger(DataStreamSpliter.class);
    //构造
    private SplitPluginContainr pluginContainer;
    private EngineContext engineContext;
    private NodeConfig nodeConfig;

    //open
    private MetricCenter metricCenter;
    private UnresolvedDataCollector unresolvedCollector;
    private Integer totalSubTask = -1;
    private Integer indexOfThisSubtask = -1;
    //state
    private RecoveryState recoveryState;
    //ext
    private EngineExtSupport engineExtSupport;
    //snapshot
    private EngineSnapshot engineSnapshot;
    private volatile boolean isRuning;
    private Map<String, DspOutputTag> tagMap = new LinkedHashMap<>();
    private MetricCounter splitTotalNum;
    private MetricCounter splitTotalSucsNum;
    private MetricCounter splitTotalFailNum;
    private MetricCounter splitMatchedNum;
    private MetricCounter splitUnMatchedNum;
    private MetricGauge<Long> splitRealEps;
    private MetricGauge<Long> splitSpentTime;
    private long currentProcessTime;


    public DataStreamSpliter(EngineContext context, PluginContainer container) {
        this.engineContext = context;
        this.pluginContainer = (SplitPluginContainr) container;
        this.nodeConfig = pluginContainer.getNodeConfig();
        this.tagMap = createOutputTags(this.pluginContainer);
    }

    private Map<String, DspOutputTag> createOutputTags(SplitPluginContainr pluginContainer) {
        List<Plugin> plugins = pluginContainer.getPlugins();
        NodeConfig nodeConfig = pluginContainer.getNodeConfig();
        String nodeId = nodeConfig.getNodeId();

        LinkedHashMap<String, DspOutputTag> tagLinkedHashMap = new LinkedHashMap<>();
        //这里根据插件的顺序,创建对应的tag
        int i = 1;
        String originNodeId = nodeId.split("\\" + Constants.SIGN_POINT)[0];
        for (Plugin plugin : plugins) {
            String tagId = originNodeId + Constants.SIGN_POINT + i;;
            DspOutputTag dspOutputTag = new DspOutputTag(tagId, plugin);
            tagLinkedHashMap.put(tagId, dspOutputTag);
            i = i + 1;
        }
        return tagLinkedHashMap;
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

        //6. recovery设置
        if (recoveryState != null && recoveryState.isRestore() && this.pluginContainer instanceof SplitPluginContainr) {
            FlinkRuntimeUtil.stateRecoverySetting(recoveryState.getCacheMapStates(), engineSnapshot, indexOfThisSubtask);
        } else {
            engineSnapshot.initSnapshot(null, false);
        }
        //7. metric设置
        this.metricCenter = (MetricCenter) engineContext.getMetricManager(nodeConfig.getNodeId());

        metricCenter.open(runtimeContext.getMetricGroup());
        //8. 初始化插件容器
        this.pluginContainer.init();
        //9. 获取未解析收集器
        this.unresolvedCollector = (UnresolvedDataCollector) engineContext.getUnresolvedCollector(nodeConfig.getNodeId());
        //10. 注册metrics
        registerMetrics();
        this.isRuning = true;
    }

    private void registerMetrics() {
        //总处理数
        this.splitTotalNum = metricCenter.registerMetric(MetricKey.SPLIT_TOTAL_NUM, MetricCounter.class);
        //总处理成功数
        this.splitTotalSucsNum = metricCenter.registerMetric(MetricKey.SPLIT_TOTAL_SUCS_NUM, MetricCounter.class);
        //总处理失败数
        this.splitTotalFailNum = metricCenter.registerMetric(MetricKey.SPLIT_TOTAL_FAIL_NUM, MetricCounter.class);
        //匹配数
        this.splitMatchedNum = metricCenter.registerMetric(MetricKey.SPLIT_MATCHED_NUM, MetricCounter.class);
        //未匹配数
        this.splitUnMatchedNum = metricCenter.registerMetric(MetricKey.SPLIT_UNMATCHED_NUM, MetricCounter.class);
        //实时EPS
        this.splitRealEps = metricCenter.registerMetric(MetricKey.SPLIT_REAL_EPS, MetricGauge.class);
        this.splitRealEps.formula(new CalculationFormula<Long>() {
            private long lastCount;
            private long lastTime;
            @Override
            public Long calculation() {
                final long currentCount = splitTotalNum.getCount();
                final long currentTime = System.currentTimeMillis();
                if(lastCount == 0) this.lastCount = currentCount;
                if(lastTime == 0) this.lastTime = currentTime;
                long c = currentCount - lastCount;
                if(c == 0) return 0L;
                long t = currentTime - lastTime;
                if(t < 1000L) t = 1000L;
                this.lastTime = currentTime;
                this.lastCount = currentCount;
                return t != 0 ? c / (t / 1000) : 0L;
            }
        });
        //平均处理时间
        this.splitSpentTime = metricCenter.registerMetric(MetricKey.SPLIT_SPENT_TIME, MetricGauge.class);
        this.splitSpentTime.formula(new CalculationFormula<Long>() {
            private long lastCount;
            private long lastTime;

            @Override
            public Long calculation() {
                final long currentCount = splitTotalNum.getCount();
                final long spendTime = currentProcessTime;
                if (lastTime == 0 || lastCount == 0) {
                    this.lastTime = spendTime;
                    this.lastCount = currentCount;
                    return spendTime;
                }
                long c = currentCount - lastCount;
                if (spendTime == 0 || currentCount == 0) return 1L;
                long t = spendTime - lastTime;
                this.lastTime = spendTime;
                this.lastCount = currentCount;
                return c != 0 ? t / c + 1: 1L;
            }
        });

    }

    @Override
    public void close() throws Exception {
        try {
            this.isRuning = false;
            this.pluginContainer.close();
            if (this.pluginContainer != null) {
                this.pluginContainer.close();
            }
            if (this.metricCenter != null) {
                this.metricCenter.close();
            }
            if(unresolvedCollector != null){
                unresolvedCollector.close();
            }
        } catch (Exception e) {
            logger.warn("An exception occurred while the split node was shutting down, which can be ignored in general", e);
        }
    }

    @Override
    public void processElement(DataRecord value, Context ctx, Collector<DataRecord> out) throws Exception {
        boolean matchFlag = false;
        long startMatchTime = System.currentTimeMillis();
        try {
            for (String key : tagMap.keySet()) {
                DspOutputTag dspOutputTag = tagMap.get(key);
                if (dspOutputTag.matchTag(value)) {
                    dspOutputTag.out(ctx, value);
                    matchFlag = true;
                }
            }
            if (!matchFlag) {
                splitUnMatchedNum.inc();
                out.collect(value);
            } else {
                splitMatchedNum.inc();
            }
            splitTotalSucsNum.inc();
        } catch (Exception e) {
            splitTotalFailNum.inc();
            unresolvedCollector.collect(UnresolvedType.FAIL, value, ExceptionUtil.getRootCauseMessage(e));
        } finally {
            long endMatchTime = System.currentTimeMillis();
            currentProcessTime += (endMatchTime - startMatchTime) + 1;
            splitTotalNum.inc();
        }

    }


    public Map<String, DspOutputTag> getDspTags() {
        if (tagMap != null && !tagMap.isEmpty()) {
            return tagMap;
        }
        return new HashMap<>();
    }

    public DspOutputTag getDspTag(String subId) {
        if (tagMap != null && tagMap.containsKey(subId)) {
            return tagMap.get(subId);
        }
        return null;
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
