package com.weiwan.dsp.console.service.impl;

import com.weiwan.dsp.api.config.core.CoreConfig;
import com.weiwan.dsp.api.config.core.CustomConfig;
import com.weiwan.dsp.api.config.core.DspConfig;
import com.weiwan.dsp.api.config.core.JobConfig;
import com.weiwan.dsp.api.config.flow.FlowConfig;
import com.weiwan.dsp.common.utils.ObjectUtil;
import com.weiwan.dsp.core.utils.DspConfigFactory;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @Author: xiaozhennan
 * @Date: 2021/11/1 0:17
 * @ClassName: ApplicationServiceImplTest
 * @Description:
 **/
public class ApplicationServiceImplTest extends TestCase {


    @Test
    public void testApplicationVo() throws IOException {

        DspConfig load = DspConfigFactory.load("G:\\project\\Flink-DSP\\dsp-console\\src\\test\\resources\\job_standalone.json");

        CoreConfig core = load.getCore();
        String coreContent = ObjectUtil.serialize(core);
        System.out.println(coreContent);
        JobConfig job = load.getJob();
        String jobContent = ObjectUtil.serialize(job);
        System.out.println(jobContent);
        CustomConfig custom = load.getCustom();
        String customContent = ObjectUtil.serialize(custom);
        System.out.println(customContent);
        FlowConfig flow = load.getFlow();

    }

}