package com.weiwan.dsp.core.nodes;

import com.weiwan.dsp.api.config.flow.NodeConfig;
import com.weiwan.dsp.api.node.SpliterNode;
import com.weiwan.dsp.api.plugin.Plugin;
import com.weiwan.dsp.api.plugin.PluginContainer;
import com.weiwan.dsp.core.plugin.container.SplitPluginContainr;

import java.util.List;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/16 18:57
 * @description:
 */
public class DspSpliterNode extends BaseNode implements SpliterNode {
    @Override
    public PluginContainer getPluginContainer() {
        List<Plugin> plugins = getPlugins();
        NodeConfig nodeConfig = getNodeConfig();
        SplitPluginContainr splitPluginContainr = new SplitPluginContainr(nodeConfig, plugins);
        return splitPluginContainr;
    }
}
