package com.weiwan.dsp.core.resolve.kafka;

import com.weiwan.dsp.api.config.core.CollectorHandlerConfigs;
import com.weiwan.dsp.api.config.core.UnresolvedCollectorConfig;
import com.weiwan.dsp.common.config.ConfigOption;

import java.util.List;

/**
 * @Author: xiaozhennan
 * @Date: 2021/8/7 22:25
 * @Package: com.weiwan.dsp.core.resolve.kafka
 * @ClassName: KafkaUnresolvedConfig
 * @Description: Kafka未解析数据收集器配置
 **/
public class KafkaUnresolvedConfig extends CollectorHandlerConfigs {

    @Override
    public void loadOptions(List<ConfigOption> options) {
        super.loadOptions(options);

    }
}
