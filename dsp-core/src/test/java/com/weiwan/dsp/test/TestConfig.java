package com.weiwan.dsp.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weiwan.dsp.api.config.core.DspContextConfig;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/5/26 18:46
 * @description:
 */
public class TestConfig {

    public static void main(String[] args) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
        objectMapper.configure(DeserializationFeature.ACCEPT_FLOAT_AS_INT, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);

        DspContextConfig config1 = objectMapper.readValue(new File("D:\\develop\\github\\Flink-DSP\\dsp-core\\src\\main\\resources\\example\\job-flow.json"), DspContextConfig.class);
        DspContextConfig config2 = objectMapper.readValue(new File("./conf/example/job-flow.json"), DspContextConfig.class);

        Map map = beanToMap(config1);
        Map map1 = beanToMap(config2);

        merge(map, map1);

        System.out.println(map);
//        System.out.println(dspContextConfig);

        System.out.println(config1);
    }

    public static void merge(Map oldMap, Map newMap) {
        for (Object newKey : newMap.keySet()) {
            Object o = oldMap.get(newKey);
            //不存在,直接放入
            if (o == null) oldMap.put(newKey, newMap.get(newKey));

            if (o instanceof Map) {
                merge((Map<String, Object>) o, (Map<String, Object>) newMap.get(newKey));
            } else if (o instanceof List) {
                continue;
            } else {
                if (newMap.get(newKey) != null) {
                    oldMap.put(newKey, newMap.get(newKey));
                }
            }
        }
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

}
