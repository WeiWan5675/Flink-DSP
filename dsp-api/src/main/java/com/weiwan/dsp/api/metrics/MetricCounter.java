package com.weiwan.dsp.api.metrics;

/**
 * @author: xiaozhennan
 * @description:
 */
public interface MetricCounter extends Metric{

    void inc();

    /**
     * Increment the current count by the given value.
     *
     * @param n value to increment the current count by
     */
    void inc(long n);

    /** Decrement the current count by 1. */
    void dec();

    /**
     * Decrement the current count by the given value.
     *
     * @param n value to decrement the current count by
     */
    void dec(long n);

    /**
     * Returns the current count.
     *
     * @return current count
     */
    long getCount();
}
