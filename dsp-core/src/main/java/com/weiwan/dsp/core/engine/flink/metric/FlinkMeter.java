package com.weiwan.dsp.core.engine.flink.metric;

import com.weiwan.dsp.core.engine.metric.DspMeter;
import com.weiwan.dsp.core.engine.metric.DspMetric;
import org.apache.flink.metrics.*;

/**
 * @author: xiaozhennan
 * @description:
 */
public class FlinkMeter extends DspMeter implements Meter, View {

    public FlinkMeter(DspMetric dspMetric) {
        super(dspMetric);
    }

    public FlinkMeter() {

    }

    private static final int DEFAULT_TIME_SPAN_IN_SECONDS = 60;

    /** The underlying counter maintaining the count. */
    private Counter counter;
    /** The time-span over which the average is calculated. */
    private int timeSpanInSeconds;
    /** Circular array containing the history of values. */
    private long[] values;
    /** The index in the array for the current time. */
    private int time = 0;
    /** The last rate we computed. */
    private double currentRate = 0;

    public FlinkMeter(int timeSpanInSeconds) {
        this(new SimpleCounter(), timeSpanInSeconds);
    }

    public FlinkMeter(Counter counter) {
        this(counter, DEFAULT_TIME_SPAN_IN_SECONDS);
    }

    public FlinkMeter(Counter counter, int timeSpanInSeconds) {
        this.counter = counter;
        // the time-span must be larger than the update-interval as otherwise the array has a size
        // of 1,
        // for which no rate can be computed as no distinct before/after measurement exists.
        this.timeSpanInSeconds =
                Math.max(
                        timeSpanInSeconds - (timeSpanInSeconds % UPDATE_INTERVAL_SECONDS),
                        UPDATE_INTERVAL_SECONDS);
        this.values = new long[this.timeSpanInSeconds / UPDATE_INTERVAL_SECONDS + 1];
    }

    @Override
    public void markEvent() {
        this.counter.inc();
    }

    @Override
    public void markEvent(long n) {
        this.counter.inc(n);
    }

    @Override
    public long getCount() {
        return counter.getCount();
    }

    @Override
    public double getRate() {
        return currentRate;
    }

    @Override
    public void update() {
        time = (time + 1) % values.length;
        values[time] = counter.getCount();
        currentRate =
                ((double) (values[time] - values[(time + 1) % values.length]) / timeSpanInSeconds);
    }
}
