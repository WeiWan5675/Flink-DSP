package com.weiwan.dsp.core.engine.metric;

import com.weiwan.dsp.common.config.AbstractConfig;
import org.apache.flink.metrics.Metric;
import org.apache.flink.metrics.MetricConfig;
import org.apache.flink.metrics.MetricGroup;
import org.apache.flink.metrics.reporter.AbstractReporter;
import org.apache.flink.metrics.reporter.Scheduled;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/15 11:07
 * @description:
 */
public abstract class DspMetricsReport extends AbstractReporter implements Scheduled {


    @Override
    public String filterCharacters(String s) {
        return null;
    }

    @Override
    public void open(MetricConfig config) {

    }

    @Override
    public void close() {

    }

    @Override
    public void notifyOfAddedMetric(Metric metric, String metricName, MetricGroup group) {
        super.notifyOfAddedMetric(metric, metricName, group);
    }

    @Override
    public void notifyOfRemovedMetric(Metric metric, String metricName, MetricGroup group) {
        super.notifyOfRemovedMetric(metric, metricName, group);
    }

    @Override
    public void report() {

    }
}
