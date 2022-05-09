package com.weiwan.dsp.api.metrics;

/**
 * @author: xiaozhennan
 * @description:
 */
public interface MetricGauge<T> extends Metric{
    public T getGaugeValue();

    public void setGaugeValue(T t);

    public void updateGaugeValue(T t);

    void formula(CalculationFormula<T> calculationFormula);
}
