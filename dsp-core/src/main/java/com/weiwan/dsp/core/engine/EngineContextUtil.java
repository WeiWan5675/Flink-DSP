package com.weiwan.dsp.core.engine;

import com.weiwan.dsp.api.config.core.DspConfig;
import com.weiwan.dsp.api.context.EngineContext;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/12 15:59
 * @description:
 */
public class EngineContextUtil {

    public static EngineContext buildContext(DspConfig dspConfig) {
        return new EngineRunContext(dspConfig);
    }

}
