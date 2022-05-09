package com.weiwan.dsp.api.context;

import java.io.Serializable;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/3/21 13:08
 * @description:
 */
public interface DspSupport extends Serializable {

    default EngineContext getContext(){
        throw new RuntimeException("This method must be implemented by the user before it can be called, otherwise it is regarded as illegal access");
    }

    default void setContext(EngineContext context){
        throw new RuntimeException("This method must be implemented by the user before it can be called, otherwise it is regarded as illegal access");
    }

}
