package com.weiwan.dsp.core.nodes;

import com.weiwan.dsp.api.config.flow.NodeConfig;
import com.weiwan.dsp.api.node.ProcessNode;
import com.weiwan.dsp.api.plugin.Plugin;
import com.weiwan.dsp.api.plugin.PluginContainer;
import com.weiwan.dsp.core.plugin.container.ProcessPluginContainer;

import java.util.List;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/16 18:56
 * @description:
 */
public class DspProcessNode extends BaseNode implements ProcessNode {

    @Override
    public PluginContainer getPluginContainer() {
        List<Plugin> plugins = getPlugins();
        NodeConfig nodeConfig = getNodeConfig();
        return new ProcessPluginContainer(nodeConfig, plugins);
    }
}
