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


import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesUtil {
    //    private static final Logger logger = LogManager.getLogger(PropertiesUtil.class);
    private static Map<String, PropertiesUtil> map = new HashMap<String, PropertiesUtil>();
    private Properties pros;
    private static String charset = "UTF-8";


    private PropertiesUtil(Properties pros) {
        this.pros = pros;
    }

    /**
     * @param
     * @return PropertiesUtil 返回类型
     * @Title: 根据指定编码加载属性文件
     * @Description: 适配属性文件定义为gbk, 读取时指定的编码为utf-8
     */
    public static PropertiesUtil load(String filePath, String charset) {
        PropertiesUtil.charset = charset;
        return PropertiesUtil.load(filePath);
    }

    /**
     * <p>Description:静态加载属性文件</p>
     *
     * @param filePath 文件路径
     * @return config 返回Config 对象
     * @Title: load
     * @author xiaozhennan
     */
    public static PropertiesUtil load(String filePath) {
        if (map.containsKey(filePath)) {
        } else {
            InputStream input = null;
            try {
                input = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
                Properties _pros = new Properties();
                _pros.load(input);
                map.put(filePath, new PropertiesUtil(_pros));
            } catch (IOException e) {
            } finally {
                try {
                    if (input != null) {
                        input.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        return map.get(filePath);
    }


    /**
     * 读取properties配置文件,转化为Map<String,String>
     *
     * @param filePath 文件路径
     * @return k/v 形式的map
     */
    public static Map<String, String> loadProperties(String filePath) {
        InputStream input = null;
        try {
            input = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
            Properties _pros = new Properties();
            _pros.load(input);
            return new HashMap<String, String>((Map) _pros);
        } catch (IOException e) {
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new HashMap<>();
    }

    public static Map<String, String> loadPropStr(String content) {
        StringReader input = new StringReader(content);
        Properties _pros = new Properties();
        Map<String,String> map = new HashMap<>();
        try {
            _pros.load(input);
            map = new HashMap<String,String>((Map) _pros);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }


}
