package com.weiwan.dsp.console.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.weiwan.dsp.api.enums.ApplicationState;
import com.weiwan.dsp.api.enums.ApplicationType;
import com.weiwan.dsp.api.enums.EngineMode;
import com.weiwan.dsp.common.constants.Constants;
import com.weiwan.dsp.api.constants.DspConstants;
import com.weiwan.dsp.console.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/27 13:56
 * @description:
 */
@RestController
@RequestMapping("system")
public class SystemController {

    @Autowired
    @Qualifier("consoleDefaultProperties")
    private Properties consoleDefaultProperties;

    @GetMapping("/version")
    public Result<JSONObject> getDspVersion() {
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
