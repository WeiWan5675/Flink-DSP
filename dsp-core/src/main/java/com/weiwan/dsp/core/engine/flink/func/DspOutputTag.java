package com.weiwan.dsp.core.engine.flink.func;

import com.weiwan.dsp.api.plugin.Plugin;
import com.weiwan.dsp.api.pojo.DataRecord;
import com.weiwan.dsp.core.plugin.RichSplitPlugin;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.OutputTag;

import java.io.Serializable;

/**
 * @author: xiaozhennan
 * @date: 2021/6/17 17:52
 * @description: Flink数据流拆分tag
 */
public class DspOutputTag implements Serializable {

    private static final long serialVersionUID = -4168558866699863011L;

    private OutputTag<DataRecord> outputTag;
    private RichSplitPlugin splitPlugin;
    private String tagNum;

    public DspOutputTag(String tagNum, Plugin plugin) {
        this.splitPlugin = (RichSplitPlugin) plugin;
        this.tagNum = tagNum;
        this.outputTag = new OutputTag<DataRecord>(this.tagNum){};
    }

    public boolean matchTag(DataRecord record) throws Exception {
        if (record == null) return false;
        if (splitPlugin != null) {
            return splitPlugin.splitMatch(record);
        }
        return false;
    }

    public OutputTag<DataRecord> getTag() {
        return outputTag;
    }

    public void out(ProcessFunction.Context ctx, DataRecord value) {
        if (outputTag != null && value != null) {
            ctx.output(outputTag, value);
        }
    }

    public void open() {
        this.splitPlugin.open(splitPlugin.getPluginConfig());
    }

    public void close(){
        this.splitPlugin.close();
    }
}
