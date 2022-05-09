package com.weiwan.dsp.core.plugin;

import com.weiwan.dsp.api.enums.MetricKey;
import com.weiwan.dsp.api.metrics.MetricCounter;
import com.weiwan.dsp.api.plugin.PluginConfig;
import com.weiwan.dsp.api.context.EngineContext;
import com.weiwan.dsp.api.plugin.InputPlugin;
import com.weiwan.dsp.api.plugin.Launcher;
import com.weiwan.dsp.core.engine.metric.MetricCenter;
import com.weiwan.dsp.api.pojo.DataRecord;
import com.weiwan.dsp.common.config.ConfigOption;
import com.weiwan.dsp.common.config.ConfigOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/3/26 10:59
 * @description:
 */
public abstract class RichInputPlugin<T extends PluginConfig> extends AbstractPlugin<T> implements InputPlugin<DataRecord> {
    private static final Logger _logger = LoggerFactory.getLogger(RichInputPlugin.class);
    private MetricCenter metricCenter;
    private boolean isBatch;
    private MetricCounter inputPluginSuccNum;
    private MetricCounter inputPluginFailNum;
    public static final ConfigOption<Boolean> BATCH_MODE = ConfigOptions.key("batchMode")
            .defaultValue(false)
            .required(false)
            .description("批拉取模式")
            .ok(Boolean.class);

    @Override
    public void open(PluginConfig config) {
        _logger.info("Initialize input plugin start, plugin name: {}", getPluginName());
        EngineContext context = getContext();
        this.metricCenter = (MetricCenter) context.getMetricManager(getNodeId());
        this.isBatch = config.getVal(BATCH_MODE);
        registerPluginMetrics();
        this.init((T) config);
        _logger.info("Initialize input plugin complete, plugin name: {}", getPluginName());
    }

    private void registerPluginMetrics() {
        //插件成功记录数
        this.inputPluginSuccNum = this.metricCenter.registerMetric(MetricKey.replacePN(MetricKey.INPUT_PLUGIN_SUCS_NUM, getPluginName()), MetricCounter.class);
        //插件失败记录数
        this.inputPluginFailNum = this.metricCenter.registerMetric(MetricKey.replacePN(MetricKey.INPUT_PLUGIN_FAIL_NUM, getPluginName()), MetricCounter.class);
    }


    @Override
    public void input(Launcher<DataRecord> launcher) {
        if (launcher == null) {
            throw new RuntimeException("Unable to read data, data launcher not properly initialized");
        }
        while (launcher.isOpen()) {
            try {
                if (!this.readEnd()) {
                    long startReadTime = System.currentTimeMillis();
                    long count = 1;
                    try {
                        if (isBatch) {
                            List<DataRecord> dataRecords = batchRead();
                            count = dataRecords.size();
                            if (dataRecords != null && !dataRecords.isEmpty()) {
                                launcher.launch(dataRecords);
                            }
                        } else {
                            DataRecord record = read();
                            if (record != null) launcher.launch(record);
                        }
                        inputPluginSuccNum.inc(count);
                    } catch (Exception e) {
                        inputPluginFailNum.inc(count);
                    } finally {
                        long endReadTime = System.currentTimeMillis();
                        recordSpendTime(endReadTime - startReadTime);
                        recordProcessCapacity(count);
                    }
                } else {
                    break;
                }
            } catch (Exception e) {
                _logger.error("An exception occurred while reading data", e);
                break;
            }
        }

    }

    protected abstract DataRecord read() throws Exception;

    protected abstract List<DataRecord> batchRead() throws Exception;

    protected abstract boolean readEnd() throws Exception;

    @Override
    public void close() {
        _logger.info("Close input plugin start, plugin name: {}", getPluginName());
        try {
            this.stop();
        } catch (Exception e) {
            _logger.warn("Close the data input plugin: {} an exception occurred.", getPluginName(), e);
        }
        _logger.info("Close input plugin complete plugin name: {}", getPluginName());
    }
}
