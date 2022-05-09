package com.weiwan.dsp.core.engine.metric;

import com.weiwan.dsp.api.metrics.MetricCounter;
import org.apache.flink.metrics.Counter;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author: xiaozhennan
 * @description:
 */
public class DspCounter extends DspMetric implements MetricCounter {

    public DspCounter(DspMetric dspMetric) {
        super(dspMetric);
        this.atomicLong = new AtomicLong();
    }
    public DspCounter() {
    }
    private AtomicLong atomicLong = new AtomicLong();;

    public DspCounter(long initValue, DspMetric dspMetric) {
        this(dspMetric);
        this.atomicLong = new AtomicLong(initValue);
    }


    @Override
    public void inc() {
        atomicLong.incrementAndGet();
    }

    @Override
    public void inc(long n) {
        long l = atomicLong.get();
        atomicLong.set(l + n);
    }

    @Override
    public void dec() {
        atomicLong.decrementAndGet();
    }

    @Override
    public void dec(long n) {
        long l = atomicLong.get();
        atomicLong.set(l - n);
    }

    @Override
    public long getCount() {
        return atomicLong.get();
    }
}
