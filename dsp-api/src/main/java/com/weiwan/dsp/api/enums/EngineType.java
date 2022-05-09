package com.weiwan.dsp.api.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Map;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/30 15:01
 * @description:
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum EngineType implements Serializable {
    FLINK("com.weiwan.dsp.core.engine.flink.FlinkJobFlowEngine", 1, "FLINK"),
    SPARK("com.weiwan.dsp.core.engine.spark.SparkJobFlowEngine", 2, "SPARK"),
    LOCAL("com.weiwan.dsp.core.engine.local.LocalJobFlowEngine", 3, "LOCAL");


    private final String type;
    private final String engineClass;
    private final int code;

    EngineType(String engineClass, int code, String type) {
        this.engineClass = engineClass;
        this.code = code;
        this.type = type;
    }



    public String getEngineName() {
        return this.name();
    }






    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static EngineType from(Object object) {
        if(object instanceof Map){
            return from((Map) object);
        }
        if(object instanceof String){
            return from((String) object);
        }
        return null;
    }

    public static EngineType from(String name) {
        EngineType[] values = values();
        for (EngineType value : values) {
            if (value.name().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
    }
    public static EngineType from(Map name) {
        Integer code = (Integer) name.get("code");
        EngineType engineType = getEngineType(code);
        return engineType;
    }

    public static EngineType getEngineType(Integer code){
        if(code == null) return null;
        EngineType[] values = values();
        for (EngineType value : values) {
            if(value.code == code){
                return value;
            }
        }
        return null;
    }

    public String getEngineClass() {
        return engineClass;
    }

    public String getType() {
        return type;
    }

    public int getCode() {
        return code;
    }



}
