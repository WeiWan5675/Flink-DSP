package com.weiwan.dsp.api.metrics;

/**
 * @Author: xiaozhennan
 * @Date: 2021/10/27 18:05
 * @ClassName: CalculationFormula
 * @Description:
 **/
@FunctionalInterface
public interface CalculationFormula<T>  {

    T calculation();

}
