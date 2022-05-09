package com.weiwan.dsp.core.plugin.container;

import com.weiwan.dsp.api.config.flow.NodeConfig;
import com.weiwan.dsp.api.plugin.Plugin;
import com.weiwan.dsp.api.plugin.SplitPlugin;
import com.weiwan.dsp.api.resolve.UnresolvedDataCollector;
import com.weiwan.dsp.core.engine.metric.MetricCenter;
import com.weiwan.dsp.core.plugin.RichSplitPlugin;
import com.weiwan.dsp.api.pojo.DataRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: xiaozhennan
 * @date: 2021/6/17 14:06
 * @description:
 */
public class SplitPluginContainr extends PluginRuningContainer{

    private UnresolvedDataCollector unresolvedDataCollector;
    private MetricCenter metricCenter;
    private List<SplitPlugin<DataRecord>> splitPlugins = new ArrayList<>();
    private boolean runing;

    @Override
    protected void open() {
        this.unresolvedDataCollector = this.getUnresolvedDataCollector();
        this.metricCenter = this.getMetricCenter();
        List<Plugin> plugins = this.getPlugins();
        plugins.forEach(p -> {
            RichSplitPlugin splitPlugin = (RichSplitPlugin) p;
            splitPlugin.setContext(getContext());
            splitPlugin.open(splitPlugin.getPluginConfig());
            splitPlugins.add(splitPlugin);
        });
        this.runing = true;
    }

    @Override
    protected void stop() {
        //do nothing
    }

    public SplitPluginContainr(NodeConfig nodeConfig, List<Plugin> plugins) {
        super(nodeConfig, plugins);
    }
}
