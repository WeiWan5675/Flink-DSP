package com.weiwan.dsp.console.service.impl.deploy;

import com.weiwan.dsp.console.event.ApplicationEventListener;
import com.weiwan.dsp.console.event.TestAppEventListener;
import com.weiwan.dsp.console.event.TestEvent;
import com.weiwan.dsp.core.event.DspEventBus;
import junit.framework.TestCase;
import org.junit.Test;

/**
 * @Author: xiaozhennan
 * @Date: 2021/10/18 17:29
 * @Package: com.weiwan.dsp.console.service.impl.deploy
 * @ClassName: DspEventBusTest
 * @Description:
 **/
public class DspEventBusTest extends TestCase {


    @Test
    public void testEventBus() {

        DspEventBus.registerApplicationListener(new TestAppEventListener());
        DspEventBus.registerApplicationListener(new ApplicationEventListener());
        ApplicationEvent applicationEvent1 = new StartApplicationEvent(null, null);
        ApplicationEvent applicationEvent2 = new StopApplicationEvent(null, null);
        TestEvent testEvent = new TestEvent();
        ApplicationEvent.ApplicationSuccessEvent successEventWarp = new ApplicationEvent.ApplicationSuccessEvent(applicationEvent1);
        ApplicationEvent.ApplicationFailEvent failEvent = new ApplicationEvent.ApplicationFailEvent(applicationEvent2, null);
        TestEvent.TestEventWarp testEventWarp = new TestEvent.TestEventWarp(testEvent);
        DspEventBus.pushEvent(successEventWarp);
        DspEventBus.pushEvent(failEvent);
        DspEventBus.pushEvent(testEventWarp);
    }

}