package com.weiwan.dsp.core.engine.flink.func;

import com.weiwan.dsp.api.context.EngineContext;
import com.weiwan.dsp.api.plugin.PluginContainer;
import com.weiwan.dsp.api.pojo.DataRecord;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.dag.Transformation;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.transformations.UnionTransformation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: xiaozhennan
 * @date: 2021/6/17 14:17
 * @description: 数据流合并算子(本地算子,不参与Flink运行)
 */
public class DataStreamUnioner {
    private EngineContext context;
    private PluginContainer container;
    private List<DataStream<DataRecord>> waitUnionStreams;
    private StreamExecutionEnvironment environment;

    public DataStreamUnioner(EngineContext context, PluginContainer container, StreamExecutionEnvironment environment) {
        this.environment = environment;
        this.container = container;
        this.context = context;
       this.waitUnionStreams = new ArrayList<>();
    }

    public void add(DataStream<DataRecord> stream) {
        waitUnionStreams.add(stream);
    }


    public DataStream union() {
        List<Transformation<DataRecord>> unionedTransforms = new ArrayList<>();
        //检查两个流类型一致
        checkStreamType();

        for (DataStream<DataRecord> newStream : waitUnionStreams) {
            unionedTransforms.add(newStream.getTransformation());
        }
        return new DataStream<>(this.environment, new UnionTransformation<>(unionedTransforms));
    }

    private void checkStreamType() {
        TypeInformation type = null;
        for (DataStream waitUnionStream : waitUnionStreams) {
            if (type == null) {
                type = waitUnionStream.getType();
            } else {
                if (type.equals(waitUnionStream.getType())) {
                    continue;
                } else {
                    throw new IllegalArgumentException(
                            "Cannot union streams of different types: "
                                    + type
                                    + " and "
                                    + waitUnionStream.getType());
                }
            }
        }
    }
}
