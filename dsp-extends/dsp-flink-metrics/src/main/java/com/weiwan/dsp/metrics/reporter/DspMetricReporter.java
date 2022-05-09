package com.weiwan.dsp.metrics.reporter;

import org.apache.flink.metrics.MetricConfig;
import org.apache.flink.metrics.reporter.AbstractReporter;
import org.apache.flink.metrics.reporter.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: xiaozhennan
 * @Date: 2021/10/26 21:23
 * @ClassName: DspMetricReporter
 * @Description: DspMetricReporter
 * 1. httpReport
 * com.weiwan.dsp.metrics.reporter.HttpPushReport
 * 配置项
 * metrics.reporter.dsp-report.class=com.weiwan.dsp.metrics.reporter.HttpPushReport
 * metrics.reporter.dsp-report.port=9875
 * metrics.reporter.dsp-report.host=127.0.0.1
 * metrics.reporter.dsp-report.interval=30
 * 2. jdbcReport
 * com.weiwan.dsp.metrics.reporter.JdbcPushReport
 **/
public abstract class DspMetricReporter extends AbstractReporter implements Scheduled {

    private Logger LOGGER = LoggerFactory.getLogger(DspMetricReporter.class);
    protected MetricConfig metricConfig;
    private DspMetricGroup dspMetricGroup;

    @Override
    public String filterCharacters(String input) {
        if(!input.startsWith("DspMetric")){
            return "";
        }
        return input;
    }

    @Override
    public void open(MetricConfig metricConfig) {
        this.metricConfig = metricConfig;
        this.dspMetricGroup = new DspMetricGroup(gauges, counters, histograms, meters);
        this.init(metricConfig);
    }


    public abstract void init(MetricConfig config);

    public abstract void stop();

    @Override
    public void report() {
        reportMetrics(dspMetricGroup);
    }

    abstract void reportMetrics(DspMetricGroup group);

    @Override
    public void close() {
        this.stop();
    }
}
