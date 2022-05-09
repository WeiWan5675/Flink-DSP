package com.weiwan.dsp.api.resolve;

import com.weiwan.dsp.api.enums.UnresolvedType;
import com.weiwan.dsp.api.pojo.DataRecord;
import com.weiwan.dsp.api.pojo.DspRecord;

import java.util.Map;

/**
 * @Author: xiaozhennan
 * @Date: 2021/6/1 23:18
 * @Package: com.weiwan.dsp.api.context
 * @ClassName: UnresolvedDataCollector
 * @Description: 未解析日志收集器接口
 **/
public interface UnresolvedDataCollector<T extends DataRecord> extends UnresolvedCollector<T> {


    /**
     * 输出未解析日志,这里是异步处理的
     *
     * @param type       未解析类型
     * @param dataRecord 数据
     */
    void collect(UnresolvedType type, T dataRecord, String msg);



    /**
     * 返回每种未解析的数量
     *
     * @return 返回每种类型的未解析数量
     */
    Map<UnresolvedType, Long> statistics();

    /**
     * 返回指定类型的未解析数量
     *
     * @param type 未解析类型
     * @return 返回指定类型的未解析数量
     */
    long count(UnresolvedType type);

    /**
     * 清空指定类型的未解析数据
     *
     * @param type 未解析类型
     * @return 返回成功状态
     */
    boolean clean(UnresolvedType type);

    /**
     * 获取所有未解析数量
     *
     * @return 数据
     */
    long allCount();


}
