package com.weiwan.dsp.api.config.core;

import com.alibaba.fastjson.JSONObject;
import com.weiwan.dsp.common.config.AbstractConfig;
import com.weiwan.dsp.common.config.ConfigOption;
import com.weiwan.dsp.common.utils.ObjectUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: xiaozhennan
 * @description:
 */
public class Configs extends AbstractConfig implements Options {

    public Configs(Map m) {
        super(m);
    }

    @Override
    public void loadOptions(List<ConfigOption> options) {

    }

    public String toJSON() {
        return toJSON(this);
    }

    public static String toJSON(Configs configs) {
        List<ConfigOption> results = new ArrayList<ConfigOption>();
        configs.loadOptions(results);
        JSONObject jsonObject = new JSONObject();
        for (ConfigOption result : results) {
            jsonObject.put(result.key(), result);
        }
        return ObjectUtil.serialize(jsonObject);
    }

    public Configs() {
    }

    public Map<String, Object> getConfigs() {
        Map<String, Object> tmp = new HashMap<>();
        for (String key : this.keySet()) {
            tmp.put(key, String.valueOf(this.get(key)));
        }
        return tmp;
    }
}
