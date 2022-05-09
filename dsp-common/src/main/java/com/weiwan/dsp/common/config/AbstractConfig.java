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
package com.weiwan.dsp.common.config;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.convert.ConvertException;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.internal.LinkedTreeMap;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("all")
public abstract class AbstractConfig extends ConcurrentHashMap<String, Object> implements Serializable {


    public AbstractConfig() {
    }

    public AbstractConfig(Map m) {
        super();
        if (m != null) {
            this.putAll(m);
        }
    }


    public void setVal(String key, Object value) {
        this.put(key, value);
    }

    public <T extends Object> void setVal(ConfigOption<T> key, Object value) {
        this.put(key.key(), value);
    }


    public void setStringVal(String key, String value) {
        setVal(key, value);
    }

    public void setBooleanVal(String key, boolean value) {
        setVal(key, value);
    }

    public void setIntVal(String key, int value) {
        setVal(key, value);
    }

    public void setLongVal(String key, long value) {
        setVal(key, value);
    }

    public void setDoubleVal(String key, double value) {
        setVal(key, value);
    }

    public Object getVal(String key) {
        Object obj = this.get(key);
        if (obj instanceof LinkedTreeMap) {
            LinkedTreeMap treeMap = (LinkedTreeMap) obj;
            Map<String, Object> newMap = new HashMap<>(treeMap.size());
            newMap.putAll(treeMap);
            return newMap;
        }
        return obj;
    }

    public Object getVal(String key, Object defaultValue) {
        Object ret = getVal(key);
        if (ret == null) {
            return defaultValue;
        }
        return ret;
    }

    public <T> T getVal(String key, Class<T> tClass) {
        Object ret = getVal(key);
        if (ret == null) return null;
        if (ret instanceof JSONArray && tClass.isAssignableFrom(List.class)) {
            ret = Convert.convertByClassName(tClass.getName(), ret);
        } else if (ret instanceof JSONObject && tClass.isAssignableFrom(Map.class)) {
            ret = Convert.convertByClassName(tClass.getName(), ret);
        } else if (tClass != null) {
            ret = Convert.convertByClassName(tClass.getName(), ret);
        } else {
            ret = Convert.convert(String.class, ret);
        }
        return (T) ret;
    }

    public String getStringVal(String key) {
        return (String) this.get(key);
    }

    public String getStringVal(String key, String defaultValue) {
        String ret = getStringVal(key);
        if (ret == null || ret.trim().length() == 0) {
            return defaultValue;
        }
        return ret;
    }

    public int getIntVal(String key, int defaultValue) {
        Object ret = this.get(key);
        if (ret == null) {
            return defaultValue;
        }
        if (ret instanceof Integer) {
            return ((Integer) ret).intValue();
        }
        if (ret instanceof String) {
            return Integer.valueOf((String) ret).intValue();
        }
        if (ret instanceof Long) {
            return ((Long) ret).intValue();
        }
        if (ret instanceof Float) {
            return ((Float) ret).intValue();
        }
        if (ret instanceof Double) {
            return ((Double) ret).intValue();
        }
        if (ret instanceof BigInteger) {
            return ((BigInteger) ret).intValue();
        }
        if (ret instanceof BigDecimal) {
            return ((BigDecimal) ret).intValue();
        }
        throw new RuntimeException("can't cast " + key + " from " + ret.getClass().getName() + " to Integer");
    }

    public long getLongVal(String key, long defaultValue) {
        Object ret = this.get(key);
        if (ret == null) {
            return defaultValue;
        }
        if (ret instanceof Long) {
            return ((Long) ret);
        }
        if (ret instanceof Integer) {
            return ((Integer) ret).longValue();
        }
        if (ret instanceof String) {
            return Long.valueOf((String) ret);
        }
        if (ret instanceof Float) {
            return ((Float) ret).longValue();
        }
        if (ret instanceof Double) {
            return ((Double) ret).longValue();
        }
        if (ret instanceof BigInteger) {
            return ((BigInteger) ret).longValue();
        }
        if (ret instanceof BigDecimal) {
            return ((BigDecimal) ret).longValue();
        }
        throw new RuntimeException("can't cast " + key + " from " + ret.getClass().getName() + " to Long");
    }

