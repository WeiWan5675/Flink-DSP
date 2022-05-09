package com.weiwan.dsp.core.event;

import com.weiwan.dsp.core.pub.BusEvent;

/**
 * @Author: xiaozhennan
 * @Date: 2021/10/18 22:11
 * @Package: com.weiwan.dsp.console.event
 * @ClassName: EventWarp
 * @Description:
 **/
public abstract class BusEventWarp<E extends BusEvent> implements EventWarp<E> {
    protected E event;
    protected Throwable throwable;
    public BusEventWarp(E event, Throwable throwable) {
        this(event);
        this.throwable = throwable;
    }

    public BusEventWarp(E event) {
        this.event = event;
    }

    @Override
    public E getEvent() {
        return event;
    }

    @Override
    public Throwable getThrowable() {
        return throwable;
    }
}
