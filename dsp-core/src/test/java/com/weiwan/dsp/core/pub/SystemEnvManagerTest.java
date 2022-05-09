package com.weiwan.dsp.core.pub;

import junit.framework.TestCase;
import org.junit.Test;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @Author: xiaozhennan
 * @Date: 2021/10/20 20:16
 * @ClassName: SystemEnvManagerTest
 * @Description:
 **/
public class SystemEnvManagerTest extends TestCase {

    @Test
    public void testGetDspRuntimeLibs() {

        Map<String, String> tmp = new HashMap<String, String>();
        tmp.put("dsp.base.dir", "G:\\project\\Flink-DSP\\target\\Flink-DSP-1.0.0");
        tmp.put("dsp.lib.dir", "G:\\project\\Flink-DSP\\target\\Flink-DSP-1.0.0\\lib");
        SystemEnvManager instance = SystemEnvManager.getInstance(tmp);

        List<URL> dspRuntimeLibs = instance.getDspRuntimeLibs();


        System.out.println(dspRuntimeLibs);

    }
}