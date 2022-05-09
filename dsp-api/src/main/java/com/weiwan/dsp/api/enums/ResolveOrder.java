package com.weiwan.dsp.api.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Map;

/**
 * @author: xiaozhennan
 * @date: 2021/6/3 16:57
 * @description: 类加载模式
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ResolveOrder implements Serializable {
    PARENT_FIRST("parent-first", 0),
    CHILD_FIRST("child-first", 1);
    private String type;
    private int code;
    ResolveOrder(String type, int code) {
        this.type = type;
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static ResolveOrder from(Map object){
        Integer code = (Integer) object.get("code");
        return getResolveOrder(code);
    }

    private static ResolveOrder getResolveOrder(Integer code) {
        ResolveOrder[] values = values();
        for (ResolveOrder value : values) {
            if(value.getCode() == code) {
                return value;
            }
        }
        return null;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ResolveOrder from(Object object){
        if(object instanceof Map){
            return from((Map) object);
        }
        if(object instanceof String){
            return from((String) object);
        }
        return null;
    }



    public static ResolveOrder from(String name){
        ResolveOrder[] values = values();
        for (ResolveOrder value : values) {
            if(value.type.equalsIgnoreCase(name)){
                return value;
            }
        }
        return null;
    }
}
