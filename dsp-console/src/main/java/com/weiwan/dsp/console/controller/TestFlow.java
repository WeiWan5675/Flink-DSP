package com.weiwan.dsp.console.controller;

import com.weiwan.dsp.api.config.flow.FlowConfig;
import com.weiwan.dsp.common.utils.FileUtil;
import com.weiwan.dsp.common.utils.ObjectUtil;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @Author: xiaozhennan
 * @Date: 2022/3/30 23:04
 * @Package: com.weiwan.dsp.console.controller
 * @ClassName: TestFlow
 * @Description: aaa
 **/
public class TestFlow {

    public static void main(String[] args) throws IOException, URISyntaxException {
        String content = FileUtil.readFileContent(new URL("file:\\G:\\project\\Flink-DSP\\dsp-console\\src\\test\\resources\\job_flow.json"));
        FlowConfig flowConfig = ObjectUtil.deSerialize(content, FlowConfig.class);
        System.out.println(flowConfig);
    }
}
