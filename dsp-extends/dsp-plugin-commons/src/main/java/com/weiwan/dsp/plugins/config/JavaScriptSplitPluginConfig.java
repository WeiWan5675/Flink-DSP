package com.weiwan.dsp.plugins.config;

import com.weiwan.dsp.api.plugin.PluginConfig;
import com.weiwan.dsp.common.config.ConfigOption;
import com.weiwan.dsp.common.config.ConfigOptions;

import java.util.List;

/**
 * @author: xiaozhennan
 * @description:
 */

public class JavaScriptSplitPluginConfig extends PluginConfig {
    private static final ConfigOption<String> JAVA_SCRIPT_CONTENT = ConfigOptions.key("javaScriptContent")
            .required(true)
            .description("JavaScript脚本内容")
            .ok(String.class);

    public String getJavaScriptContent() {
        return this.getVal(JAVA_SCRIPT_CONTENT);
    }

}
