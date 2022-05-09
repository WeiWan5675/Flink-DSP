package com.weiwan.dsp.core.engine.metric;

import com.weiwan.dsp.api.config.core.MetricConfig;
import com.weiwan.dsp.api.context.EngineContext;
import com.weiwan.dsp.api.enums.MetricType;
import com.weiwan.dsp.api.enums.NodeType;
import com.weiwan.dsp.api.metrics.MetricManager;

/**
 * @author: xiaozhennan
 * @description: metricManager基础类,封装了一些基本信息
 */
public abstract class DspMetricManager implements MetricManager, MetricCenter {
    protected String jobId;
    protected String jobName;
    protected String nodeId;
    protected String nodeName;
    protected NodeType nodeType;
    protected MetricConfig metricConfig;
    protected EngineContext context;

    private DspMetricManager(String jobId, String nodeId) {
        this.jobId = jobId;
        this.nodeId = nodeId;
    }


    public DspMetricManager() {
    }

    protected <T extends DspMetric> DspMetric buildDspMetric(MetricType metricType, Class<T> tClass) {
        DspMetric build = DspMetric.DspMetricBuilder.builder()
                .jobId(jobId)
                .jobName(jobName)
                .nodeId(nodeId)
                .nodeName(nodeName)
                .metricType(metricType)
                .nodeType(nodeType).build(tClass);
        return build;
    }

    @Override
    public void setContext(EngineContext context) {
        this.context = context;
    }


    public static final class DspMetricManagerBuilder {
        private String jobId;
        private String jobName;
        private String nodeId;
        private String nodeName;
        private NodeType nodeType;
        private MetricConfig metricConfig;
        private EngineContext context;

        private DspMetricManagerBuilder() {
        }

        public static DspMetricManagerBuilder builder() {
            return new DspMetricManagerBuilder();
        }

        public DspMetricManagerBuilder jobId(String jobId) {
            this.jobId = jobId;
            return this;
        }

        public DspMetricManagerBuilder jobName(String jobName) {
            this.jobName = jobName;
            return this;
        }

        public DspMetricManagerBuilder nodeId(String nodeId) {
            this.nodeId = nodeId;
            return this;
        }

        public DspMetricManagerBuilder nodeName(String nodeName) {
            this.nodeName = nodeName;
            return this;
        }

        public DspMetricManagerBuilder nodeType(NodeType nodeType) {
            this.nodeType = nodeType;
            return this;
        }

        public DspMetricManagerBuilder metricConfig(MetricConfig metricConfig) {
            this.metricConfig = metricConfig;
            return this;
        }

        public DspMetricManagerBuilder context(EngineContext context) {
            this.context = context;
            return this;
        }

        public <T extends DspMetricManager> DspMetricManager build(Class<T> tClass) {
            T t = null;
            try {
                t = tClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            DspMetricManager dspMetricManager = t;
            dspMetricManager.jobId = jobId;
            dspMetricManager.jobName = jobName;
            dspMetricManager.setContext(context);
            dspMetricManager.jobName = this.jobName;
            dspMetricManager.nodeId = this.nodeId;
            dspMetricManager.nodeName = this.nodeName;
            dspMetricManager.metricConfig = this.metricConfig;
            dspMetricManager.nodeType = this.nodeType;
            return t;
        }
    }
}
