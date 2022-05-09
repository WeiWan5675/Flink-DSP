package com.weiwan.dsp.api.plugin;

import com.alibaba.fastjson.JSONObject;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/16 9:29
 * @description:
 */
public interface UnionPlugin<U1 extends JSONObject, U2 extends JSONObject> {

    void union(U1 r1 , U2 r2);

    void meage(U1 r1, U2 r2);
}
