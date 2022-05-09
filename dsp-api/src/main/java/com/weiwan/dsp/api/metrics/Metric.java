package com.weiwan.dsp.api.metrics;

import com.weiwan.dsp.api.enums.MetricKey;

/**
 * @Author: xiaozhennan
 * @Date: 2021/10/27 16:21
 * @ClassName: Metric
 * @Description:
 **/
public interface Metric {
    String getMetricId(String key);

    String getMetricId(MetricKey key);
}
