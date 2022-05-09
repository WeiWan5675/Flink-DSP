package com.weiwan.dsp.api.config.core;

import java.io.Serializable;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/5/26 15:28
 * @description:
 */
public class DspContextConfig implements Serializable {

    private DspConfig dsp;

    public DspConfig getDsp() {
        return dsp;
    }

    public void setDsp(DspConfig dsp) {
        this.dsp = dsp;
    }

    public DspContextConfig(DspConfig dsp) {
        this.dsp = dsp;
    }

    public DspContextConfig() {
    }
}
