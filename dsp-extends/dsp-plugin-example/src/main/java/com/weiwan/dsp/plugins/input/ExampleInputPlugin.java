package com.weiwan.dsp.plugins.input;

import com.alibaba.fastjson.JSONObject;
import com.weiwan.dsp.api.metrics.MetricCounter;
import com.weiwan.dsp.api.metrics.MetricManager;
import com.weiwan.dsp.api.plugin.PluginConfig;
import com.weiwan.dsp.api.pojo.*;
import com.weiwan.dsp.common.utils.FileUtil;
import com.weiwan.dsp.common.utils.ObjectUtil;
import com.weiwan.dsp.core.engine.ext.EngineExtSupport;
import com.weiwan.dsp.core.engine.snapshot.EngineSnapshotFacade;
import com.weiwan.dsp.core.plugin.RichInputPlugin;
import com.weiwan.dsp.api.pojo.DataRecord;
import com.weiwan.dsp.plugins.utils.MockDataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/15 15:52
 * @description:
 */
public class ExampleInputPlugin extends RichInputPlugin<ExampleInputPluginConfig> {

    private static final Logger logger = LoggerFactory.getLogger(ExampleInputPlugin.class);

    private static final String INPUT_OUT_NUM_METRIC_KEY = "example_input_out_num";
    private DspState<Long> state = new DspState(0);
    private MetricCounter exampleInputOutNum = null;
    private List fields = new ArrayList<>();
    private MetricManager metricManager;
    private EngineExtSupport engineExtSupport;
    private EngineSnapshotFacade engineSnapshotFacade;
    private Long maxMockSize = 100000L;
    private Long mockInterval = 5000L;
    private Long mockBatchSize = 10L;
    private Long currentIndex = 0L;

    @Override
    public void init(ExampleInputPluginConfig config) {
        this.fields = config.getFields();
        this.maxMockSize = config.getMaxMockSize();
        this.mockInterval = config.getMockInterval();
        this.mockBatchSize = config.getMockBatchSize();
        this.metricManager = getContext().getMetricManager(getNodeId());
        this.exampleInputOutNum = metricManager.registerCounter(INPUT_OUT_NUM_METRIC_KEY);
        this.engineExtSupport = getEngineExtSupport();
        this.engineSnapshotFacade = engineExtSupport.getEngineSnapshotFacde();
        if (engineSnapshotFacade.isRestore()) {
            this.state = engineSnapshotFacade.getState(getPluginId());
            this.currentIndex = state.getValue();
        } else {
            state = new DspState<Long>(0L);
            engineSnapshotFacade.addState(state, getPluginId());
        }

        engineSnapshotFacade.addSnapshotCallback(getPluginId(), false, (snapshotId, dspState) -> {
            logger.info("Example input plugin checkpoint callback success, current state: {}", dspState.getValue());
        });
        engineSnapshotFacade.addSnapshotFailCallback(getPluginId(), false, (snapshotId, dspState) -> {
            logger.info("Example input plugin checkpoint Fail callback success, current state: {}", dspState.getValue());
        });
    }

    @Override
    protected DataRecord read() throws Exception {
        return getDataRecord();
    }


    @Override
    protected List<DataRecord> batchRead() throws Exception {
        List<DataRecord> dataRecords = new ArrayList<>();
        for (int i = 0; i <= mockBatchSize; i++) {
            dataRecords.add(getDataRecord());
        }
        return dataRecords;
    }

    @Override
    protected boolean readEnd() throws Exception {
        return this.currentIndex >= this.maxMockSize;
    }


    private DataRecord getDataRecord() throws Exception {
        DataRecord dataRecord = new DataRecord();
        for (Object field : fields) {
            JSONObject jsonObject = new JSONObject((Map) field);
            String fieldName = jsonObject.getString("fieldName");
            String fieldType = jsonObject.getString("fieldType");
            Object o = MockDataUtil.mockData(fieldType);
            dataRecord.put(fieldName, o);
        }
        Random rd = new Random();
        int interval = rd.nextInt(mockInterval.intValue());
        TimeUnit.MILLISECONDS.sleep(interval);
        this.exampleInputOutNum.inc();
        this.currentIndex++;
        this.state.update(currentIndex);
        logger.info("example input mock data : {}", dataRecord);
        return dataRecord;
    }

    @Override
    public void stop() {
        //do nothing
    }

    public static void main(String[] args) throws IOException {
        String content = FileUtil.readFileContent("F:\\project\\Flink-DSP\\dsp-extends\\dsp-plugin-example\\src\\main\\resources\\conf\\Dsp-Plugin-Example.json");
        PluginConfig pluginConfig = ObjectUtil.deSerialize(content, PluginConfig.class);
        System.out.println(pluginConfig);
        List<Map> val = pluginConfig.getVal(ExampleInputPluginConfig.FIELDS);
        for (Map field : val) {
            System.out.println(field);
        }

    }


}
