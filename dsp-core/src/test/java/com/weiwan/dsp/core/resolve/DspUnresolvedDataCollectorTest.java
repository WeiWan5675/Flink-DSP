package com.weiwan.dsp.core.resolve;

import com.weiwan.dsp.api.plugin.PluginConfig;
import com.weiwan.dsp.api.plugin.PluginControl;
import junit.framework.TestCase;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * @Author: xiaozhennan
 * @Date: 2021/6/2 23:10
 * @Package: com.weiwan.dsp.core.resolve
 * @ClassName: DspUnresolvedDataCollectorTest
 * @Description:
 **/
public class DspUnresolvedDataCollectorTest extends TestCase {

    @Test
    public void testNewInstanceUnresolvedCollector() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        String clazz = "com.weiwan.dsp.core.resolve.log.LogUnresolvedDataCollector";

        Class<?> aClass = Class.forName(clazz);

        DspUnresolvedDataCollector collector = (DspUnresolvedDataCollector) aClass.newInstance();

        Field maxSamplingRecord = collector.getClass().getSuperclass().getDeclaredField("maxSamplingRecord");
        Field samplingInterval = collector.getClass().getSuperclass().getDeclaredField("samplingInterval");
        Field nodeId = collector.getClass().getSuperclass().getDeclaredField("nodeId");
        Field nodeName = collector.getClass().getSuperclass().getDeclaredField("nodeName");
        Field jobName = collector.getClass().getSuperclass().getDeclaredField("jobName");
        maxSamplingRecord.setAccessible(true);
        samplingInterval.setAccessible(true);
        nodeId.setAccessible(true);
        nodeName.setAccessible(true);
        jobName.setAccessible(true);
        maxSamplingRecord.setLong(collector, 10000);
        samplingInterval.setLong(collector, 30);
        nodeId.set(collector, "testJobId");
        nodeName.set(collector, "testNodeName");
        jobName.set(collector, "testJobName");

        PluginControl control = (PluginControl) collector;
        control.open(new PluginConfig(new HashMap()));
//        DataRecord<String> dr = new DataRecord<>();
//        dr.setRow("");
//        collector.collect(UnresolvedType.FORMAT,dr);
    }

}