package com.weiwan.dsp.core.plugin;

import com.weiwan.dsp.api.plugin.PluginConfig;
import com.weiwan.dsp.api.plugin.Plugin;

/**
 * @Author: xiaozhennan
 * @Date: 2021/8/18 23:52
 * @Package: com.weiwan.dsp.core.plugin
 * @ClassName: PluginInfo
 * @Description:
 **/
public class PluginInfo {
    private PluginConfig pluginConfig;
    private Class<? extends Plugin> pluginClass;
    public PluginInfo(PluginConfig pluginConfig, Class<? extends Plugin> aClass) {
        this.pluginConfig = pluginConfig;
        this.pluginClass = aClass;
    }
}
