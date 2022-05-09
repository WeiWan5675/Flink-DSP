package com.weiwan.dsp.core.resolve.kafka;

import com.weiwan.dsp.api.pojo.UnresolvedDataRecord;
import com.weiwan.dsp.core.resolve.DspUnresolvedDataCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: xiaozhennan
 * @date: 2021/6/2 14:50
 * @description:
 */
public class KafkaUnresolvedDataCollector extends DspUnresolvedDataCollector<KafkaUnresolvedConfig> {
    private static final Logger logger = LoggerFactory.getLogger(KafkaUnresolvedDataCollector.class);

    @Override
    public void init(KafkaUnresolvedConfig collectorConfig) {

    }

    @Override
    public void stop() {

    }

    @Override
    public void handler(UnresolvedDataRecord record) {
        logger.info(record.toString());
    }
}
