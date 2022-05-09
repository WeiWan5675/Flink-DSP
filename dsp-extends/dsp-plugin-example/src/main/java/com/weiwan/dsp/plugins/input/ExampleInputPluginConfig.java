package com.weiwan.dsp.plugins.input;

import com.alibaba.fastjson.JSONObject;
import com.weiwan.dsp.api.plugin.PluginConfig;
import com.weiwan.dsp.common.config.ConfigOption;
import com.weiwan.dsp.common.config.ConfigOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: xiaozhennan
 * @description:
 */
public class ExampleInputPluginConfig extends PluginConfig {

    public static final ConfigOption<List> FIELDS = ConfigOptions.key("fields")
            .defaultValue(new ArrayList<>())
            .required(true)
            .description("数据字段")
            .ok(List.class);
    public static final ConfigOption<Long> MOCK_INTERVAL = ConfigOptions.key("mockInterval")
            .defaultValue(5000L)
            .required(true)
            .description("Mock数据的间隔")
            .ok(Long.class);

    public static final ConfigOption<Long> MAX_MOCK_SIZE = ConfigOptions.key("maxMockSize")
            .defaultValue(100000L)
            .required(false)
            .description("mock的最大数据条数")
            .ok(Long.class);
    public static final ConfigOption<Long> MOCK_BATCH_SIZE = ConfigOptions.key("mockBatchSize")
            .defaultValue(10L)
            .required(false)
            .description("mock的批次大小")
            .ok(Long.class);


    public List getFields() {
        return this.getVal(FIELDS);
    }


    public Long getMaxMockSize() {
        return this.getVal(MAX_MOCK_SIZE);
    }

    public Long getMockInterval() {
        return this.getVal(MOCK_INTERVAL);
    }

    public Long getMockBatchSize() {
        return this.getVal(MOCK_BATCH_SIZE);
    }
}
