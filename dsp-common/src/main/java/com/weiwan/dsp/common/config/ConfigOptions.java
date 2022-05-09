package com.weiwan.dsp.common.config;

import com.alibaba.fastjson.TypeReference;

import java.io.Serializable;
import java.util.List;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/3/21 15:24
 * @description:
 */
public final class ConfigOptions implements Serializable {
    private String key;
    private String fullKey;
    private Object value;
    private String description;
    private Object defaultValue;
    private Object[] optionals;
    private boolean required;
    private boolean password;
    private boolean textarea;

    private ConfigOptions() {
    }


    public static ConfigOptions key(String key) {
        return newOption(key);
    }

    public ConfigOptions fullKey(String fullKey) {
        this.fullKey = fullKey;
        return this;
    }

    public <T> ConfigOptions value(T var) {
        this.value = var;
        return this;
    }

    public <T> ConfigOptions defaultValue(T defaultVar) {
        this.defaultValue = defaultVar;
        return this;
    }

    public ConfigOptions required(boolean required) {
        this.required = required;
        return this;
    }

    public ConfigOptions password(boolean password) {
        this.password = password;
        return this;
    }

    public ConfigOptions textarea(boolean textarea) {
        this.textarea = textarea;
        return this;
    }

    public ConfigOptions optionals(Object... optionals) {
        this.optionals = optionals;
        return this;
    }

    public ConfigOptions description(String description) {
        this.description = description;
        return this;
    }

    public <T extends Object> ConfigOption<T> ok(Class<T> tClass) {
        return (ConfigOption<T>) new ConfigOption<T>(this.key, this.fullKey, this.value, this.defaultValue, this.required, this.description, this.optionals, tClass, this.password, this.textarea);
    }

    private static ConfigOptions newOption(String key) {
        ConfigOptions configOptions = new ConfigOptions();
        configOptions.key = key;
        return configOptions;
    }


    /**
     * 需要手动设置数据
     *
     * @param option
     * @param <T>
     * @return
     */
    public static <T> ConfigOption<T> from(ConfigOption<T> option, T newValue) {
        return ConfigOptions.key(option.key())
                .fullKey(option.fullKey())
                .description(option.description())
                .defaultValue(option.defaultValue())
                .value(newValue)
                .optionals(option.optionals())
                .required(option.required())
                .ok(option.type());
    }

}
