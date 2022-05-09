package com.weiwan.dsp.plugins.output;

import com.weiwan.dsp.api.plugin.PluginConfig;
import com.weiwan.dsp.common.config.ConfigOption;
import com.weiwan.dsp.common.config.ConfigOptions;

import java.util.List;


/**
 * @author: xiaozhennan
 * @description:
 */
public class ExampleOutputPluginConfig extends PluginConfig {

    public static final ConfigOption<Long> WRITE_INTERVAL = ConfigOptions.key("writeInterval")
            .defaultValue(10L)
            .required(false)
            .description("mock的批次大小")
            .ok(Long.class);

}
