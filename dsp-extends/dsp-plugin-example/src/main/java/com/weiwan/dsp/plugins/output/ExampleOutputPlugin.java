package com.weiwan.dsp.plugins.output;

import com.weiwan.dsp.api.pojo.DataRecord;
import com.weiwan.dsp.core.plugin.RichOutputPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/15 15:52
 * @description:
 */
public class ExampleOutputPlugin extends RichOutputPlugin<ExampleOutputPluginConfig> {

    private static final Logger logger = LoggerFactory.getLogger(ExampleOutputPlugin.class);
    private ExampleOutputPluginConfig config;

    private Long writeInterval;

    @Override
    public void init(ExampleOutputPluginConfig config) {
        logger.info("Example Output Plugin Init Function Running.");
        this.writeInterval = config.getVal(ExampleOutputPluginConfig.WRITE_INTERVAL);
        this.config = config;
    }

    @Override
    public void stop() {
        logger.info("Example Output Plugin Stop Function Running.");
    }

    @Override
    public void write(DataRecord record) throws Exception {
        sleepWrite();
        logger.info(record.toString());
    }

    private void sleepWrite() throws InterruptedException {
        Random random = new Random();
        int interval = random.nextInt(writeInterval.intValue());
        TimeUnit.MILLISECONDS.sleep(interval);
    }

    @Override
    public void batchWrite(List<DataRecord> records) throws Exception {
        sleepWrite();
        logger.info(records.toString());
    }
}
