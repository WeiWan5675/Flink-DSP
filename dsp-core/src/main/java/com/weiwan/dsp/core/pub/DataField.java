package com.weiwan.dsp.core.pub;

import com.alibaba.fastjson.JSONObject;
import com.weiwan.dsp.api.enums.FieldType;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/3/26 10:26
 * @description:
 */
public class DataField implements Serializable {
    private String fieldName;
    private FieldType fieldType;
    private Object fieldValue;

    public DataField() {
    }

    public DataField(String fieldName, FieldType fieldType) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
    }

    public DataField(String fieldName, FieldType fieldType, Object fieldValue) {
        this(fieldName, fieldType);
        this.fieldValue = fieldValue;
    }

    public DataField(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataField dataField = (DataField) o;
        return Objects.equals(fieldName, dataField.fieldName) && fieldType == dataField.fieldType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldName, fieldType);
    }


    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }


    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    public Object getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(Object fieldValue) {
        this.fieldValue = fieldValue;
    }

    public <T> T getFieldValue(Class<T> tClass) {
        if (this.fieldType.getClazz() == tClass) {
            return (T) fieldValue;
        }
        return null;
    }

}
