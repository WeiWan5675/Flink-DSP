package com.weiwan.dsp.core.event;

import com.weiwan.dsp.core.pub.BusEvent;

/**
 * @Author: xiaozhennan
 * @Date: 2021/10/18 22:13
 * @Package: com.weiwan.dsp.console.event
 * @ClassName: EventWarp
 * @Description:
 **/
public interface EventWarp<E extends BusEvent> {
    E getEvent();

    Throwable getThrowable();
}
