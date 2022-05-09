package com.weiwan.dsp.core.engine.flink.metric;


import com.weiwan.dsp.api.enums.NodeType;
import com.weiwan.dsp.api.metrics.MetricCounter;
import com.weiwan.dsp.core.engine.metric.DspCounter;
import com.weiwan.dsp.core.engine.metric.DspMetric;
import org.apache.flink.metrics.Counter;

/**
 * @author: xiaozhennan
 * @description:
 */
public class FlinkCounter extends DspCounter implements Counter {

    public FlinkCounter(DspMetric dspMetric) {
        super(dspMetric);
    }

    public FlinkCounter() {

    }


    @Override
    public void inc() {
        super.inc();
    }

    @Override
    public void inc(long n) {
        super.inc(n);
    }

    @Override
    public void dec() {
        super.dec();
    }

    @Override
    public void dec(long n) {
        super.dec(n);
    }

    @Override
    public long getCount() {
        return super.getCount();
    }
}
