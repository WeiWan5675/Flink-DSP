package com.weiwan.dsp.api.plugin;

import com.weiwan.dsp.common.config.AbstractConfig;

import java.io.Serializable;

/**
 * @author: xiaozhennan
 * @description:
 */
public interface DspControl<T extends AbstractConfig> extends Serializable {

    /**
     * 初始化插件
     *
     * @param config
     */
    void open(T config);


    /**
     * 关闭插件
     */
    void close();
}
