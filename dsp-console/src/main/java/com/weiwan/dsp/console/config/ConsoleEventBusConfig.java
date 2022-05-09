package com.weiwan.dsp.console.config;

import com.weiwan.dsp.console.event.ApplicationEventListener;
import com.weiwan.dsp.core.event.BusEventListener;
import com.weiwan.dsp.core.event.DspEventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: xiaozhennan
 * @Date: 2021/10/18 22:41
 * @Package: com.weiwan.dsp.console.config
 * @ClassName: ConsoleEventBusConfig
 * @Description: 控制台总线配置
 **/
@Configuration
public class ConsoleEventBusConfig {

    @Autowired
    private ApplicationEventListener applicationEventListener;

    @Bean
    public DspEventBus getConsoleEventBus(BusEventListener[] busEventListeners) {
        DspEventBus eventBus = DspEventBus.getEventBus();
        for (BusEventListener busEventListener : busEventListeners) {
            eventBus.registerListener(busEventListener);
        }
        return eventBus;
    }

}
