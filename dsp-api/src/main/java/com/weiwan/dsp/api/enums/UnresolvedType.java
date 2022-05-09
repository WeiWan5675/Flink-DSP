package com.weiwan.dsp.api.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @Author: xiaozhennan
 * @Date: 2021/6/1 23:46
 * @Package: com.weiwan.dsp.api.enums
 * @ClassName: UnresolvedType
 * @Description:
 **/
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum UnresolvedType {
    //数据格式化错误导致未解析
    FORMAT,
    //程序异常未解析
    FAIL,
    //规则异常未解析
    RULE,
    //用户未解析
    USER,
    //未知未解析
    UNKNOWN;

    public static final String TYPE_PREFIX_USER = "USER.";
    public static final String TYPE_PREFIX_SYSTEM = "SYSTEM.";

    @JsonCreator
    public static UnresolvedType from(String name) {
        if (name == null) return null;
        UnresolvedType[] values = values();
        for (UnresolvedType value : values) {
            if (value.name().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
    }


    public String getTypeName(String name) {
        if(name == null){
            return TYPE_PREFIX_SYSTEM + UnresolvedType.UNKNOWN.name();
        }
        if(this == UnresolvedType.USER){
            return TYPE_PREFIX_USER + name;
        }
        return TYPE_PREFIX_SYSTEM + name;
    }
}
