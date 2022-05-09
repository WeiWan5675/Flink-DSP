package com.weiwan.dsp.plugins.split;

import com.weiwan.dsp.api.pojo.DataRecord;
import com.weiwan.dsp.core.plugin.RichSplitPlugin;
import com.weiwan.dsp.plugins.config.RegexSplitPluginConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

/**
 * @author: xiaozhennan
 * @date: 2021/6/21 18:59
 * @description:
 */
public class RegexSplitPlugin extends RichSplitPlugin<RegexSplitPluginConfig> {

    private static final Logger logger = LoggerFactory.getLogger(RegexSplitPlugin.class);

    private String regexMatchField;
    private String regexContent;

    @Override
    public void init(RegexSplitPluginConfig config) {
        logger.debug("Regex field match split plugin init function running.");
        //获取正则表达式匹配字段和内容
        this.regexMatchField = config.getRegexMatchField();
        this.regexContent = config.getRegexContent();
    }

    @Override
    public boolean match(DataRecord record) {
        //获取record中的匹配字段值, 与正则表达式匹配
        if (StringUtils.isNotBlank(this.regexMatchField)) {
            String regexValue = record.getString(this.regexMatchField);
            if (StringUtils.isNotBlank(regexValue)) {
                return Pattern.matches(this.regexContent, regexValue);
            }
        }
        return false;
    }

    @Override
    public void stop() {
        logger.debug("Regex field match split plugin stop function running.");
    }
}
