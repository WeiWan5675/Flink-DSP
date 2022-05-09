package com.weiwan.dsp.core.event;

import com.google.common.eventbus.EventBus;
import com.weiwan.dsp.common.utils.CheckTool;
import com.weiwan.dsp.core.pub.BusEvent;

/**
 * @Author: xiaozhennan
 * @Date: 2021/10/18 17:09
 * @Package: com.weiwan.dsp.core.event
 * @ClassName: DspEventBus
 * @Description: 应用事件总线
 **/
public class DspEventBus {

    private static DspEventBus eventBus;
    private EventBus bus = new EventBus("DspEventBus");

    private DspEventBus() {
    }

    public static DspEventBus getEventBus() {
        if (eventBus == null) {
            synchronized (DspEventBus.class) {
                if (eventBus == null) {
                    eventBus = new DspEventBus();
                    eventBus.init();
                }
            }
        }
        return eventBus;
    }

    private void init() {

    }

    public static <S extends EventWarp,F extends EventWarp> void registerApplicationListener(BusEventListener<S,F> eventListener) {
        CheckTool.checkNotNull(eventListener, "EventListener can not be empty");
        DspEventBus eventBus = DspEventBus.getEventBus();
        eventBus.registerListener(eventListener);
    }

    public static <E extends BusEvent> void pushEvent(EventWarp<E> event) {
        DspEventBus eventBus = DspEventBus.getEventBus();
        CheckTool.checkNotNull(event, "ApplicationEvent can not be empty");
        eventBus.postEvent(event);
    }

    public static <S extends EventWarp,F extends EventWarp> void unregisterApplicationListener(BusEventListener<S,F> eventListener) {
        CheckTool.checkNotNull(eventListener, "EventListener can not be empty");
        DspEventBus eventBus = DspEventBus.getEventBus();
        eventBus.unregisterListener(eventListener);
    }

    public <E> void postEvent(E event) {
        bus.post(event);

    }

    public <L> void registerListener(L listener) {
        bus.register(listener);
    }

    public <T> void unregisterListener(T listener) {
        if (listener != null) {
            bus.unregister(listener);
        }
    }
}
