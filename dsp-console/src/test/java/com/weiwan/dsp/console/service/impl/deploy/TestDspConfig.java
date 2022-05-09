package com.weiwan.dsp.console.service.impl.deploy;

import com.alibaba.fastjson.JSONObject;
import com.weiwan.dsp.api.config.core.DspConfig;
import com.weiwan.dsp.core.utils.DspConfigFactory;
import org.junit.Test;

import java.io.IOException;

/**
 * @Author: xiaozhennan
 * @Date: 2021/10/31 15:50
 * @ClassName: TestDspConfig
 * @Description:
 **/
public class TestDspConfig {


    @Test
    public void testDspConfigSeri() throws IOException {
        DspConfig dspConfig = DspConfigFactory.load("G:\\project\\Flink-DSP\\dsp-console\\src\\test\\resources\\job_standalone.json");
        String s = DspConfigFactory.dspConfigToContent(dspConfig);
        JSONObject jsonObject = JSONObject.parseObject(s);
    }
}
