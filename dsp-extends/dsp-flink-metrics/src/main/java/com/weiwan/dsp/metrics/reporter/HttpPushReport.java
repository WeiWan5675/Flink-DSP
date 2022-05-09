package com.weiwan.dsp.metrics.reporter;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.weiwan.dsp.common.utils.CheckTool;
import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.Gauge;
import org.apache.flink.metrics.MetricConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: xiaozhennan
 * @Date: 2021/10/26 21:24
 * @ClassName: HttpPushReport
 * @Description: 一个简单的基于Http协议上报MetricReporter
 **/
public class HttpPushReport extends DspMetricReporter {

    private static final Logger logger = LoggerFactory.getLogger(HttpPushReport.class);
    public static final String REPORT_SERVER_API_FORMAT = "http://%s:%s//metrics/push";
    private String reportUrl;
    private int reportPort;
    public static final String REPORT_PORT_KEY = "port";

    @Override
    public void init(MetricConfig config) {
        logger.info("init the metric reporter");
        reportPort = config.getInteger("port", 9875);
        String host = config.getString("host", "127.0.0.1");
        reportUrl = String.format(REPORT_SERVER_API_FORMAT, host, reportPort);
    }

    @Override
    public void stop() {
        logger.info("stop the metric reporter");
    }

    @Override
    void reportMetrics(DspMetricGroup group) {
        HttpRequest post = HttpUtil.createPost(reportUrl);
        Map<Counter, String> counters = group.getCounters();
        for (Counter counter : counters.keySet()) {
            logger.info("Key: {} Value: {}", counters.get(counter), counter.getCount());
        }

        Map<Gauge<?>, String> gauges = group.getGauges();
        for (Gauge<?> gauge : gauges.keySet()) {
            logger.info("Key: {} Value: {}", gauges.get(gauge), gauge.getValue());
        }
    }
}
