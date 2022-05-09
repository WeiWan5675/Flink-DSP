package com.weiwan.dsp.api.plugin;

import com.alibaba.fastjson.JSONObject;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/16 9:26
 * @description:
 */
public interface SplitPlugin<T extends JSONObject> extends Plugin {

    default boolean splitMatch(T record) throws Exception{
        throw new UnsupportedOperationException("need to override the method");
    }

}
