package com.weiwan.dsp.api.config.core;

import com.alibaba.fastjson.JSONObject;
import com.weiwan.dsp.common.config.ConfigOption;
import com.weiwan.dsp.common.utils.ObjectUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: xiaozhennan
 * @Date: 2022/4/11 17:52
 * @Package: com.weiwan.dsp.api.config.core
 * @ClassName: Options
 * @Description:
 **/
public interface Options {

    default void loadOptions(List<ConfigOption> options){

    }

    default String toJSON() {
        List<ConfigOption> results = new ArrayList<ConfigOption>();
        loadOptions(results);
        JSONObject jsonObject = new JSONObject();
        for (ConfigOption result : results) {
            jsonObject.put(result.key(), result);
        }
        return ObjectUtil.serialize(jsonObject);
    }

}
