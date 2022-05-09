package com.weiwan.dsp.common.config;

import com.alibaba.fastjson.JSONObject;
import com.weiwan.dsp.common.utils.CheckTool;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/3/21 14:36
 * @description:
 */
public class ConfigOption<T> implements Serializable {
    private String fullKey;
    private String key;
    private T value;
    private T defaultValue;
    T[] optionals;
    private boolean required;
    private String description;
    private Class<T> type;
    private boolean password;
    private boolean textarea;

    public ConfigOption(String key, String fullKey, Object value, Object defaultValue, boolean required, String description, Object[] optionals, Class<T> clazz, boolean password, boolean textarea) {
        this.key = key;
        if (fullKey == null) {
            this.fullKey = key;
        } else {
            this.fullKey = fullKey;
        }
        this.value = (T) value;
        this.defaultValue = (T) defaultValue;
        this.type = clazz;
        this.optionals = (T[]) optionals;
        this.required = required;
        this.description = description;
        this.password = password;
        this.textarea = textarea;
    }

    private ConfigOption() {
    }


    public String key() {
        return key;
    }

    public T value() {
        if (value == null) {
            return defaultValue;
        }
        return (T) value;
    }

    public String description() {
        return description;
    }

    public Class<T> type() {
        return type;
    }

    public T defaultValue() {
        return (T) defaultValue;
    }

    public T[] optionals() {
        return this.optionals;
    }

    public String fullKey() {
        return fullKey;
    }

    public boolean required() {
        return required;
    }

    public boolean password() {
        return password;
    }

    public void setPassword(boolean password) {
        this.password = password;
    }

    public boolean textarea() {
        return textarea;
    }

    public void setTextarea(boolean textarea) {
        this.textarea = textarea;
    }

    public void setFullKey(String fullKey) {
        this.fullKey = fullKey;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public void setDefaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setOptionals(T[] optionals) {
        this.optionals = optionals;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(Class<T> type) {
        this.type = type;
    }


    public String getFullKey() {
        return fullKey;
    }

    public String getKey() {
        return key;
    }

    public T getValue() {
        return value;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public T[] getOptionals() {
        return optionals;
    }

    public boolean isRequired() {
        return required;
    }

    public String getDescription() {
        return description;
    }

    public Class<T> getType() {
        return type;
    }

    public boolean isPassword() {
        return password;
    }

    public boolean isTextarea() {
        return textarea;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> res = new HashMap();
        res.put("key", key);
        res.put("fullKey", fullKey);
        res.put("value", value);
        res.put("defaultValue", defaultValue);
        res.put("description", description);
        res.put("required", required);
        res.put("optionals", optionals);
        res.put("type", type.getName());
        res.put("password", password);
        res.put("textarea", textarea);
        return res;
    }


    public boolean check(T t) {
        if (t == null) {
            if (required) {
                new IllegalArgumentException(String.format("Parameter: [%s] cannot be empty, description: [%s]", this.key, this.description));
            } else {
                return true;
            }
        }
        return true;
    }
}
