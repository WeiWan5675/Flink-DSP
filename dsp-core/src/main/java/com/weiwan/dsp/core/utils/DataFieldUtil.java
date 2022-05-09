package com.weiwan.dsp.core.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.sun.istack.NotNull;
import com.weiwan.dsp.api.enums.FieldType;
import com.weiwan.dsp.core.pub.DataField;

/**
 * @author: xiaozhennan
 * @date: 2021/6/21 15:40
 * @description:
 */
public class DataFieldUtil {


    public <T> T convertType(@NotNull final Object obj, @NotNull final T tClass) {
        FieldType dscType = FieldType.from(tClass.getClass().getName());
        if (dscType == null) {
            return Convert.convertByClassName(FieldType.STRING.getClassName(), obj);
        } else {
            return Convert.convertByClassName(dscType.getClassName(), obj);
        }
    }

    public DataField convertFieldType(@NotNull final DataField dataField, @NotNull final FieldType fieldType) {
        FieldType originType = dataField.getFieldType();
        if (originType.equals(fieldType)) {
            return dataField;
        }
        Object obj = Convert.convertByClassName(fieldType.getClassName(), dataField.getFieldValue());
        DataField convertDataType = new DataField(dataField.getFieldName(), fieldType, obj);
        return convertDataType;
    }

    public DataField cloneField(DataField dataField) {
        return ObjectUtil.clone(dataField);
    }


}
