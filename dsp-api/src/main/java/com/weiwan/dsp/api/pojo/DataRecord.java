package com.weiwan.dsp.api.pojo;

import com.alibaba.fastjson.JSONObject;
import com.weiwan.dsp.common.utils.FileUtil;
import com.weiwan.dsp.common.utils.ObjectUtil;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/3/21 12:55
 * @description:
 */
public class DataRecord extends JSONObject implements Serializable {
    protected long timestamp = -1;

    public DataRecord() {
        this.timestamp = System.currentTimeMillis();
    }

    public DataRecord(Map<String, Object> map) {
        super(map);
        this.timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    public static final String serialize(DataRecord record) {
        return JSONObject.toJSONString(record);
    }

    public static final DataRecord deSerialize(String str) {
        return JSONObject.parseObject(str, DataRecord.class);
    }


    public <T> T getField(String patternName, Class<T> tClass) {
        return getField(patternName, this, tClass);
    }


    public static final <T> T getField(String patternName, JSONObject object, Class<T> tClass) {
        if (patternName == null) {
            return null;
        }
        if (patternName.contains(".")) {
            String[] keys = patternName.split("\\.");
            return (T) getInternalField(keys, object);
        } else {
            JSONObject jsonObject = new JSONObject();
            return object.getObject(patternName, tClass);
        }
    }

    private static final Object getInternalField(String[] keys, Object object) {
        String currentKey = keys[0];
        int currentIndex = -1;
        keys = Arrays.copyOfRange(keys, 1, keys.length);
        Object obj = object;
        if (currentKey.contains("[") && currentKey.contains("]")) {
            final String tmpKey = currentKey.substring(0, currentKey.indexOf('['));
            currentIndex = Integer.parseInt(currentKey.substring(currentKey.indexOf('[') + 1, currentKey.lastIndexOf(']')));
            currentKey = tmpKey;
        }
        if (object instanceof Map) {
            final Map tmpMap = (Map) object;
            obj = tmpMap.get(currentKey);
            if (obj instanceof List && currentIndex > -1) {
                final List tmpList = (List) obj;
                obj = tmpList.get(currentIndex);
            }
        }
        if (object instanceof List && currentIndex > -1) {
            final List tmpList = (List) object;
            obj = tmpList.get(currentIndex);
        }
        if (keys.length > 0 && obj != null) {
            return getInternalField(keys, obj);
        } else {
            return obj;
        }
    }

    public void removeField(String patternName) {
        if (patternName.contains(".")) {
            String[] keys = patternName.split("\\.");
            removeField(keys, this);
        } else {
            this.remove(patternName);
        }
    }

    /**
     * 移除指定的key
     * 递归找出指定的key, 然后删除
     *
     * @param keys   需要删除的字段
     * @param object 当前数据
     */
    public static final void removeField(String[] keys, Object object) {
        String currentKey = keys[0];
        int currentIndex = -1;
        keys = Arrays.copyOfRange(keys, 1, keys.length);
        Object obj = object;
        if (currentKey.contains("[") && currentKey.contains("]")) {
            final String tmpKey = currentKey.substring(0, currentKey.indexOf('['));
            currentIndex = Integer.parseInt(currentKey.substring(currentKey.indexOf('[') + 1, currentKey.lastIndexOf(']')));
            currentKey = tmpKey;
        }
        if (object instanceof Map) {
            final Map tmpMap = (Map) object;
            obj = tmpMap.get(currentKey);
            if (keys.length > 0 && obj instanceof List && currentIndex > -1) {
                final List tmpList = (List) obj;
                obj = tmpList.get(currentIndex);
            }
        }
        if (object instanceof List && currentIndex > -1) {
            if (keys.length > 0) {
                final List tmpList = (List) object;
                obj = tmpList.get(currentIndex);
            }
        }
        if (keys.length > 0) {
            removeField(keys, obj);
        } else {
            if (currentIndex > -1 && obj instanceof List) {
                ((List<Object>) obj).remove(currentIndex);
            } else {
                ((Map<String, Object>) object).remove(currentKey);
            }
        }
    }

    public void addField(String patternName, Object field) {
        if (patternName.contains(".")) {
            String[] keys = patternName.split("\\.");
            addField(keys, this, field);
        } else {
            Map map = this;
            map.put(patternName, field);
        }
    }

    private void addField(String[] keys, Object object, Object field) {
        String currentKey = keys[0];
        int currentIndex = -1;
        keys = Arrays.copyOfRange(keys, 1, keys.length);
        Object obj = object;
        if (currentKey.contains("[") && currentKey.contains("]")) {
            final String tmpKey = currentKey.substring(0, currentKey.indexOf('['));
            currentIndex = Integer.parseInt(currentKey.substring(currentKey.indexOf('[') + 1, currentKey.lastIndexOf(']')));
            currentKey = tmpKey;
        }
        if (object instanceof Map) {
            final Map tmpMap = (Map) object;
            obj = tmpMap.get(currentKey);
            if (obj instanceof List && currentIndex > -1) {
                final List tmpList = (List) obj;
                obj = tmpList.get(currentIndex);
            }
        }
        if (object instanceof List && currentIndex > -1) {
            final List tmpList = (List) object;
            obj = tmpList.get(currentIndex);
        }
        if (keys.length > 0 && obj != null) {
            addField(keys, obj, field);
        } else if (object instanceof Map) {
            Map map = (Map) object;
            map.put(currentKey, field);
        }
    }


    public static void main(String[] args) throws IOException {
        String content = FileUtil.readFileContent("F:\\project\\Flink-DSP\\tmp\\jobs\\job_b82344d529d3a8b8d35b1f5b76576824.json");
        JSONObject jsonObject = JSONObject.parseObject(content);
        DataRecord dataRecord = new DataRecord(jsonObject);
//        Object field = "123123123";
//        dataRecord.addField("dsp.flow.nodes.fcf523c4-8cc4-41c1-8795-3ab8c2509853.nodeDependents[0].testAdd", field);
//        System.out.println(dataRecord);

        dataRecord.removeField("dsp.flow.nodes.fcf523c4-8cc4-41c1-8795-3ab8c2509853.testRemove[0].a[0]");
        System.out.println(dataRecord);


//        List field = dataRecord.getField("dsp.flow.nodes.fcf523c4-8cc4-41c1-8795-3ab8c2509853.plugins", List.class);
//        System.out.println(field);


    }
}
