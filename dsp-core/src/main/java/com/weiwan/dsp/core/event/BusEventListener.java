package com.weiwan.dsp.core.event;


/**
 * @Author: xiaozhennan
 * @Date: 2021/10/18 22:15
 * @Package: com.weiwan.dsp.console.event
 * @ClassName: BusEventListener
 * @Description: 需要手动在实现的方法上添加{@link com.google.common.eventbus.Subscribe}
 **/
public interface BusEventListener<S extends EventWarp, F extends EventWarp> {


    default void handleSuccessEvent(S successEventWarp) {

    }


    default void handleFailEvent(F failEventWarp){

    }
}
