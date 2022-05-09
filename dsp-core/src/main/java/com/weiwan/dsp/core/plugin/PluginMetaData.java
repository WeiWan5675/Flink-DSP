package com.weiwan.dsp.core.plugin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.weiwan.dsp.api.plugin.PluginConfig;
import com.weiwan.dsp.api.enums.PluginJarOrigin;
import com.weiwan.dsp.api.enums.PluginType;

import java.io.Serializable;
import java.net.URL;
import java.util.Map;

/**
 * @author: xiaozhennan
 * @description:
 */
public class PluginMetaData implements Serializable {
    private static final long serialVersionUID = -5321418095255385700L;

    //构建时赋值
    private JSONObject metaJson;
    private URL pluginUrl;
    private String pluginName;
    private PluginType pluginType;
    private String pluginClass;
    private String pluginDescription;

    private Map<String, JSONObject> pluginConfigs;
    private Map<String, String> uiInfos;

    //校验通过标识
    private boolean checkPassed;

    //锁定的(锁定的插件不可以被卸载或者reload)
    private boolean blocked;

    //被禁用的插件不允许被使用
    private boolean disabled;

    //最后reload时间
    private long lastReloadTime;

    //加载时间
    private long loadTime;

    //加载类型 AUTO USER
    private PluginJarOrigin pluginJarOrigin;

    public PluginMetaData(JSONObject metaJson) {
        this.metaJson = metaJson;
        this.pluginJarOrigin = PluginJarOrigin.USER;
    }

    public PluginMetaData() {
    }

    public PluginMetaData(JSONObject metaJson, URL url) {
        this(metaJson);
        this.pluginUrl = url;
        //开始进行元数据准备
        this.pluginClass = metaJson.getString(PluginConfig.PLUGIN_CLASS.key());
        this.pluginName = metaJson.getString(PluginConfig.PLUGIN_NAME.key());
        this.pluginType = PluginType.from(metaJson.getString(PluginConfig.PLUGIN_TYPE.key()));
        this.pluginDescription = metaJson.getString(PluginConfig.PLUGIN_DESCRIPTION.key());
        this.pluginConfigs = (Map) metaJson.getJSONObject(PluginConfig.PLUGIN_CONFIGS.key());
        this.uiInfos = (Map) metaJson.getJSONObject(PluginConfig.PLUGIN_UI_INFOS.key());
    }

    public URL getPluginUrl() {
        return pluginUrl;
    }

    public void setPluginUrl(URL pluginUrl) {
        this.pluginUrl = pluginUrl;
    }

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public String getPluginClass() {
        return pluginClass;
    }

    public void setPluginClass(String pluginClass) {
        this.pluginClass = pluginClass;
    }

    public String getPluginDescription() {
        return pluginDescription;
    }

    public void setPluginDescription(String pluginDescription) {
        this.pluginDescription = pluginDescription;
    }

    public Map<String, JSONObject> getPluginConfigs() {
        return pluginConfigs;
    }

    public void setPluginConfigs(Map<String, JSONObject> pluginConfigs) {
        this.pluginConfigs = pluginConfigs;
    }

    public Map<String, String> getUiInfos() {
        return uiInfos;
    }

    public void setUiInfos(Map<String, String> uiInfos) {
        this.uiInfos = uiInfos;
    }

    public PluginType getPluginType() {
        return pluginType;
    }

    public void setPluginType(PluginType pluginType) {
        this.pluginType = pluginType;
    }

    public long getLastReloadTime() {
        return lastReloadTime;
    }

    public void setLastReloadTime(long lastReloadTime) {
        this.lastReloadTime = lastReloadTime;
    }

    public long getLoadTime() {
        return loadTime;
    }

    public void setLoadTime(long loadTime) {
        this.loadTime = loadTime;
    }


    public JSONObject getMetaJson() {
        return metaJson;
    }

    public void setMetaJson(JSONObject metaJson) {
        this.metaJson = metaJson;
    }

    public boolean isCheckPassed() {
        return checkPassed;
    }

    public void setCheckPassed(boolean checkPassed) {
        this.checkPassed = checkPassed;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public PluginJarOrigin getPluginOrigin() {
        return pluginJarOrigin;
    }

    public void setPluginOrigin(PluginJarOrigin pluginJarOrigin) {
        this.pluginJarOrigin = pluginJarOrigin;
    }


    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }


}
