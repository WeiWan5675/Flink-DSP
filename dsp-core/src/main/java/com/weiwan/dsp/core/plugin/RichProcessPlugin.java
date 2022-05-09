package com.weiwan.dsp.core.plugin;

import com.weiwan.dsp.api.enums.MetricKey;
import com.weiwan.dsp.api.metrics.CalculationFormula;
import com.weiwan.dsp.api.metrics.MetricCounter;
import com.weiwan.dsp.api.metrics.MetricGauge;
import com.weiwan.dsp.api.plugin.PluginConfig;
import com.weiwan.dsp.api.context.EngineContext;
import com.weiwan.dsp.api.plugin.ProcessPlugin;
import com.weiwan.dsp.core.engine.metric.MetricCenter;
import com.weiwan.dsp.api.pojo.DataRecord;
import com.weiwan.dsp.api.resolve.UnresolvedDataCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/9 16:02
 * @description:
 */
public abstract class RichProcessPlugin<T extends PluginConfig> extends AbstractPlugin<T> implements ProcessPlugin<DataRecord, DataRecord> {


    private static final Logger _logger = LoggerFactory.getLogger(RichProcessPlugin.class);
    private volatile boolean runing;
    private UnresolvedDataCollector unresolvedDataCollector;
    private MetricCenter metricCenter;
    private MetricGauge<Long> processPluginSpentTime;
    private MetricCounter processPluginSucsNum;
    private MetricCounter processPluginFailNum;

    @Override
    public void open(PluginConfig config) {
        _logger.info("Initialize process plugin start, plugin name: {}", getPluginName());
        EngineContext context = getContext();
        this.unresolvedDataCollector = (UnresolvedDataCollector) context.getUnresolvedCollector(getNodeId());
        this.metricCenter = (MetricCenter) context.getMetricManager(getNodeId());
        registerPluginMetrics();
        init((T) config);
        this.runing = true;
        _logger.info("Initialize process plugin complete, plugin name: {}", getPluginName());
    }

    private void registerPluginMetrics() {
        //插件处理耗时
        this.processPluginSpentTime = this.metricCenter.registerMetric(MetricKey.replacePN(MetricKey.PROCESS_PLUGIN_SPENT_TIME, getPluginName()), MetricGauge.class);
        this.processPluginSpentTime.formula(new CalculationFormula<Long>() {
            private long lastCount;
            private long lastTime;

            @Override
            public Long calculation() {
                final long currentCount = getProcessCapacity();
                final long spendTime = getSpendTime();
                if (lastTime == 0 || lastCount == 0) {
                    this.lastTime = spendTime;
                    this.lastCount = currentCount;
                    return spendTime;
                }
                long c = currentCount - lastCount;
                if (spendTime == 0 || currentCount == 0) return 1L;
                long t = spendTime - lastTime;
                this.lastTime = spendTime;
                this.lastCount = currentCount;
                return c != 0 ? t / c + 1: 1L;
            }
        });
        //插件成功记录数
        this.processPluginSucsNum = this.metricCenter.registerMetric(MetricKey.replacePN(MetricKey.PROCESS_PLUGIN_SUCS_NUM, getPluginName()), MetricCounter.class);
        //插件失败记录数
        this.processPluginFailNum = this.metricCenter.registerMetric(MetricKey.replacePN(MetricKey.PROCESS_PLUGIN_FAIL_NUM, getPluginName()), MetricCounter.class);
    }

    @Override
    public void close() {
        _logger.info("Close process plugin start, plugin name: {}", getPluginName());
        this.runing = false;
        try {
            this.stop();
            _logger.info("Close process plugin complete plugin name: {}", getPluginName());
        } catch (Exception e) {
            _logger.warn("Close the data processing plugin: {} an exception occurred.", this.getPluginName(), e);
        }
    }

    @Override
    public DataRecord process(DataRecord obj) {
        if (runing) {
            long startProcessTime = System.currentTimeMillis();
            try {
                DataRecord record = this.handle(obj);
                this.processPluginSucsNum.inc();
                return record;
            } catch (Exception e) {
                this.processPluginFailNum.inc();
                throw e;
            } finally {
                long endProcessTime = System.currentTimeMillis();
                recordSpendTime(endProcessTime - startProcessTime);
                recordProcessCapacity(1);
            }
        } else {
            return obj;
        }
    }


    public abstract DataRecord handle(DataRecord record);

}
