package com.weiwan.dsp.core.plugin;

import com.weiwan.dsp.api.plugin.PluginConfig;
import com.weiwan.dsp.api.plugin.UnionPlugin;
import com.weiwan.dsp.api.pojo.DataRecord;

/**
 * @author: xiaozhennan
 * @date: 2021/6/21 19:01
 * @description:
 */
public abstract class RichUnionPlugin<T extends PluginConfig> extends AbstractPlugin<T> implements UnionPlugin<DataRecord, DataRecord> {


    @Override
    public void open(PluginConfig config) {
        this.init((T) config);
    }

    @Override
    public void close() {

    }

    @Override
    public void union(DataRecord r1, DataRecord r2) {
        throw new UnsupportedOperationException("Union插件不需要进行实现.");
    }

    @Override
    public void meage(DataRecord r1, DataRecord r2) {
        throw new UnsupportedOperationException("Union插件不需要进行实现.");
    }
}
