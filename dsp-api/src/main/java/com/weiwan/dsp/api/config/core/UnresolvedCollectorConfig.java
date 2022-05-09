package com.weiwan.dsp.api.config.core;

import com.weiwan.dsp.api.enums.UnresolvedHandlerType;
import com.weiwan.dsp.common.config.ConfigOption;
import com.weiwan.dsp.common.config.ConfigOptions;

import java.io.Serializable;

/**
 * @author: xiaozhennan
 * @date: 2021/6/2 17:24
 * @description:
 */
public class UnresolvedCollectorConfig implements Serializable {


    private UnresolvedHandlerType collectorHandler;
    private long maxSamplingRecord;
    private long samplingInterval;
    private boolean enableCollector;
    private CollectorHandlerConfigs handlerConfigs;


    public static final ConfigOption<UnresolvedHandlerType> COLLECTOR_HANDLER = ConfigOptions.key("collectorHandler")
            .fullKey("dsp.core.unresolvedCollector.collectorHandler")
            .defaultValue(UnresolvedHandlerType.LOG_HANDLER)
            .description("unresolved collector class name")
            .optionals(UnresolvedHandlerType.LOG_HANDLER, UnresolvedHandlerType.HTTP_HANDLER)
            .required(true)
            .ok(UnresolvedHandlerType.class);
    public static final ConfigOption<Long> MAX_SAMPLING_RECORD = ConfigOptions.key("maxSamplingRecord")
            .fullKey("dsp.core.unresolvedCollector.maxSamplingRecord")
            .defaultValue(100L)
            .description("Maximum number of unresolved collectors collected per unit time")
            .required(false)
            .ok(Long.class);
    public static final ConfigOption<Long> SAMPLING_INTERVAL = ConfigOptions.key("samplingInterval")
            .fullKey("dsp.core.unresolvedCollector.samplingInterval")
            .defaultValue(30L)
            .description("unit time collected by the unresolved collector")
            .required(false)
            .ok(Long.class);
    public static final ConfigOption<Boolean> ENABLE_COLLECTOR = ConfigOptions.key("enableCollector")
            .fullKey("dsp.core.unresolvedCollector.enableCollector")
            .defaultValue(true)
            .description("whether to open the unresolved data collector")
            .required(true)
            .ok(Boolean.class);
    public static final ConfigOption<CollectorHandlerConfigs> COLLECTOR_HANDLER_CONFIGS = ConfigOptions.key("collectorHandlerConfigs")
            .fullKey("dsp.core.unresolvedCollector.handlerConfigs")
            .defaultValue(new Configs())
            .description("The configuration information of the data collection processor was not resolved")
            .required(false)
            .ok(CollectorHandlerConfigs.class);

    public UnresolvedHandlerType getCollectorHandler() {
        return collectorHandler;
    }

    public void setCollectorHandler(UnresolvedHandlerType collectorHandler) {
        this.collectorHandler = collectorHandler;
    }

    public long getMaxSamplingRecord() {
        return maxSamplingRecord;
    }

    public void setMaxSamplingRecord(long maxSamplingRecord) {
        this.maxSamplingRecord = maxSamplingRecord;
    }

    public long getSamplingInterval() {
        return samplingInterval;
    }

    public void setSamplingInterval(long samplingInterval) {
        this.samplingInterval = samplingInterval;
    }

    public boolean isEnableCollector() {
        return enableCollector;
    }

    public void setEnableCollector(boolean enableCollector) {
        this.enableCollector = enableCollector;
    }

    public CollectorHandlerConfigs getHandlerConfigs() {
        return handlerConfigs;
    }

    public void setHandlerConfigs(CollectorHandlerConfigs handlerConfigs) {
        this.handlerConfigs = handlerConfigs;
    }
}
