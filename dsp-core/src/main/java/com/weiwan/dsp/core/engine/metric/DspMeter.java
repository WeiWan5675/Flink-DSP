package com.weiwan.dsp.core.engine.metric;

import com.weiwan.dsp.api.metrics.MetricMeter;

/**
 * @author: xiaozhennan
 * @description:
 */
public class DspMeter extends DspMetric implements MetricMeter {
    public DspMeter(DspMetric dspMetric) {
        super(dspMetric);
    }

    public DspMeter() {
    }
}
