package com.weiwan.dsp.core.pub;

import com.weiwan.dsp.api.metrics.CalculationFormula;
import com.weiwan.dsp.api.metrics.MetricGauge;
import com.weiwan.dsp.core.engine.flink.metric.FlinkGauge;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * @Author: xiaozhennan
 * @Date: 2021/10/27 18:34
 * @ClassName: CalculationFormulaTest
 * @Description:
 **/
public class CalculationFormulaTest extends TestCase {


    @Test
    public void testFormula() {

        FlinkGauge flinkGauge = new FlinkGauge();
        int a = 15;
        int b = 20;
        flinkGauge.formula(new CalculationFormula() {
            @Override
            public Double calculation() {
                Integer c = b - a;
                return c.doubleValue();
            }
        });


        Object gaugeValue = flinkGauge.getGaugeValue();

        System.out.println(gaugeValue);

    }

    private Long l = 12L;
    @Test
    public void testEspCalculation() throws InterruptedException {

        MetricGauge<Long> flinkGauge = new FlinkGauge();

        flinkGauge.formula(new CalculationFormula<Long>() {
            private long lastTime;
            private long lastCount;
            @Override
            public Long calculation() {
                final long tmpC = l;
                final long tmpT = System.currentTimeMillis();
                long c = tmpC - lastCount;
                long t = tmpT - lastTime;
                if(c == 0) return 0L;
                Long d = t / c;
                lastCount = tmpC;
                lastTime = tmpT;
                return d;
            }
        });
        int i = 0;
        while (i < 10) {
            TimeUnit.SECONDS.sleep(3);
            Long gaugeValue = flinkGauge.getGaugeValue();
            System.out.println(gaugeValue);
            i++;
            l++;
        }


    }


    class Task extends Thread {
        private Long l1;

        public Task(Long l) {
            this.l1 = l;
        }

        @Override
        public void run() {
            l1++;
            try {
                TimeUnit.MILLISECONDS.sleep(500L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}