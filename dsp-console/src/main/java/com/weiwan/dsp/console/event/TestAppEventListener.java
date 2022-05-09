package com.weiwan.dsp.console.event;

import com.google.common.eventbus.Subscribe;
import com.weiwan.dsp.core.event.BusEventListener;

/**
 * @Author: xiaozhennan
 * @Date: 2022/4/9 13:01
 * @Package: com.weiwan.dsp.console.service.impl.deploy
 * @ClassName: TestAppEventListener
 * @Description:
 **/
public class TestAppEventListener implements BusEventListener<TestEvent.TestEventWarp, TestEvent.TestEventWarp> {
    @Override
    @Subscribe
    public void handleSuccessEvent(TestEvent.TestEventWarp eventWarp) {
        System.out.println("handleSuccessEvent" + eventWarp.getEvent());
    }

    @Override
    @Subscribe
    public void handleFailEvent(TestEvent.TestEventWarp failEventWarp) {
        System.out.println("handleFailEvent" + failEventWarp.getEvent());
    }
}
