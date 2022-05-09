package com.weiwan.dsp.api.plugin;

import com.alibaba.fastjson.JSONObject;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/3/26 10:24
 * @description:
 */
public interface ProcessPlugin<IN extends JSONObject, OUT extends JSONObject> extends Plugin {

    default OUT process(IN obj){
        throw new UnsupportedOperationException("need to override the method");
    }

}
