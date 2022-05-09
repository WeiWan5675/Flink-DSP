package com.weiwan.dsp.core.engine;

import com.weiwan.dsp.api.config.core.EngineConfig;
import com.weiwan.dsp.api.context.EngineContext;
import com.weiwan.dsp.api.enums.EngineType;
import com.weiwan.dsp.common.exception.DspException;
import com.weiwan.dsp.common.utils.ReflectUtil;
import com.weiwan.dsp.core.engine.flink.FlinkJobFlowEngine;
import com.weiwan.dsp.core.flow.DspJobFlowGraph;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/16 10:17
 * @description: 任务引擎的工程, 可以创建javaHeap引擎, Flink引擎, Spark引擎
 */
public class FlowEngineFactory {

    public static DspJobFlowEngine createEngine(EngineContext context) throws Exception {
        EngineConfig engineConfig = context.getEngineConfig();
        EngineType engineType = engineConfig.getEngineType();
        switch (engineType) {
            case FLINK:
                return createFlinkEngine(context, null);
            case SPARK:
                return createSparkEngine(context, null);
            case LOCAL:
                return createJavaHeapEngine(context, null);
        }
        throw new DspException("No corresponding task execution engine was found");
    }

    public static DspJobFlowEngine createEngine(EngineContext context, DspJobFlowGraph graph) throws Exception {
        EngineConfig engineConfig = context.getEngineConfig();
        EngineType engineType = engineConfig.getEngineType();
        switch (engineType) {
            case FLINK:
                return createFlinkEngine(context, graph);
            case SPARK:
                return createSparkEngine(context, graph);
            case LOCAL:
                return createJavaHeapEngine(context, graph);
        }
        throw new DspException("No corresponding task execution engine was found");
    }


    public static DspJobFlowEngine createFlinkEngine(EngineContext engineContext, DspJobFlowGraph dspJobFlowGraph) throws Exception {
        Class<?> engineClass = Class.forName(EngineType.FLINK.getEngineClass());
        FlinkJobFlowEngine flinkFlowEngine = (FlinkJobFlowEngine) engineClass.newInstance();
        ReflectUtil.setFieldValue(flinkFlowEngine, "engineContext", engineContext);
        return flinkFlowEngine;
    }

    public static DspJobFlowEngine createJavaHeapEngine(EngineContext engineContext, DspJobFlowGraph dspJobFlowGraph) {
        throw new RuntimeException("Local mode engine configuration is not supported at this time");
    }

    public static DspJobFlowEngine createSparkEngine(EngineContext engineContext, DspJobFlowGraph dspJobFlowGraph) {
        throw new RuntimeException("Spark engine configuration is not supported at the moment");
    }
}
