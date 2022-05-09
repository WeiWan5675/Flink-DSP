package com.weiwan.dsp.core.nodes;

import com.weiwan.dsp.api.config.flow.NodeConfig;
import com.weiwan.dsp.api.node.WriterNode;
import com.weiwan.dsp.api.plugin.Plugin;
import com.weiwan.dsp.api.plugin.PluginContainer;
import com.weiwan.dsp.core.plugin.container.InputPluginContainer;
import com.weiwan.dsp.core.plugin.container.OutputPluginContainr;
import com.weiwan.dsp.core.plugin.container.PluginRuningContainer;

import java.util.List;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/16 13:26
 * @description:
 */
public class DspWriterNode extends BaseNode implements WriterNode {


    @Override
    public PluginContainer getPluginContainer() {
        List<Plugin> plugins = getPlugins();
        NodeConfig nodeConfig = getNodeConfig();
        OutputPluginContainr pluginRuningContainer = new OutputPluginContainr(nodeConfig, plugins);
        return pluginRuningContainer;
    }
}
