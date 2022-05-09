package com.weiwan.dsp.api.plugin;


import com.alibaba.fastjson.JSONObject;
import com.weiwan.dsp.api.pojo.DataRecord;
import com.weiwan.dsp.api.pojo.DspRecord;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/3/26 10:24
 * @description:
 */
public interface InputPlugin<OUT extends JSONObject> extends Plugin{

    default void input(Launcher<OUT> launcher){
        throw new UnsupportedOperationException("need to override the method");
    }
}
