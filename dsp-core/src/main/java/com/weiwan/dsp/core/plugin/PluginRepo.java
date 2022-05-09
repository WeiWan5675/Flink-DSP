package com.weiwan.dsp.core.plugin;

import com.weiwan.dsp.api.plugin.Plugin;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: xiaozhennan
 * @date: 2021/6/15 10:25
 * @description:
 */
public class PluginRepo extends ConcurrentHashMap<String, Class<? extends Plugin>> {

    public void addPlugin(Class<? extends Plugin> pluginClass) {
        this.put(pluginClass.getName(), pluginClass);
    }

    public Class<? extends Plugin> getPlugin(String pluginClassName) {
        return this.get(pluginClassName);
    }
}
