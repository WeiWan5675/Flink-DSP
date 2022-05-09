package com.weiwan.dsp.core.pub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * @Author: xiaozhennan
 * @Date: 2021/8/18 23:34
 * @Package: com.weiwan.dsp.core.pub
 * @ClassName: PojoMapper
 * @Description: 一个简单的JSON对象解析器
 **/
public class PojoMapper {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        MAPPER.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        MAPPER.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
        MAPPER.configure(DeserializationFeature.ACCEPT_FLOAT_AS_INT, true);
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MAPPER.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
        MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        MAPPER.configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false);
        MAPPER.configure(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS, false);
        MAPPER.configure(SerializationFeature.WRITE_SELF_REFERENCES_AS_NULL, true);
    }

    public static <T> T json2Bean(String json, Class<T> tClass) {
        try {
            return MAPPER.readValue(json, tClass);
        } catch (JsonProcessingException e) {
            System.err.println(e);
        }
        return null;
    }


    public static <T> T bean2Json(T obj) {
        try {
            return (T) MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            System.err.println(e);
        }
        return null;
    }

}
