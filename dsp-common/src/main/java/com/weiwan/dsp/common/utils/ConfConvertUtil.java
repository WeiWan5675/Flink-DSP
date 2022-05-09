package com.weiwan.dsp.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/14 11:05
 * @description: json & yaml & properties 互相转换
 */
public class ConfConvertUtil {


    /**
     * 转换Yaml字符串到JSON字符串
     * @param yamlStr
     * @return
     */
    public static String convertYaml2Json(String yamlStr) {
        ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
        Object obj = null;
        try {
            obj = yamlReader.readValue(yamlStr, Object.class);
            ObjectMapper jsonWriter = new ObjectMapper();
            return jsonWriter.writeValueAsString(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 转换Prop字符串到Json字符串
     * @param propStr
     * @return
     */
    public static String convertProp2Json(String propStr) {
        ObjectMapper yamlReader = new ObjectMapper(new JavaPropsFactory());
        Object obj = null;
        try {
            obj = yamlReader.readValue(propStr, Object.class);
            ObjectMapper jsonWriter = new ObjectMapper();
            return jsonWriter.writeValueAsString(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String convertJson2Yaml(String json){
        ObjectMapper jsonReader = new ObjectMapper();
        Object obj = null;
        try {
            obj = jsonReader.readValue(json, Object.class);
            ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
            return objectMapper.writeValueAsString(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String convertJson2Prop(String json){
        ObjectMapper jsonReader = new ObjectMapper();
        Object obj = null;
        try {
            obj = jsonReader.readValue(json, Object.class);
            ObjectMapper objectMapper = new ObjectMapper(new JavaPropsFactory());
            return objectMapper.writeValueAsString(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
