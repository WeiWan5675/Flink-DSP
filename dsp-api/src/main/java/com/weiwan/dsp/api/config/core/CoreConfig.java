package com.weiwan.dsp.api.config.core;

import com.weiwan.dsp.common.config.ConfigOption;
import com.weiwan.dsp.common.config.ConfigOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/5/21 18:43
 * @description:
 */
public class CoreConfig implements Serializable {

    public static final ConfigOption<MetricConfig> METRIC_CONFIG = ConfigOptions.key("metricConfig")
            .fullKey("dsp.core.metricConfig").defaultValue(new MetricConfig()).description("监控配置").ok(MetricConfig.class);

    public static final ConfigOption<EngineConfig> ENGINE_CONFIG = ConfigOptions.key("engineConfig")
            .fullKey("dsp.core.engineConfig").defaultValue(new EngineConfig()).description("引擎配置").ok(EngineConfig.class);

    public static final ConfigOption<SpeedLimiterConfig> SPEED_LIMITER = ConfigOptions.key("speedLimiter")
            .fullKey("dsp.core.speedLimiter").defaultValue(new SpeedLimiterConfig()).description("限流配置").ok(SpeedLimiterConfig.class);

    public static final ConfigOption<UnresolvedCollectorConfig> UNRESOLVED_COLLECTOR = ConfigOptions.key("unresolvedCollector")
            .fullKey("dsp.core.unresolvedCollector").defaultValue(new UnresolvedCollectorConfig()).description("未处理数据配置").ok(UnresolvedCollectorConfig.class);


    private EngineConfig engineConfig;
    private MetricConfig metricConfig;
    private SpeedLimiterConfig speedLimiter;
    private UnresolvedCollectorConfig unresolvedCollector;


    public SpeedLimiterConfig getSpeedLimiter() {
        return speedLimiter;
    }

    public void setSpeedLimiter(SpeedLimiterConfig speedLimiter) {
        this.speedLimiter = speedLimiter;
    }

    public UnresolvedCollectorConfig getUnresolvedCollector() {
        return unresolvedCollector;
    }

    public void setUnresolvedCollector(UnresolvedCollectorConfig unresolvedCollector) {
        this.unresolvedCollector = unresolvedCollector;
    }

    public EngineConfig getEngineConfig() {
        return engineConfig;
    }

    public void setEngineConfig(EngineConfig engineConfig) {
        this.engineConfig = engineConfig;
    }


    public MetricConfig getMetricConfig() {
        return metricConfig;
    }

    public void setMetricConfig(MetricConfig metricConfig) {
        this.metricConfig = metricConfig;
    }


    /**
     * 这里每次添加核心配置, 都需要在该方法种添加
     *
     * @return
     */
    public static final List<ConfigOption> getCoreConfigOptions() {
        List<ConfigOption> options = new ArrayList<ConfigOption>();
        options.add(METRIC_CONFIG);
        options.add(ENGINE_CONFIG);
        options.add(SPEED_LIMITER);
        options.add(UNRESOLVED_COLLECTOR);
        return options;
    }
}
