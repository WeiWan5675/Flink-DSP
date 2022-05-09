package com.weiwan.dsp.core.plugin.flink;

import com.weiwan.dsp.api.plugin.OutputPlugin;
import com.weiwan.dsp.api.plugin.PluginConfig;
import com.weiwan.dsp.core.plugin.AbstractPlugin;
import com.weiwan.dsp.api.pojo.DataRecord;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

/**
 * @Author: xiaozhennan
 * @Date: 2022/4/4 19:00
 * @Package: com.weiwan.dsp.core.plugin.flink
 * @ClassName: ConnectorOutputPlugin
 * @Description: 连接器输出插件base类
 **/
public abstract class ConnectorOutputPlugin<T extends PluginConfig> extends AbstractPlugin<T> implements OutputPlugin<DataRecord> {


    public abstract SinkFunction<DataRecord> getSinkFunction(T config);

    @Override
    public void output(DataRecord obj) {

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
