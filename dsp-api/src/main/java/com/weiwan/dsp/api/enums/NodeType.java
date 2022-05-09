package com.weiwan.dsp.api.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Map;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/2/10 14:25
 * @description:
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum NodeType {
    READER("Reader", 1), //reader插件
    WRITER("Writer", 2),  //writer插件
    PROCESS("Process", 3),  //process插件
    UNION("Union", 4), //合并节点
    SPLIT("Split", 5), //拆分节点
    UNKNOWN("Unknown", 6); //未知

    private final String type;
    private final int code;

    NodeType(String type, int code) {
        this.type = type;
        this.code = code;
    }


    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static NodeType from(Object object) {
        if(object instanceof Map){
            return from((Map) object);
        }
        if(object instanceof String){
            return from((String) object);
        }
        if(object instanceof Integer){
            return getNodeType((Integer) object);
        }
        return null;
    }

    public static NodeType from(String name) {
        NodeType[] values = values();
        for (NodeType value : values) {
            if (value.name().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
    }
    public static NodeType from(Map name) {
        Integer code = (Integer) name.get("code");
        NodeType engineType = getNodeType(code);
        return engineType;
    }

    private static NodeType getNodeType(Integer code) {
        NodeType[] values = values();
        for (NodeType value : values) {
            if(value.getCode() == code){
                return value;
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
