package com.weiwan.dsp.core.engine.flink;

import com.weiwan.dsp.api.config.core.EngineConfig;
import com.weiwan.dsp.api.config.flow.NodeConfig;
import com.weiwan.dsp.api.context.EngineContext;
import com.weiwan.dsp.api.enums.EngineMode;
import com.weiwan.dsp.api.enums.EngineType;
import com.weiwan.dsp.common.constants.Constants;
import com.weiwan.dsp.core.engine.flink.checkpoint.RecoveryState;
import com.weiwan.dsp.core.engine.snapshot.EngineSnapshot;
import com.weiwan.dsp.core.engine.snapshot.NodeState;
import com.weiwan.dsp.core.engine.snapshot.Snapshot;
import com.weiwan.dsp.api.constants.CoreConstants;
import org.apache.flink.api.common.state.KeyedStateStore;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.state.OperatorStateStore;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author: xiaozhennan
 * @description:
 */
public class FlinkRuntimeUtil {

    private static final Logger logger = LoggerFactory.getLogger(FlinkRuntimeUtil.class);

    public static FlinkEngineExtInfo createFlinkEngineExtInfo(EngineContext engineContext, int indexOfThisSubtask, int totalSubTask) {
        EngineConfig engineConfig = engineContext.getEngineConfig();
        EngineType engineType = engineConfig.getEngineType();
        EngineMode engineMode = engineConfig.getEngineMode();

        FlinkEngineExtInfo flinkEngineExtInfo = new FlinkEngineExtInfo(engineType, engineMode);
        flinkEngineExtInfo.setThisTaskNum(indexOfThisSubtask);
        flinkEngineExtInfo.setTotalTaskNum(totalSubTask);
        return flinkEngineExtInfo;
    }


    /**
     * 设置状态后端,这里主要是有一个按照子任务获取对应的状态的操作
     */
    public static void stateRecoverySetting(Map<Integer, NodeState> stateMap, EngineSnapshot engineSnapshot, int taskNum) {
        logger.info("restore the job, current task num: {}, state: {}", taskNum, stateMap);
        if (stateMap != null && stateMap.size() > 0) {
            NodeState nodeState = stateMap.get(taskNum);
            Snapshot snapshot = new Snapshot();
            if (nodeState != null) {
                Snapshot oldSnapshod = nodeState.getSnapshot();
                engineSnapshot.initSnapshot(oldSnapshod != null ? oldSnapshod : snapshot, true);
            }
        }
    }


    public static RecoveryState initStateSetting(FunctionInitializationContext context, EngineContext engineContext, NodeConfig nodeConfig) throws Exception {
        OperatorStateStore stateStore = context.getOperatorStateStore();
        ListState<NodeState> listState = stateStore.getUnionListState(new ListStateDescriptor<NodeState>(
                engineContext.getJobName() + Constants.HENG_GANG + nodeConfig.getNodeId() + CoreConstants.STATE_STR,
                TypeInformation.of(new TypeHint<NodeState>() {
                })));
        RecoveryState recoveryState = new RecoveryState();
        recoveryState.setListState(listState);
        if (context.isRestored()) {
            logger.info("Start from the checkpoint, cache the last job status for recovery: {}", listState.toString());
            recoveryState.setRestore(true);
            Map<Integer, NodeState> cacheMapStates = recoveryState.getCacheMapStates();
            //这里缓存state是为了进行任务恢复,任务恢复时,是有可能带有标号的.根据直接子任务标号,恢复自己的State
            for (NodeState nodeState : listState.get()) {
                cacheMapStates.put(nodeState.getNumOfSubTask(), nodeState);
            }
        }
        return recoveryState;
    }
}
