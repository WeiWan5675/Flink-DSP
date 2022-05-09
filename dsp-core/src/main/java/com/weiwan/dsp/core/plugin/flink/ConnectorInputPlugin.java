package com.weiwan.dsp.core.plugin.flink;

import com.weiwan.dsp.api.plugin.InputPlugin;
import com.weiwan.dsp.api.plugin.Launcher;
import com.weiwan.dsp.api.plugin.PluginConfig;
import com.weiwan.dsp.core.plugin.AbstractPlugin;
import com.weiwan.dsp.api.pojo.DataRecord;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

/**
 * @Author: xiaozhennan
 * @Date: 2022/4/4 18:59
 * @Package: com.weiwan.dsp.core.plugin.flink
 * @ClassName: ConnectorInputPlugin
 * @Description: 连接器输入插件base类
 **/
public abstract class ConnectorInputPlugin<T extends PluginConfig> extends AbstractPlugin<T> implements InputPlugin<DataRecord> {

    public abstract SourceFunction<DataRecord> getSourceFunction(T config);

    @Override
    public void input(Launcher<DataRecord> launcher) {
        InputPlugin.super.input(launcher);
    }

    @Override
    public void open(PluginConfig config) {

    }

    @Override
    public void close() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void init(T config) {

    }
}
