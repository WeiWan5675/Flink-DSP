package com.weiwan.dsp.core.engine.metric;

import com.weiwan.dsp.api.config.flow.NodeConfig;
import com.weiwan.dsp.api.context.EngineContext;
import com.weiwan.dsp.api.enums.MetricKey;
import com.weiwan.dsp.api.enums.MetricType;
import com.weiwan.dsp.api.enums.NodeType;
import com.weiwan.dsp.api.metrics.Metric;
import com.weiwan.dsp.common.exception.DspException;
import com.weiwan.dsp.core.engine.flink.metric.FlinkCounter;

/**
 * @author: xiaozhennan
 * @description: dspMetric的基础类
 */
public class DspMetric implements Metric {
    private String jobId;
    private String jobName;
    private String nodeId;
    private String nodeName;
    private NodeType nodeType;
    private MetricType metricType;
    private static final String METRIC_ID_FORMAT = "DspMetric-[%s|%s|%s]-[%s]-%s";

    public DspMetric(DspMetric dspMetric) {
        this.jobId = dspMetric.getJobId();
        this.jobName = dspMetric.getJobName();
        this.nodeId = dspMetric.getNodeId();
        this.nodeName = dspMetric.getNodeName();
        this.nodeType = dspMetric.getNodeType();
    }


    public DspMetric() {
    }

    public static <T extends DspMetric> T createMetric(EngineContext engineContext, NodeConfig nodeConfig, MetricType metricType, Class<T> tClass) {
        return DspMetric.DspMetricBuilder.builder()
                .jobId(engineContext.getJobId())
                .jobName(engineContext.getJobName())
                .nodeId(nodeConfig.getNodeId())
                .nodeName(nodeConfig.getNodeName())
                .nodeType(nodeConfig.getNodeType())
                .metricType(metricType)
                .build(tClass);
    }


    public static DspCounter createCounter(EngineContext engineContext, NodeConfig nodeConfig, MetricType metricType) {
        switch (engineContext.getEngineConfig().getEngineType()) {
            case FLINK:
                return createMetric(engineContext, nodeConfig, metricType, FlinkCounter.class);
            case SPARK:
            case LOCAL:
                throw new DspException("Metric in spark local mode is not yet supported");
        }
        return null;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public MetricType getMetricType() {
        return metricType;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    @Override
    public String getMetricId(String counterId) {
        //DspMetric-[custom]-[123132|TestJob]-[R1|r1|READER]-用户定义指标
        return String.format(METRIC_ID_FORMAT, nodeId, nodeName, nodeType.name(), metricType.name(), counterId);
    }
    @Override
    public String getMetricId(MetricKey keyId) {
        //DspMetric-[custom]-[123132|TestJob]-[R1|r1|READER]-用户定义指标
        return String.format(METRIC_ID_FORMAT, nodeId, nodeName, nodeType.name(), metricType.name(), keyId.getKey());
    }


    public static final class DspMetricBuilder {
        private String jobId;
        private String jobName;
        private String nodeId;
        private String nodeName;
        private NodeType nodeType;
        private MetricType metricType;

        private DspMetricBuilder() {
        }

        public static DspMetricBuilder builder() {
            return new DspMetricBuilder();
        }

        public DspMetricBuilder jobId(String jobId) {
            this.jobId = jobId;
            return this;
        }

        public DspMetricBuilder jobName(String jobName) {
            this.jobName = jobName;
            return this;
        }

        public DspMetricBuilder nodeId(String nodeId) {
            this.nodeId = nodeId;
            return this;
        }

        public DspMetricBuilder nodeName(String nodeName) {
            this.nodeName = nodeName;
            return this;
        }

        public DspMetricBuilder nodeType(NodeType nodeType) {
            this.nodeType = nodeType;
            return this;
        }

        public DspMetricBuilder metricType(MetricType metricType) {
            this.metricType = metricType;
            return this;
        }

        public <T extends DspMetric> T build(Class<T> tClass) {
            T t = null;
            try {
                t = tClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            DspMetric dspMetric = (DspMetric) t;
            dspMetric.setJobId(jobId);
            dspMetric.setJobName(jobName);
            dspMetric.setNodeId(nodeId);
            dspMetric.setNodeName(nodeName);
            dspMetric.setNodeType(nodeType);
            dspMetric.setMetricType(metricType);
            return t;
        }
    }

    private void setMetricType(MetricType metricType) {
        this.metricType = metricType;
    }
}