    public double getDoubleVal(String key, double defaultValue) {
        Object ret = this.get(key);
        if (ret == null) {
            return defaultValue;
        }
        if (ret instanceof Double) {
            return ((Double) ret);
        }
        if (ret instanceof Long) {
            return ((Long) ret).doubleValue();
        }
        if (ret instanceof Integer) {
            return ((Integer) ret).doubleValue();
        }
        if (ret instanceof String) {
            return Double.valueOf((String) ret);
        }
        if (ret instanceof Float) {
            return ((Float) ret).doubleValue();
        }
        if (ret instanceof BigInteger) {
            return ((BigInteger) ret).doubleValue();
        }
        if (ret instanceof BigDecimal) {
            return ((BigDecimal) ret).doubleValue();
        }
        throw new RuntimeException("can't cast " + key + " from " + ret.getClass().getName() + " to Double");
    }


    public boolean getBooleanVal(String key, boolean defaultValue) {
        Object ret = this.get(key);
        if (ret == null) {
            return defaultValue;
        }
        if (ret instanceof Boolean) {
            return (Boolean) ret;
        }
        if (ret instanceof String) {
            return Boolean.valueOf(((String) ret).toLowerCase());
        }
        throw new RuntimeException("can't cast " + key + " from " + ret.getClass().getName() + " to Boolean");
    }

    public List<String> getListForSplit(String key, String split) {
        List<String> vars = new ArrayList<>();
        if (key != null && !"".equalsIgnoreCase(key)) {
            Object obj = this.get(key);
            if (obj instanceof String) {
                String[] splits = ((String) obj).split(split);
                vars.addAll(Arrays.asList(splits));
            }
        }
        return vars;
    }

    public <T extends Object> T getVal(ConfigOption<T> option) {
        if (option == null) return null;
        Object o = this.get(option.key());
        if (o == null) return option.defaultValue();

        if (o instanceof Map) {
            JSONObject jsonObject = new JSONObject((Map) o);
            ConfigOption configOption = jsonObject.toJavaObject(new TypeReference<ConfigOption<T>>(){});
            Object value = configOption.value();
            if (value == null) {
                value = configOption.defaultValue();
            }
            if (option.type().isAssignableFrom(Map.class)) {
                if (value instanceof Map) {
                    return (T) value;
                } else if (value instanceof String) {
                    return (T) JSONObject.parseObject((String) value);
                }
            }
            if (option.type().isAssignableFrom(List.class)) {
                if (value instanceof List) {
                    return (T) value;
                } else if (value instanceof String) {
                    return (T) JSONArray.parseArray((String) value);
                }
            }
            return Convert.convert(option.type(), value);
        }

        if (o instanceof List) {
            JSONArray objects = new JSONArray((List) o);
            if (option.type().isAssignableFrom(List.class)) {
                return (T) objects.toJavaList(option.type());
            }
        }

        if (o instanceof Byte && option.type() == Byte.class) {
            return (T) o;
        }
        if (o instanceof Short && option.type() == Short.class) {
            return (T) o;
        }
        if (o instanceof Integer && option.type() == Integer.class) {
            return (T) o;
        }
        if (o instanceof Long && option.type() == Long.class) {
            return (T) o;
        }
        if (o instanceof Double && option.type() == Double.class) {
            return (T) o;
        }
        if (o instanceof Float && option.type() == Float.class) {
            return (T) o;
        }
        if (o instanceof Boolean && option.type() == Boolean.class) {
            return (T) o;
        }
        try {
            return Convert.convert(option.type(), o);
        } catch (ConvertException e) {
        }
        return option.defaultValue();
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

    public abstract void loadOptions(List<ConfigOption> options);
}