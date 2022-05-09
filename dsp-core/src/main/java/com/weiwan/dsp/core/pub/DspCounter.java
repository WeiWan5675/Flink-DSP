package com.weiwan.dsp.core.pub;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author: xiaozhennan
 * @date: 2021/6/2 13:34
 * @description: 一个简单的计数器
 */
public class DspCounter {

    private AtomicLong atomicLong = new AtomicLong(0);

    public long increment() {
        return atomicLong.incrementAndGet();
    }

    public long decrement() {
        return atomicLong.decrementAndGet();
    }

    public void clean() {
        atomicLong.set(0);
    }

    public long get() {
        return atomicLong.get();
    }

    public long add(long l) {
        return atomicLong.addAndGet(l);
    }

}
