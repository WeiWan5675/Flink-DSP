package com.weiwan.dsp.core.engine.flink.metric;


import com.weiwan.dsp.api.enums.MetricKey;
import com.weiwan.dsp.api.enums.MetricType;
import com.weiwan.dsp.api.metrics.MetricCounter;
import com.weiwan.dsp.api.metrics.MetricGauge;
import com.weiwan.dsp.api.metrics.MetricMeter;
import com.weiwan.dsp.core.engine.metric.DspMetric;
import com.weiwan.dsp.core.engine.metric.DspMetricManager;
import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.MetricGroup;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/12 15:21
 * @description:
 */
public class FlinkMetricManager extends DspMetricManager {

    private MetricGroup metricGroup;
    private static final String FLINK_METRIC_GROUP_FORMAT = "DspMetricGroup-[%s|%s]";

    @Override
    public MetricCounter registerCounter(String counterId) {
        DspMetric dspMetric = buildDspMetric(MetricType.CUSTOM, FlinkCounter.class);
        String metricId = dspMetric.getMetricId(counterId);
        FlinkCounter flinkCounter = new FlinkCounter(dspMetric);
        return metricGroup.counter(metricId, flinkCounter);
    }


    @Override
    public MetricGauge registerGauge(String gaugeId) {
        DspMetric dspMetric = buildDspMetric(MetricType.CUSTOM, FlinkGauge.class);
        String metricId = dspMetric.getMetricId(gaugeId);
        FlinkGauge flinkGauge = new FlinkGauge(dspMetric);
        return metricGroup.gauge(metricId, flinkGauge);
    }

    @Override
    public MetricMeter registerMeter(String meterId) {
        DspMetric dspMetric = buildDspMetric(MetricType.CUSTOM, FlinkMeter.class);
        String metricId = dspMetric.getMetricId(meterId);
        FlinkMeter flinkMeter = new FlinkMeter(dspMetric);
        return metricGroup.meter(metricId, flinkMeter);
    }


    @Override
    public void open(Object engineMetric) {
        if (engineMetric instanceof MetricGroup) {
            String groupId = String.format(FLINK_METRIC_GROUP_FORMAT, jobId, jobName);
            this.metricGroup = ((MetricGroup) engineMetric).addGroup(groupId);
        }
    }


    @Override
    public void close() {
        this.metricGroup = null;
    }

    @Override
    public <T> T registerMetric(MetricKey metricKey, Class<T> tClass) {
        return registerMetric(metricKey.getKey(), tClass);
    }

    public <T> T registerMetric(String metricKey, Class<T> tClass) {
        DspMetric dspMetric = null;
        if (tClass == MetricCounter.class) {
            dspMetric = buildDspMetric(MetricType.SYSTEM, FlinkCounter.class);
            String metricId = dspMetric.getMetricId(metricKey);
            FlinkCounter flinkCounter = new FlinkCounter(dspMetric);
            return (T) metricGroup.counter(metricId, flinkCounter);
        } else if (tClass == MetricGauge.class) {
            dspMetric = buildDspMetric(MetricType.SYSTEM, FlinkGauge.class);
            String metricId = dspMetric.getMetricId(metricKey);
            FlinkGauge flinkGauge = new FlinkGauge(dspMetric);
            return (T) metricGroup.gauge(metricId, flinkGauge);
        } else {
            dspMetric = buildDspMetric(MetricType.SYSTEM, FlinkMeter.class);
            String metricId = dspMetric.getMetricId(metricKey);
            FlinkMeter flinkMeter = new FlinkMeter(dspMetric);
            return (T) metricGroup.meter(metricId, flinkMeter);
        }
    }


}
