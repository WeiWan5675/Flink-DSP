package com.weiwan.dsp.console.util;

import com.weiwan.dsp.api.enums.MetricKey;
import junit.framework.TestCase;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @Author: xiaozhennan
 * @Date: 2021/10/27 23:14
 * @ClassName: MetricKeyTest
 * @Description:
 **/
public class MetricKeyTest extends TestCase {

    @Test
    public void testGetMetricKey(){
        String testName = MetricKey.replacePN(MetricKey.PROCESS_PLUGIN_SPENT_TIME, "testName");
        System.out.println(testName);
    }

}