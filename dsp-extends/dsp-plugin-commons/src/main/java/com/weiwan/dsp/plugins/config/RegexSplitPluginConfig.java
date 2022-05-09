package com.weiwan.dsp.plugins.config;

import com.weiwan.dsp.api.plugin.PluginConfig;
import com.weiwan.dsp.common.config.ConfigOption;
import com.weiwan.dsp.common.config.ConfigOptions;

import java.util.List;

/**
 * @author: xiaozhennan
 * @description:
 */
public class RegexSplitPluginConfig extends PluginConfig {

    private static final ConfigOption<String> REGEX_MATCH_FIELD = ConfigOptions.key("regexMatchField")
            .description("正则表达式匹配字段")
            .required(true)
            .ok(String.class);

    private static final ConfigOption<String> REGEX_CONTENT = ConfigOptions.key("regexContent")
            .description("正则表达式内容")
            .required(true)
            .ok(String.class);


    public String getRegexMatchField() {
        return this.getVal(REGEX_MATCH_FIELD);
    }

    public String getRegexContent() {
        return this.getVal(REGEX_CONTENT);
    }

}
