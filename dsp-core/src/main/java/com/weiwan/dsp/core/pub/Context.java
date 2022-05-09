package com.weiwan.dsp.core.pub;

import com.weiwan.dsp.common.config.ConfigOption;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: xiaozhennan
 * @description: 简单的Context封装
 */
public class Context {
    private final Map<Object, Object> map = new HashMap<>();

    public void setVal(ConfigOption<String> key, Object value) {
        map.put(key, value);
    }

    public void set(Object key, Object value) {
        map.put(key, value);
    }

    public <T> T getVal(ConfigOption<T> key) {
        return (T) map.getOrDefault(key, key.defaultValue());
    }

    public Object get(Object key) {
        return map.get(key);
    }

    public <T> T get(Object key, T defaultVar) {
        return (T) map.getOrDefault(key, defaultVar);
    }
}
