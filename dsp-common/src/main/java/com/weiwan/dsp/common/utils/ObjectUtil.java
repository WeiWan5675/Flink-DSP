package com.weiwan.dsp.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.weiwan.dsp.common.config.AbstractConfig;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Map;

/**
 * @Author: xiaozhennan
 * @Date: 2021/10/15 0:10
 * @Package: com.weiwan.dsp.common.utils
 * @ClassName: ObjectUtil
 * @Description: 对象工具类
 **/
public class ObjectUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        MAPPER.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, true);
        MAPPER.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        MAPPER.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
        MAPPER.configure(DeserializationFeature.ACCEPT_FLOAT_AS_INT, true);
        MAPPER.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, true);
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MAPPER.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
        MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        MAPPER.configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false);
        MAPPER.configure(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS, false);
        MAPPER.configure(SerializationFeature.WRITE_SELF_REFERENCES_AS_NULL, true);
    }


    public static <T extends Serializable> String objectToContent(T t) throws JsonProcessingException {
        return MAPPER.writeValueAsString(t);
    }

    public static <T extends Serializable> T contentToObject(String content, Class<T> type) throws JsonProcessingException {
        return MAPPER.readValue(content, type);
    }


    public static <T extends Serializable> String serialize(T obj) {
        if (obj != null) {
            try {
                return objectToContent(obj);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static <T extends Serializable> T deSerialize(String json, Class<T> type) {
        if (StringUtils.isNotBlank(json)) {
            try {
                return contentToObject(json, type);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 对象转Map
     *
     * @param object
     * @return
     */
    public static Map beanToMap(Object object) {
        return JSONObject.parseObject(JSON.toJSONString(object), Map.class);
    }

    /**
     * map转对象
     *
     * @param map
     * @param beanClass
     * @param <T>
     * @return
     */
    public static <T> T mapToBean(Map map, Class<T> beanClass) {
        return JSONObject.parseObject(JSON.toJSONString(map), beanClass);
    }


    public static <T> T mergeObjects(T srcObj, T dscObj) {
        JSONObject jsonObject = new JSONObject();
        if (srcObj != null) AbstractConfig.merge(jsonObject, ObjectUtil.beanToMap(srcObj));
        if (dscObj != null) AbstractConfig.merge(jsonObject, ObjectUtil.beanToMap(dscObj));
        return (T) ObjectUtil.mapToBean(jsonObject, srcObj.getClass());
    }


}
