package com.weiwan.dsp.api.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Map;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/3/21 15:54
 * @description:
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PluginType {
    INPUT("输入插件", 1),
    OUTPUT("输出插件", 2),
    PROCESS("处理插件", 3),
    SPLIT("拆分插件", 4),
    UNION("合并插件", 5),
    SYSTEM("系统插件", 6),
    UNKNOWN("未知插件", 7);

    private String type;
    private int code;

    PluginType(String type, int code) {
        this.type = type;
        this.code = code;
    }


    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static PluginType from(Object object) {
        if(object instanceof Map){
            return from((Map) object);
        }
        if(object instanceof String){
            return from((String) object);
        }
        return null;
    }

    public static PluginType from(String name) {
        PluginType[] values = values();
        for (PluginType value : values) {
            if (value.name().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
    }
    public static PluginType from(Map name) {
        Integer code = (Integer) name.get("code");
        PluginType engineType = getEngineType(code);
        return engineType;
    }

    public static PluginType getEngineType(Integer code){
        if(code == null) return null;
        PluginType[] values = values();
        for (PluginType value : values) {
            if(value.code == code){
                return value;
            }
        }
        return null;
    }

    public static PluginType getPluginType(int code) {
        PluginType[] values = values();
        if (code != 0) {
            for (PluginType value : values) {
                if (value.getCode() == code) {
                    return value;
                }
            }
        }
        return null;
    }

    public String getType() {
        return type;
    }

    public int getCode() {
        return code;
    }
}
