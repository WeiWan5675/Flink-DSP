package com.weiwan.dsp.core.resolve.http;

import com.weiwan.dsp.api.config.core.CollectorHandlerConfigs;
import com.weiwan.dsp.api.config.core.UnresolvedCollectorConfig;
import com.weiwan.dsp.common.config.ConfigOption;
import com.weiwan.dsp.common.config.ConfigOptions;
import org.omg.CORBA.PUBLIC_MEMBER;

import java.util.List;

/**
 * @Author: xiaozhennan
 * @Date: 2021/8/7 22:26
 * @Package: com.weiwan.dsp.core.resolve.http
 * @ClassName: HttpUnresolvedConfig
 * @Description: Http未解析数据收集器配置
 **/
public class HttpUnresolvedConfig extends CollectorHandlerConfigs {
    public static final ConfigOption<String> HTTP_HOSTNAME = ConfigOptions
            .key("hostname")
            .fullKey("dsp.core.unresolved.handlerConfigs.hostname")
            .required(false)
            .description("http未解析数据处理器数据上报主机地址")
            .defaultValue("127.0.0.1").ok(String.class);
    public static final ConfigOption<Integer> HTTP_PORT = ConfigOptions
            .key("port")
            .fullKey("dsp.core.unresolved.handlerConfigs.port")
            .required(false)
            .description("http未解析数据处理器数据上报主机端口")
            .defaultValue(9875).ok(Integer.class);
    public static final ConfigOption<String> HTTP_URL = ConfigOptions
            .key("url")
            .fullKey("dsp.core.unresolved.handlerConfigs.url")
            .required(false)
            .description("http未解析数据处理器数据上报请求接口, 如果使用域名, 则需要提供完整的接口地址 (会覆盖host和port参数)")
            .defaultValue("/unresolved/report").ok(String.class);

    @Override
    public void loadOptions(List<ConfigOption> options) {
        super.loadOptions(options);
        options.add(HTTP_HOSTNAME);
        options.add(HTTP_PORT);
        options.add(HTTP_URL);
    }

    public String getHostname() {
        return this.getVal(HTTP_HOSTNAME);
    }

    public Integer getPort() {
        return this.getVal(HTTP_PORT);
    }

    public String getUrl() {
        return this.getVal(HTTP_URL);
    }
}
