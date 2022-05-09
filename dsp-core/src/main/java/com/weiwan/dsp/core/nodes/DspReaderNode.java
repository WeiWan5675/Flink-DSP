package com.weiwan.dsp.core.nodes;

import com.weiwan.dsp.api.config.flow.NodeConfig;
import com.weiwan.dsp.api.node.ReaderNode;
import com.weiwan.dsp.api.plugin.Plugin;
import com.weiwan.dsp.api.plugin.PluginContainer;
import com.weiwan.dsp.api.pojo.DataRecord;
import com.weiwan.dsp.core.plugin.container.InputPluginContainer;
import com.weiwan.dsp.core.plugin.container.PluginRuningContainer;

import java.util.List;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/3/21 16:45
 * @description:
 */
public class DspReaderNode extends BaseNode implements ReaderNode<DataRecord> {


    @Override
    public PluginContainer getPluginContainer() {
        /**
         * 这里返回得应该是插件得运行包装类.因为在这里执行时,实际上插件得运行顺序和内容已经确定了
         * 这里不确定得是在那个框架运行?,谁来调用得问题,所以我们只需要把调用和运行得操作交给引擎就可以
         * 返回一个插件运行容器,具体得运行还是由引擎来决定,这样就可以支持不同得引擎调用了.
         */
        List<Plugin> plugins = getPlugins();
        NodeConfig nodeConfig = getNodeConfig();
        PluginRuningContainer pluginRuningContainer = new InputPluginContainer(nodeConfig, plugins);
        return pluginRuningContainer;
    }

}
