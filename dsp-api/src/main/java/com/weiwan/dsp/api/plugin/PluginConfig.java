package com.weiwan.dsp.api.plugin;

import com.alibaba.fastjson.JSONObject;
import com.weiwan.dsp.api.config.core.Options;
import com.weiwan.dsp.api.config.core.PluginConfigs;
import com.weiwan.dsp.api.enums.PluginType;
import com.weiwan.dsp.common.config.ConfigOption;
import com.weiwan.dsp.common.config.ConfigOptions;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/2/20 11:34
 * @description: 这是一个插件配置类, 如果需要写自己的插件, 需要提供一个插件配置类
 */
public class PluginConfig implements Options, Serializable {

    public static final ConfigOption<String> PLUGIN_ID = ConfigOptions.key("pluginId")
            .required(true).description("插件Id").ok(String.class);

    public static final ConfigOption<String> PLUGIN_NAME = ConfigOptions.key("pluginName")
            .required(true).description("插件名称").ok(String.class);

    public static final ConfigOption<PluginType> PLUGIN_TYPE = ConfigOptions.key("pluginType")
            .required(true).description("插件类型").ok(PluginType.class);

    public static final ConfigOption<String> PLUGIN_CLASS = ConfigOptions.key("pluginClass")
            .required(true).description("插件ClassName").ok(String.class);

    public static final ConfigOption<String> PLUGIN_DESCRIPTION = ConfigOptions.key("pluginDescription")
            .required(false).description("插件说明").ok(String.class);

    public static final ConfigOption<PluginConfigs> PLUGIN_CONFIGS = ConfigOptions.key("pluginConfigs")
            .required(false).description("插件配置项").ok(PluginConfigs.class);

    public static final ConfigOption<Map> PLUGIN_UI_INFOS = ConfigOptions.key("pluginInfos")
            .required(false).description("插件UI信息").ok(Map.class);


    private String pluginId;
    private String pluginJarId;
    private String pluginName;
    private PluginType pluginType;
    private String pluginClass;
    private String pluginDescription;
    private PluginConfigs pluginConfigs;
    private JSONObject pluginInfos;


    public String getPluginJarId() {
        return pluginJarId;
    }

    public void setPluginJarId(String pluginJarId) {
        this.pluginJarId = pluginJarId;
    }

    public String getPluginId() {
        return pluginId;
    }

    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }


    public void setPluginClass(String pluginClass) {
        this.pluginClass = pluginClass;
    }

    public void setPluginDescription(String pluginDescription) {
        this.pluginDescription = pluginDescription;
    }


    public PluginConfigs getPluginConfigs() {
        return this.pluginConfigs;
    }


    public void setPluginConfigs(PluginConfigs pluginConfigs) {
        this.pluginConfigs = pluginConfigs;
    }


    public JSONObject getPluginInfos() {
        return pluginInfos;
    }

    public void setPluginInfos(JSONObject pluginInfos) {
        this.pluginInfos = pluginInfos;
    }

    public PluginConfig(Map m) {
        this.pluginConfigs = new PluginConfigs(m);
    }

    public PluginConfig() {
    }

    public String getPluginName() {
        return pluginName;
    }


    public String getPluginClass() {
        return pluginClass;
    }

    public String getPluginDescription() {
        return pluginDescription;
    }

    public PluginType getPluginType() {
        return pluginType;
    }

    public void setPluginType(PluginType pluginType) {
        this.pluginType = pluginType;
    }


    public <T> T getVal(ConfigOption<T> configOption) {
        if (this.pluginConfigs != null) {
            return this.pluginConfigs.getVal(configOption);
        }
        return null;
    }


    public <T> void setVal(ConfigOption<T> key, T value) {
        if (this.pluginConfigs != null) {
            this.pluginConfigs.setVal(key, value);
        }
    }

    public <T> T getVal(String key, Class<T> tClass) {
        if(key != null && tClass != null){
            if(this.pluginConfigs != null){
                return this.pluginConfigs.getVal(key, tClass);
            }
        }
        return null;
    }
}
