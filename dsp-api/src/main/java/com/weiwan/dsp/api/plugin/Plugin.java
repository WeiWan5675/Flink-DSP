package com.weiwan.dsp.api.plugin;

import java.io.Serializable;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/3/26 10:24
 * @description:
 */
public interface Plugin extends PluginControl, Serializable {


    PluginConfig getPluginConfig();

    /**
     * 插件管理器调用该方法进行配置的下发
     *
     * @param config
     */
    void setPluginConfig(PluginConfig config);

}
