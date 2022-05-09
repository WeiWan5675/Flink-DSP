package com.weiwan.dsp.api.plugin;

import com.alibaba.fastjson.JSONObject;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/3/26 10:24
 * @description:
 */
public interface OutputPlugin<INPUT extends JSONObject> extends Plugin {

    public void output(INPUT obj);

}
