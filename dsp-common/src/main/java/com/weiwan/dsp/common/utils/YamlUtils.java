/*
 *      Copyright [2020] [xiaozhennan1995@gmail.com]
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *      http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.weiwan.dsp.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hanlex.Liu on 2019/8/15 14:31.
 * 功能描述 : yml配置文件读取工具,读取yml配置文件的工具类.可以实现将 server : port : portnumber : 8081 转换为 key为"server.port.portnumber",值为"8081"的Map集合
 */

public class YamlUtils {


    /**
     * 以指定class为定位,获取制定文件名的配置文件,并读取
     *
     * @param fileName 文件名为空,读取默认配置文件
     * @return
     */
    public static Map<String, String> getYamlByFileName(String fileName) {
        Map<String, String> result = new HashMap<String, String>();
        try {
            if (StringUtils.isBlank(fileName)) {
                throw new RuntimeException("必须指定一个配置文件!");
            }

            Yaml yaml = new Yaml();
            File file = new File(fileName);
            if (!file.exists()) {
                return result;
            }
            InputStream is = new FileInputStream(fileName);
            Map<String, Object> params = yaml.loadAs(is, Map.class);
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (entry.getValue() instanceof Map) {
                    eachYaml(entry.getKey(), (Map<String, Object>) entry.getValue(), result);
                } else {
                    if (entry.getValue() != null) {
                        result.put(entry.getKey(), entry.getValue().toString());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return result;
    }



    /**
     * 读取yml文件
     *
     * @param is 配置文件输入流
     * @return
     */
    public static Map<String, String> getYamlByStream(InputStream is) {
        Map<String, String> result = new HashMap<String, String>();
        try {
            Yaml yaml = new Yaml();
            Map<String, Object> params = yaml.loadAs(is, Map.class);
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (entry.getValue() instanceof Map) {
                    eachYaml(entry.getKey(), (Map<String, Object>) entry.getValue(), result);
                } else {
                    if (entry.getValue() != null) {
                        result.put(entry.getKey(), entry.getValue().toString());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 使用递归进行深度转换,将Map<String,Object>转换为Map<String,String>;
     *
     * @param key 父级key
     * @param map 父级entry
     */
    private static void eachYaml(String key, Map<String, Object> map, Map<String, String> result) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String newKey = "";
            if (StringUtils.isNotEmpty(key)) {
                newKey = (key + "." + entry.getKey());
            } else {
                newKey = entry.getKey();
            }
            if (entry.getValue() instanceof Map) {
                eachYaml(newKey, (Map<String, Object>) entry.getValue(), result);
            } else {
                if (entry != null && entry.getValue() != null)
                    result.put(newKey, entry.getValue().toString());
            }
        }
    }


    /**
     * 根据key 获取指定的值(指定文件)
     */
    public static String getValue(String fileName, String key) {
        Map<String, String> result = getYamlByFileName(fileName);
        if (result == null || StringUtils.isBlank(result.get(key))) {
            return null;
        }
        return result.get(key);
    }


    public static Map<String, String> loadYamlStr(String str) {
        Map<String, String> result = new HashMap<String, String>();
        if (StringUtils.isEmpty(str)) {
            return result;
        }
        Yaml yaml = new Yaml();
        Map<String, Object> params = yaml.loadAs(str, Map.class);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() instanceof Map) {
                eachYaml(entry.getKey(), (Map<String, Object>) entry.getValue(), result);
            } else {
                if (entry.getValue() != null) {
                    result.put(entry.getKey(), entry.getValue().toString());
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {

    }
}