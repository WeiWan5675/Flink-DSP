package com.weiwan.dsp.core.engine.flink.metric;

import com.weiwan.dsp.api.metrics.CalculationFormula;
import com.weiwan.dsp.core.engine.metric.DspGauge;
import com.weiwan.dsp.core.engine.metric.DspMetric;
import org.apache.flink.metrics.Gauge;

/**
 * @author: xiaozhennan
 * @description:
 */
public class FlinkGauge<T> extends DspGauge<T> implements Gauge<T> {

    public FlinkGauge(DspMetric dspMetric) {
        super(dspMetric);
    }

    public FlinkGauge() {
    }

    @Override
    public T getGaugeValue() {
        return super.getGaugeValue();
    }

    @Override
    public void setGaugeValue(T s) {
        super.setGaugeValue(s);
    }

    @Override
    public void updateGaugeValue(T s) {
        super.updateGaugeValue(s);
    }

    @Override
    public T getValue() {
        return super.getGaugeValue();
    }
}
