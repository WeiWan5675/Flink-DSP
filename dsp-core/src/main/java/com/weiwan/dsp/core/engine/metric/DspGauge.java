package com.weiwan.dsp.core.engine.metric;

import com.weiwan.dsp.api.metrics.CalculationFormula;
import com.weiwan.dsp.api.metrics.MetricGauge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: xiaozhennan
 * @description:
 */
public class DspGauge<T> extends DspMetric implements MetricGauge<T> {
    public DspGauge(DspMetric dspMetric) {
        super(dspMetric);
    }
    private Logger logger = LoggerFactory.getLogger(DspGauge.class);
    public DspGauge() {
    }

    protected CalculationFormula<T> formula;

    protected T t;


    @Override
    public T getGaugeValue() {
        if(formula != null){
            T calculation = formula.calculation();
            return calculation;
        }
        return this.t;
    }

    @Override
    public void setGaugeValue(T t) {
        this.t = t;
    }

    @Override
    public void updateGaugeValue(T t) {
        this.setGaugeValue(t);
    }

    @Override
    public void formula(CalculationFormula<T> calculationFormula) {
        this.formula = calculationFormula;
    }
}
