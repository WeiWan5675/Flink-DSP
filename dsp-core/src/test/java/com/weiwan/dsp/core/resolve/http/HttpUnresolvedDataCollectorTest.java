package com.weiwan.dsp.core.resolve.http;

import com.weiwan.dsp.api.pojo.UnresolvedDataRecord;
import com.weiwan.dsp.api.pojo.DataRecord;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Zhu Shaoqin
 * @email zsqmia@163.com
 * @date 2022/3/9 14:55
 * @description
 */
public class HttpUnresolvedDataCollectorTest {

    private HttpUnresolvedDataCollector collector;

    @Before
    public void beforeInit() {
        this.collector = new HttpUnresolvedDataCollector();
        HttpUnresolvedConfig config = new HttpUnresolvedConfig();
        collector.init(config);
    }

    @Test
    public void testHandler() {
        UnresolvedDataRecord<DataRecord> record = new UnresolvedDataRecord<>();
        collector.handler(record);
    }
}