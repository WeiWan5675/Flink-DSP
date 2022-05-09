package com.weiwan.dsp.core.engine.metric;

import com.weiwan.dsp.api.context.DspSupport;
import com.weiwan.dsp.api.enums.MetricKey;
import com.weiwan.dsp.api.enums.MetricType;
import com.weiwan.dsp.api.metrics.MetricCounter;
import com.weiwan.dsp.api.metrics.MetricGauge;
import com.weiwan.dsp.api.metrics.MetricMeter;

/**
 * @author: xiaozhennan
 * @description: metric操作中心,框架用来进行初始化和注册Metric
 */
public interface MetricCenter extends DspSupport {

    public void open(Object engineMetric);

    public void close();

    <T> T registerMetric(MetricKey metricId, Class<T> tClass);

    <T> T registerMetric(String metricId, Class<T> tClass);
}
