package com.weiwan.dsp.api.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Map;

/**
 * @author: xiaozhennan
 * @description:
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PluginJarOrigin implements Serializable {
    SYSTEM("System", 1),
    USER("User", 2);

    private final int code;
    private final String type;

    PluginJarOrigin(String type, int code) {
        this.type = type;
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String getType() {
        return type;
    }


    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static PluginJarOrigin from(Object object) {
        if(object instanceof Map){
            return from((Map) object);
        }
        if(object instanceof String){
            return from((String) object);
        }
        return null;
    }

    public static PluginJarOrigin from(String name) {
        PluginJarOrigin[] values = values();
        for (PluginJarOrigin value : values) {
            if (value.name().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
    }
    public static PluginJarOrigin from(Map name) {
        Integer code = (Integer) name.get("code");
        PluginJarOrigin pluginJarOrigin = getPluginJarOrigin(code);
        return pluginJarOrigin;
    }

    public static PluginJarOrigin getPluginJarOrigin(Integer code){
        if(code == null) return null;
        PluginJarOrigin[] values = values();
        for (PluginJarOrigin value : values) {
            if(value.code == code){
                return value;
            }
        }
        return null;
    }
}
