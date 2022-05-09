package com.weiwan.dsp.api.config.core;

import com.weiwan.dsp.common.config.ConfigOption;
import com.weiwan.dsp.common.config.ConfigOptions;

import java.io.Serializable;

/**
 * @author: xiaozhennan
 * @date: 2021/6/2 17:24
 * @description: 全局限速设置(AppConfig中)
 */
public class SpeedLimiterConfig implements Serializable {
    public static final ConfigOption<Long> READ_SPEED = ConfigOptions.key("readSpeed")
            .fullKey("dsp.core.speedLimiter.readSpeed")
            .defaultValue(999999L)
            .required(true)
            .description("限速器默认读取限速")
            .ok(Long.class);

    public static final ConfigOption<Long> PROCESS_SPEED = ConfigOptions.key("processSpeed")
            .fullKey("dsp.core.speedLimiter.processSpeed")
            .defaultValue(999999L)
            .required(true)
            .description("限速器默认处理限速")
            .ok(Long.class);

    public static final ConfigOption<Long> WRITE_SPEED = ConfigOptions.key("writeSpeed")
            .fullKey("dsp.core.speedLimiter.writeSpeed")
            .defaultValue(999999L)
            .required(true)
            .description("限速器默认写出限速")
            .ok(Long.class);

    public static final ConfigOption<Long> SAMPLING_INTERVAL = ConfigOptions.key("samplingInterval")
            .fullKey("dsp.core.speedLimiter.samplingInterval")
            .defaultValue(60L)
            .required(true)
            .description("限速器默认限速间隔")
            .ok(Long.class);



    private boolean enableLimiter;
    private long readSpeed;
    private long processSpeed;
    private long writeSpeed;
    private long samplingInterval;

    public boolean isEnableLimiter() {
        return enableLimiter;
    }

    public void setEnableLimiter(boolean enableLimiter) {
        this.enableLimiter = enableLimiter;
    }

    public long getReadSpeed() {
        if (readSpeed == 0) {
            return READ_SPEED.defaultValue();
        }
        return readSpeed;
    }

    public void setReadSpeed(long readSpeed) {
        this.readSpeed = readSpeed;
    }

    public long getWriteSpeed() {
        if (writeSpeed == 0) {
            return WRITE_SPEED.defaultValue();
        }
        return writeSpeed;
    }

    public void setWriteSpeed(long writeSpeed) {
        this.writeSpeed = writeSpeed;
    }

    public long getSamplingInterval() {
        if (samplingInterval == 0) {
            return SAMPLING_INTERVAL.defaultValue();
        }
        return samplingInterval;
    }

    public void setSamplingInterval(long samplingInterval) {
        this.samplingInterval = samplingInterval;
    }

    public long getProcessSpeed() {
        if (processSpeed == 0) {
            return PROCESS_SPEED.defaultValue();
        }
        return processSpeed;
    }

    public void setProcessSpeed(long processSpeed) {
        this.processSpeed = processSpeed;
    }
}
