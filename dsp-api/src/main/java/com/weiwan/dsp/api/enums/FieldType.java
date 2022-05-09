package com.weiwan.dsp.api.enums;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author: xiaozhennan
 * @date: 2021/6/21 13:53
 * @description:
 */
public enum FieldType {

    //基本类型
    BYTE("java.lang.Byte", Byte.class),
    SHORT("java.lang.Short", Short.class),
    INTEGER("java.lang.Integer", Integer.class),
    LONG("java.lang.Long", Long.class),
    FLOAT("java.lang.Float", Float.class),
    DOUBLE("java.lang.Double", Double.class),
    BOOLEAN("java.lang.Boolean", Boolean.class),
    STRING("java.lang.String", String.class),

    //组合类型
    JSON_OBJECT("com.alibaba.fastjson.JSONObject", JSONObject.class),
    JSON_ARRAY("com.alibaba.fastjson.JSONArray", JSONArray.class),
    MAP("java.util.Map", Map.class),
    LIST("java.util.List", List.class),

    //高精度
    DECIMAL("java.math.BigDecimal", BigDecimal.class),


    //时间
    DATE("java.util.Date", Date.class),
    Time("java.sql.Time", java.sql.Time.class),
    Timestamp("java.sql.Timestamp", java.sql.Timestamp.class);

    private final Class<?> clazz;
    private final String className;

    FieldType(String className, Class<?> clazz) {
        this.className = className;
        this.clazz = clazz;
    }

    public static FieldType from(String tClass) {
        FieldType[] values = values();
        for (FieldType value : values) {
            if (value.className.equals(tClass)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.className;
    }

    public String getClassName() {
        return className;
    }

    public Class<?> getClazz() {
        return clazz;
    }
}
