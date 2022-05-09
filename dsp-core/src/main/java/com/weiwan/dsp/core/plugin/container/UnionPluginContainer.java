package com.weiwan.dsp.core.plugin.container;

import com.weiwan.dsp.api.config.flow.NodeConfig;
import com.weiwan.dsp.api.plugin.Plugin;

import java.util.List;

/**
 * @author: xiaozhennan
 * @date: 2021/6/17 14:06
 * @description:
 */
public class UnionPluginContainer extends PluginRuningContainer{

    @Override
    protected void open() {

    }

    @Override
    protected void stop() {

    }

    public UnionPluginContainer(NodeConfig nodeConfig, List<Plugin> plugins) {
        super(nodeConfig, plugins);
    }
}
