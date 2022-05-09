package com.weiwan.dsp.api.resolve;

import com.alibaba.fastjson.JSONObject;
import com.weiwan.dsp.api.config.core.UnresolvedCollectorConfig;
import com.weiwan.dsp.api.pojo.DspRecord;

/**
 * @author: xiaozhennan
 * @description:
 */
public interface UnresolvedCollector<T extends JSONObject> {
    /**
     * 输出未解析日志,这里是异步处理的
     *
     * @param type       未解析类型
     * @param dataRecord 数据
     */
    void collect(String type, T dataRecord, String msg);


    void open(UnresolvedCollectorConfig config);

    void close();
}
