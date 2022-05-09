package com.weiwan.dsp.test;

import com.weiwan.dsp.api.config.core.*;
import com.weiwan.dsp.api.config.flow.NodeConfig;
import com.weiwan.dsp.api.plugin.PluginConfig;
import com.weiwan.dsp.api.enums.NodeType;
import com.weiwan.dsp.common.config.ConfigOption;
import com.weiwan.dsp.common.config.ConfigOptions;
import com.weiwan.dsp.common.utils.FileUtil;
import com.weiwan.dsp.common.utils.ObjectUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertTrue;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/15 16:35
 * @description:
 */
public class ConfigOptionTest {

    private PluginConfig pluginConfig;

    @Before
    public void optionCreate() {
        HashMap hashMap = new HashMap();
        hashMap.put("testVar1", 'A');
        hashMap.put("testVar2", 1);
        hashMap.put("testVar3","CCCCCCCCC");
        hashMap.put("testVar4", 1.1d);
        hashMap.put("testVar5", 1.23f);
        hashMap.put("testVar6", new ArrayList<>());
        hashMap.put("testVar7", new Date());
        pluginConfig = new PluginConfig(new HashMap());
        ConfigOption<String> testKey = ConfigOptions.key("testKey").ok(String.class);
    }

    @Test
    public void testnewOption() throws IOException {
        String content = FileUtil.readFileContent("G:\\project\\Flink-DSP\\test.json");
        DspConfig dspConfig = ObjectUtil.deSerialize(content, DspConfig.class);
        System.out.println(dspConfig);
        CoreConfig core = dspConfig.getCore();
        EngineConfig engineConfig = core.getEngineConfig();
        Configs engineConfigs = engineConfig.getEngineConfigs();

        Integer val = engineConfigs.getVal(FlinkConfigs.JOB_PARALLELISM);
        Boolean val1 = engineConfigs.getVal(FlinkConfigs.CLASSLOADER_LEAKED_CHECK);
        Long val2 = engineConfigs.getVal(FlinkConfigs.JOB_CHECKPOINT_INTERVAL);
        Boolean val3 = engineConfigs.getVal(FlinkConfigs.JOB_CHECKPOINT_ENABLE);

        System.out.println("");

    }

    @Test
    public void testGetOption() {
        Object var1 = null;
        Object var2 = null;
        try {
            var1 = pluginConfig.getVal(PluginConfig.PLUGIN_TYPE);
            var2 = pluginConfig.getVal(PluginConfig.PLUGIN_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertTrue(var1 != null);
        assertTrue(var2 != null);

        System.out.println(var1);
        System.out.println(var2);
    }


    @Test
    public void testOptionType() {

    }

}
