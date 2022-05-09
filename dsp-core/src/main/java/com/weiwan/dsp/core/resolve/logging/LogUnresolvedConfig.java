package com.weiwan.dsp.core.resolve.logging;

import com.weiwan.dsp.api.config.core.CollectorHandlerConfigs;
import com.weiwan.dsp.common.config.ConfigOption;
import com.weiwan.dsp.common.config.ConfigOptions;

import java.util.List;

/**
 * @Author: xiaozhennan
 * @Date: 2021/8/7 22:22
 * @Package: com.weiwan.dsp.core.resolve.loging
 * @ClassName: LogUnresolvedConfig
 * @Description: 日志未解析数据收集器配置
 **/
public class LogUnresolvedConfig extends CollectorHandlerConfigs {
    public static final ConfigOption<String> LOG_LEVEL = ConfigOptions
            .key("logLevel")
            .fullKey("dsp.core.unresolved.handlerConfigs.logLevel")
            .required(false)
            .description("日志未解析数据处理器的日志输出级别")
            .defaultValue("info").ok(String.class);
    @Override
    public void loadOptions(List<ConfigOption> options) {
        super.loadOptions(options);
        options.add(LOG_LEVEL);
    }

    public String getLogLevel() {
        return this.getVal(LOG_LEVEL);
    }
}
