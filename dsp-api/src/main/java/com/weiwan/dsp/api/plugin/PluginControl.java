package com.weiwan.dsp.api.plugin;

import com.weiwan.dsp.api.enums.PluginType;

import java.io.Serializable;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/3/21 13:34
 * @description:
 */
public interface PluginControl extends Serializable {

    /**
     * 初始化插件
     *
     * @param config
     */
    void open(PluginConfig config);

    /**
     * 关闭插件
     */
    void close();

    String getPluginName();

    void setPluginName(String name);

    Integer getPluginId();

    void setPluginId(Integer id);

    PluginType getPluginType();

    void setPluginType(PluginType type);
}
