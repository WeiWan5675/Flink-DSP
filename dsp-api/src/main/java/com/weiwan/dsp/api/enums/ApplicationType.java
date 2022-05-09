package com.weiwan.dsp.api.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Map;

/**
 * @author: xiaozhennan
 * @description:
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ApplicationType {
    STREAM("流应用", 1),
    BATCH("批应用", 2),
    SQL("SQL应用", 3);

    private final int code;
    private final String type;

    ApplicationType(String type, int code) {
        this.type = type;
        this.code = code;
    }

    public static ApplicationType from(Integer code) {
        ApplicationType[] values = values();
        for (ApplicationType value : values) {
            if(value.code == code){
                return value;
            }
        }
        return null;
    }

    public static ApplicationType from(String name) {
        ApplicationType[] values = values();
        for (ApplicationType value : values) {
            if (value.name().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
    }

    public static ApplicationType from(Map object){
        ApplicationType[] values = values();
        Integer code = (Integer) object.get("code");
        return getResolveOrder(code);
    }

    private static ApplicationType getResolveOrder(Integer code) {
        ApplicationType[] values = values();
        for (ApplicationType value : values) {
            if(value.getCode() == code) {
                return value;
            }
        }
        return null;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ApplicationType from(Object object){
        if(object instanceof Map){
            return from((Map) object);
        }
        if(object instanceof String){
            return from((String) object);
        }
        if(object instanceof Integer){
            return from((Integer) object);
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public String getType() {
        return type;
    }
}
