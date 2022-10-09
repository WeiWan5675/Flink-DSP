package com.weiwan.dsp.core.plugin;

import com.weiwan.dsp.api.plugin.PluginConfig;
import com.weiwan.dsp.api.enums.PluginType;
import com.weiwan.dsp.api.plugin.Plugin;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;

/**
 * @author: xiaozhennan
 * @description:
 */
public class PluginRepoTest extends TestCase {


    @Test
    public void testPluginRepo() throws Exception {
        DspPluginManager pluginManager = DspPluginManager.getInstance();
        File file = new File("D:\\develop\\github\\Flink-DSP\\target\\Flink-DSP-1.0.0\\plugin");
        File[] files = file.listFiles();

        pluginManager.registerPlugins(files);
        PluginRepo pluginRepo = pluginManager.getPluginRepo();
        PluginMetaData search = pluginRepo.search("com.weiwan.dsp.plugins.input.ExampleInputPlugin");
//        boolean disable = pluginRepo.disable(search.getPluginClass());
//        boolean block = pluginRepo.block(search.getPluginClass());
//
//        for (File jarFile : files) {
//            Map<String, PluginMetaData> delete =
//                    pluginRepo.delete(jarFile.toURI().toURL().toString());
//        }

        //这种是web端用
        PluginFactory instance = PluginFactory.getInstance();
        PluginConfig pluginConfig = new PluginConfig();
        pluginConfig.setVal(PluginConfig.PLUGIN_NAME, "test");
        pluginConfig.setVal(PluginConfig.PLUGIN_CLASS, "com.weiwan.dsp.plugins.input.ExampleInputPlugin");
        pluginConfig.setVal(PluginConfig.PLUGIN_TYPE, PluginType.INPUT);

        Plugin plugin = instance.createPlugin(pluginConfig);

        System.out.println(plugin);
        System.out.println(search);
    }

}