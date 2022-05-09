package com.weiwan.dsp.plugins.process;

import com.weiwan.dsp.api.plugin.PluginConfig;
import com.weiwan.dsp.api.enums.FieldType;
import com.weiwan.dsp.common.config.ConfigOption;
import com.weiwan.dsp.common.config.ConfigOptions;
import com.weiwan.dsp.core.pub.DataField;
import com.weiwan.dsp.api.pojo.DataRecord;
import com.weiwan.dsp.core.plugin.RichProcessPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/15 15:52
 * @description:
 */
public class ExampleProcessPlugin extends RichProcessPlugin<ExampleProcessPluginConfig> {
    private static final Logger logger = LoggerFactory.getLogger(ExampleProcessPlugin.class);
    private PluginConfig config;

    public static final ConfigOption<Long> PROCESS_INTERVAL = ConfigOptions.key("processInterval")
            .fullKey("example.interval")
            .defaultValue(50L)
            .description("模拟处理数据耗费时间")
            .ok(Long.class);

    private long interval;

    @Override
    public void init(ExampleProcessPluginConfig config) {
        logger.info("Example Process Plugin Init Function running");
        interval = config.getVal(PROCESS_INTERVAL);
        this.config = config;
    }

    @Override
    public void stop() {
        logger.info("Example Process Plugin Stop Function running.");
    }

    @Override
    public DataRecord handle(DataRecord record) {
        DataField dataField = new DataField();
        dataField.setFieldType(FieldType.DATE);
        dataField.setFieldValue(new Date());
        dataField.setFieldName("processAddField");
        Random random = new Random();
        int i = random.nextInt((int) interval);
        try {
            TimeUnit.MILLISECONDS.sleep(i);
        } catch (InterruptedException e) {
        }
        record.put(dataField.getFieldName(), dataField);
        if (record != null) {
            if (record.getTimestamp() != 0) {
                if (record.getTimestamp() % 13 == 0) {
                    throw new RuntimeException("测试异常");
                }
            }
        }
        return record;
    }
}
