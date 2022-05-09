package com.weiwan.dsp.api.plugin;

import com.alibaba.fastjson.JSONObject;
import com.weiwan.dsp.api.context.DspSupport;
import com.weiwan.dsp.api.pojo.DspRecord;

/**
 * @author: xiaozhennan
 * @date: 2021/6/15 13:53
 * @description:
 */
public interface PluginContainer<T extends JSONObject> extends DspSupport {


    /**
     * 这个方法在真正运行前被调用,可以处理一些引擎的内容
     */
    void init();

    void close();


}
