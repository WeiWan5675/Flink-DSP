package com.weiwan.dsp.api.metrics;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/15 10:01
 * @description:
 */
public interface MetricManager {


    MetricCounter registerCounter(String counter);

    MetricGauge registerGauge(String gauge);

    MetricMeter registerMeter(String meter);

}
