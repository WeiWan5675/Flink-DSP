package com.weiwan.dsp.api.config.core;

import com.weiwan.dsp.common.config.AbstractConfig;
import com.weiwan.dsp.common.config.ConfigOption;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/12 15:56
 * @description:
 */
public class MetricConfig implements Serializable {
    private String reporter;
    private Long interval;
    private String host;
    private Integer port;

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public Long getInterval() {
        return interval;
    }

    public void setInterval(Long interval) {
        this.interval = interval;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
