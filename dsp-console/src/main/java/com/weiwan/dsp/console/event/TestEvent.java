package com.weiwan.dsp.console.event;

import com.weiwan.dsp.core.event.BusEventWarp;
import com.weiwan.dsp.core.pub.BusEvent;

/**
 * @Author: xiaozhennan
 * @Date: 2022/4/9 13:08
 * @Package: com.weiwan.dsp.console.service.impl.deploy
 * @ClassName: TestEvent
 * @Description: AA
 **/
public class TestEvent implements BusEvent {


    public static class TestEventWarp extends BusEventWarp<TestEvent> {

        public TestEventWarp(TestEvent event) {
            super(event);
        }
    }
}
