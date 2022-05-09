package com.weiwan.dsp.console.controller;

import com.alibaba.fastjson.JSONObject;
import com.weiwan.dsp.api.constants.DspConstants;
import com.weiwan.dsp.common.constants.Constants;
import com.weiwan.dsp.console.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Properties;

/**
 * @Author: xiaozhennan
 * @Date: 2021/10/31 12:44
 * @ClassName: MetricsController
 * @Description: Metrics监控相关
 **/
@RestController
@RequestMapping("report")
public class ReportController {

    @Autowired
    @Qualifier("consoleDefaultProperties")
    private Properties consoleDefaultProperties;

    @GetMapping("/push")
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


}
