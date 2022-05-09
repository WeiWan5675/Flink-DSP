package com.weiwan.dsp.core.engine.metric;

import com.weiwan.dsp.api.config.core.EngineConfig;
import com.weiwan.dsp.api.config.flow.NodeConfig;
import com.weiwan.dsp.api.context.EngineContext;
import com.weiwan.dsp.api.enums.EngineType;
import com.weiwan.dsp.api.enums.NodeType;
import com.weiwan.dsp.api.metrics.MetricManager;
import com.weiwan.dsp.core.engine.flink.metric.FlinkMetricManager;

/**
 * @author: xiaozhennan
 * @description: metricManager工厂类
 */
public class MetricManagerFactory {


    public static MetricManager createMetricManager(String nodeId, EngineContext context) {
        EngineConfig engineConfig = context.getEngineConfig();
        EngineType engineType = engineConfig.getEngineType();
        switch (engineType) {
            case FLINK:
                return createFlinkMetricManager(nodeId, context);
            case SPARK:
                return createSparkMetricManager(nodeId, context);
            case LOCAL:
                return createLocalMetricManager(nodeId, context);
            default:
                throw new IllegalAccessError("不支持未知类型的MetricManager");
        }
    }

    private static MetricManager createLocalMetricManager(String nodeId, EngineContext context) {
        throw new IllegalAccessError("Local operation mode metric related functions are not supported at present");
    }

    private static MetricManager createSparkMetricManager(String nodeId, EngineContext context) {
        throw new IllegalAccessError("Spark metric related functions are not supported at the moment");
    }

    private static MetricManager createFlinkMetricManager(String nodeId, EngineContext context) {
        NodeConfig nodeConfig = context.getNodeConfig(nodeId);
        String jobName = context.getJobName();
        String jobId = context.getJobId();
        String nodeName = nodeConfig.getNodeName();
        NodeType nodeType = nodeConfig.getNodeType();

        DspMetricManager manager = DspMetricManager.DspMetricManagerBuilder.builder()
                .context(context)
                .metricConfig(context.getMetricConfig())
                .jobId(jobId)
                .jobName(jobName)
                .nodeId(nodeId)
                .nodeName(nodeName)
                .nodeType(nodeType).build(FlinkMetricManager.class);
        return manager;
    }

}
