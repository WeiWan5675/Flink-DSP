package com.weiwan.dsp.core.plugin;

import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;

/**
 * @author: xiaozhennan
 * @description:
 */
public class DspPluginManagerTest extends TestCase {

    @Test
    public void testRegisterPlugin() {
        DspPluginManager dspPluginManager = DspPluginManager.getInstance();
        File file0 = new File("D:\\develop\\github\\Flink-DSP\\target\\Flink-DSP-1.0.0\\dsp-plugin-example-1.0.jar");
        File file1 = new File("D:\\develop\\github\\Flink-DSP\\target\\Flink-DSP-1.0.0\\plugin");
        File file2 = new File("./dsp-plugin-example-1.0.jar");
        dspPluginManager.registerPlugins(file0, file1, file2);
    }



    @Test
    public void testNewInstancePluginManager() {
        DspPluginManager dspPluginManager = DspPluginManager.getInstance();
        System.out.println(dspPluginManager);
    }



    @Test
    public void testPluginManager(){
        DspPluginManager dspPluginManager = DspPluginManager.getInstance();
        PluginRepoV2 pluginRepo = dspPluginManager.getPluginRepo();

    }

}