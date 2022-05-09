package com.weiwan.dsp.test;

import com.weiwan.dsp.api.config.core.Configs;
import com.weiwan.dsp.api.config.core.EngineConfig;
import com.weiwan.dsp.api.plugin.PluginConfig;
import com.weiwan.dsp.api.enums.EngineMode;
import com.weiwan.dsp.api.enums.EngineType;
import com.weiwan.dsp.common.config.ConfigOption;
import com.weiwan.dsp.common.config.ConfigOptions;

import java.util.HashMap;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/5/27 18:38
 * @description:
 */
public class TestTypeConver {

    public static void main(String[] args) {
        EngineConfig engineConfig = new EngineConfig();
        Configs engineConfigs = engineConfig.getEngineConfigs();

        engineConfigs.setVal(EngineConfig.ENGINE_MODE, "flink_Yarn_per_job");
        EngineMode val = engineConfigs.getVal(EngineConfig.ENGINE_MODE);
        System.out.println(val);


        engineConfigs.setVal(EngineConfig.ENGINE_TYPE, "flink");

        EngineType val1 = engineConfigs.getVal(EngineConfig.ENGINE_TYPE);
        System.out.println(val1);


        PluginConfig pluginConfig = new PluginConfig(new HashMap());
        pluginConfig.setVal(PluginConfig.PLUGIN_NAME, "test");
        String val2 = pluginConfig.getVal(PluginConfig.PLUGIN_NAME);
        System.out.println(val2);

        ConfigOption<Integer> Test = ConfigOptions.key("pluginClass")
                .defaultValue(1).description("插件ClassName").ok(Integer.class);

        pluginConfig.setVal(Test, 1231);

        Integer val3 = pluginConfig.getVal(Test);


    }


}
