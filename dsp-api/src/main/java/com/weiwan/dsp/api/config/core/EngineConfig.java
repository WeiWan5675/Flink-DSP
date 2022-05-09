package com.weiwan.dsp.api.config.core;

import com.alibaba.fastjson.JSONObject;
import com.weiwan.dsp.api.enums.EngineMode;
import com.weiwan.dsp.api.enums.EngineType;
import com.weiwan.dsp.common.config.ConfigOption;
import com.weiwan.dsp.common.config.ConfigOptions;

import java.io.Serializable;
import java.util.*;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/15 9:59
 * @description:
 */
public class EngineConfig implements Serializable {
    public static final ConfigOption<EngineType> ENGINE_TYPE = ConfigOptions.key("engineType")
            .fullKey("dsp.core.engineConfig.engineType")
            .defaultValue(EngineType.FLINK)
            .description("运行引擎的类型")
            .ok(EngineType.class);

    public static final ConfigOption<EngineMode> ENGINE_MODE = ConfigOptions.key("engineMode")
            .fullKey("dsp.core.engineConfig.engineMode")
            .defaultValue(EngineMode.FLINK_ON_YARN_PER)
            .description("运行引擎的模式")
            .ok(EngineMode.class);

    public static final ConfigOption<Configs> ENGINE_CONFIGS = ConfigOptions.key("engineConfigs")
            .fullKey("dsp.core.engineConfig.engineConfigs")
            .defaultValue(new JSONObject())
            .description("引擎运行配置")
            .ok(Configs.class);


    private EngineType engineType;
    private EngineMode engineMode;
    private Configs engineConfigs;

    public EngineConfig() {
    }


    public EngineType getEngineType() {
        return engineType;
    }

    public void setEngineType(EngineType engineType) {
        this.engineType = engineType;
    }

    public EngineMode getEngineMode() {
        return engineMode;
    }

    public void setEngineMode(EngineMode engineMode) {
        this.engineMode = engineMode;
    }

    public Configs getEngineConfigs() {
        return engineConfigs;
    }

    public void setEngineConfigs(Configs configs) {
        this.engineConfigs = configs;
    }


    public static final List<ConfigOption> getEngineConfigOptions() {
        List<ConfigOption> options = new ArrayList<ConfigOption>();
        options.add(ENGINE_TYPE);
        options.add(ENGINE_MODE);
        options.add(ENGINE_CONFIGS);
        return options;
    }
}
