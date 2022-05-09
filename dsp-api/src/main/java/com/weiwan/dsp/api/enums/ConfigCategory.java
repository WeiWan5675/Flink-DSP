package com.weiwan.dsp.api.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Map;

/**
 * @author: xiaozhennan
 * @date: 2021/6/21 16:02
 * @description:
 */
public enum ConfigCategory {
    DATABASE("Database", 1),
    SERVICE("Service", 2),
    PLUGIN("Plugin", 3),
    OTHER("Other", 4);
    private String type;
    private Integer code;

    ConfigCategory(String type, Integer code) {
        this.type = type;
        this.code = code;
    }


    @Override
    public String toString() {
        return this.name();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public static ConfigCategory from(Integer code) {
        if (code == null) return ConfigCategory.OTHER;
        ConfigCategory[] values = values();
        for (ConfigCategory value : values) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ConfigCategory from(Object object) {
        if (object instanceof Map) {
            return from((Map) object);
        }
        if (object instanceof String) {
            return from((String) object);
        }
        if (object instanceof Integer) {
            return from((Integer) object);
        }
        return OTHER;
    }

    public static ConfigCategory from(String name) {
        ConfigCategory[] values = values();
        for (ConfigCategory value : values) {
            if (value.name().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
    }


    public static ConfigCategory from(Map<String, String> name) {
        Integer code = Integer.parseInt(name.get("code"));
        ConfigCategory configCategory = getConfigCategory(code);
        return configCategory;
    }

    public static ConfigCategory getConfigCategory(Integer code) {
        ConfigCategory[] values = values();
        for (ConfigCategory value : values) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }


}
