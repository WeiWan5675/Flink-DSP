package com.weiwan.dsp.console.controller;

import com.alibaba.fastjson.JSONObject;
import com.weiwan.dsp.api.constants.DspConstants;
import com.weiwan.dsp.common.constants.Constants;
import com.weiwan.dsp.console.model.Result;
import com.weiwan.dsp.console.model.query.MetricQuery;
import com.weiwan.dsp.console.model.vo.ApplicationOverviewVo;
import com.weiwan.dsp.console.service.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Properties;

/**
 * @Author: xiaozhennan
 * @Date: 2021/11/14 17:14
 * @ClassName: MetricsController
 * @Description: MetricsController
 **/
@RestController
@RequestMapping("metrics")
public class MetricsController {

    @Autowired
    @Qualifier("consoleDefaultProperties")
    private Properties consoleDefaultProperties;

    @Autowired
    private MetricsService metricsService;

    @GetMapping("/report")
    public Result<JSONObject> pushMetrics(JSONObject jsonObject) {
        String appVersion = (String) consoleDefaultProperties.getOrDefault(DspConstants.DSP_VERSION, Constants.UNKNOWN);
        String appBuildVersion = (String) consoleDefaultProperties.getOrDefault(DspConstants.DSP_BUILD_VERSION, Constants.UNKNOWN);
        String appName = (String) consoleDefaultProperties.getOrDefault(DspConstants.DSP_NAME, Constants.UNKNOWN);
        String flinkVersion = (String) consoleDefaultProperties.getOrDefault(DspConstants.DSP_BUILD_FLINK_VERSION, Constants.UNKNOWN);
        String hadoopVersion = (String) consoleDefaultProperties.getOrDefault(DspConstants.DSP_BUILD_HADOOP_VERSION, Constants.UNKNOWN);
        String jdkVersion = (String) consoleDefaultProperties.getOrDefault(DspConstants.DSP_BUILD_JDK_VERSION, Constants.UNKNOWN);
        String mavenVersion = (String) consoleDefaultProperties.getOrDefault(DspConstants.DSP_BUILD_MAVEN_VERSION, Constants.UNKNOWN);
        JSONObject obj = new JSONObject();
        obj.put("name", appName);
        obj.put("version", appVersion);
        obj.put("buildVersion", appBuildVersion);
        obj.put("flinkVersion", flinkVersion);
        obj.put("hadoopVersion", hadoopVersion);
        obj.put("jdkVersion", jdkVersion);
        obj.put("mavenVersion", mavenVersion);
        return Result.success(obj);
    }

    @GetMapping("/application/overview")
    public Result<ApplicationOverviewVo> getApplicationOverview(){
        MetricQuery metricQuery = new MetricQuery();
        ApplicationOverviewVo vo = metricsService.getApplicationOverview(metricQuery);
        return Result.success(vo);
    }
}
