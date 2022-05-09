package com.weiwan.dsp.client;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.weiwan.dsp.api.config.core.DspConfig;
import com.weiwan.dsp.core.utils.DspConfigFactory;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/5/21 19:26
 * @description:
 */
public class DspConfigManagerTest {



    @Test
    public void testJobConfigToJson() throws IOException {
        ObjectMapper MAPPER = new ObjectMapper();
        DspConfig load = DspConfigFactory.load(new File("G:\\project\\Flink-DSP\\conf\\example\\job-flow.properties"));


//        JobConfig jobConfig1 = MAPPER.readValue(new File("G:\\project\\Flink-DSP\\conf\\example\\job-flow.json"), JobConfig.class);
//        System.out.println(jobConfig1);
        String s = MAPPER.writeValueAsString(load);
        System.out.println(s);
    }
}