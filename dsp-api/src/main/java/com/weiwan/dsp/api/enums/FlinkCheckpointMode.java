package com.weiwan.dsp.api.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Map;

/**
 * @Author: xiaozhennan
 * @Date: 2021/10/20 20:44
 * @ClassName: FlinkCheckpointMode
 * @Description:
 **/
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum FlinkCheckpointMode {
    AT_LEAST_ONCE("at_least_once", 1),
    EXACTLY_ONCE("exactly_once", 2);


    private final String type;
    private final int code;

    FlinkCheckpointMode(String type, int code) {
        this.type = type;
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String getType() {
        return type;
    }

    public static FlinkCheckpointMode from(String name) {
        FlinkCheckpointMode[] values = values();
        for (FlinkCheckpointMode value : values) {
            if (value.name().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
    }

    public static FlinkCheckpointMode from(Map object){
        FlinkCheckpointMode[] values = values();
        Integer code = (Integer) object.get("code");
        return getResolveOrder(code);
    }

    private static FlinkCheckpointMode getResolveOrder(Integer code) {
        FlinkCheckpointMode[] values = values();
        for (FlinkCheckpointMode value : values) {
            if(value.getCode() == code) {
                return value;
            }
        }
        return null;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static FlinkCheckpointMode from(Object object){
        if(object instanceof Map){
            return from((Map) object);
        }
        if(object instanceof String){
            return from((String) object);
        }
        return null;
    }



}
