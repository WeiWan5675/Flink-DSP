package com.weiwan.dsp.metrics.reporter;

import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.Gauge;
import org.apache.flink.metrics.Histogram;
import org.apache.flink.metrics.Meter;

import java.util.Map;

/**
 * @Author: xiaozhennan
 * @Date: 2021/10/29 20:10
 * @ClassName: DspMetricGroup
 * @Description:
 **/
public class DspMetricGroup {
    private Map<Gauge<?>, String> gauges;
    private Map<Counter, String> counters;
    private Map<Histogram, String> histograms;
    private Map<Meter, String> meters;

    public DspMetricGroup(Map<Gauge<?>, String> gauges, Map<Counter, String> counters, Map<Histogram, String> histograms, Map<Meter, String> meters) {
        this.gauges = gauges;
        this.counters = counters;
        this.histograms = histograms;
        this.meters = meters;
    }

    public Map<Gauge<?>, String> getGauges() {
        return gauges;
    }

    public void setGauges(Map<Gauge<?>, String> gauges) {
        this.gauges = gauges;
    }

    public Map<Counter, String> getCounters() {
        return counters;
    }

    public void setCounters(Map<Counter, String> counters) {
        this.counters = counters;
    }

    public Map<Histogram, String> getHistograms() {
        return histograms;
    }

    public void setHistograms(Map<Histogram, String> histograms) {
        this.histograms = histograms;
    }

    public Map<Meter, String> getMeters() {
        return meters;
    }

    public void setMeters(Map<Meter, String> meters) {
        this.meters = meters;
    }
}
