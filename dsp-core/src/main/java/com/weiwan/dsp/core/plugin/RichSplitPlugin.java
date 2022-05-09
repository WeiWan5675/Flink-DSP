package com.weiwan.dsp.core.plugin;

import com.weiwan.dsp.api.context.EngineContext;
import com.weiwan.dsp.api.enums.MetricKey;
import com.weiwan.dsp.api.metrics.CalculationFormula;
import com.weiwan.dsp.api.metrics.MetricCounter;
import com.weiwan.dsp.api.metrics.MetricGauge;
import com.weiwan.dsp.api.plugin.PluginConfig;
import com.weiwan.dsp.api.plugin.SplitPlugin;
import com.weiwan.dsp.api.resolve.UnresolvedDataCollector;
import com.weiwan.dsp.core.engine.metric.MetricCenter;
import com.weiwan.dsp.api.pojo.DataRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: xiaozhennan
 * @date: 2021/6/17 17:53
 * @description:
 */
public abstract class RichSplitPlugin<T extends PluginConfig> extends AbstractPlugin<T> implements SplitPlugin<DataRecord> {
    private static final Logger _logger = LoggerFactory.getLogger(RichSplitPlugin.class);
    private boolean runing = false;
    private UnresolvedDataCollector unresolvedDataCollector;
    private MetricCenter metricCenter;

    private MetricCounter splitPluginMatchedNum;
    private MetricCounter splitPluginUnMatchedNum;
    private MetricGauge<Long> splitPluginMatchTime;

    @Override
    public void open(PluginConfig config) {
        _logger.info("Initialize split plugin start, plugin name: {}", getPluginName());
        EngineContext context = getContext();
        this.unresolvedDataCollector = (UnresolvedDataCollector) context.getUnresolvedCollector(getNodeId());
        this.metricCenter = (MetricCenter) context.getMetricManager(getNodeId());
        registerPluginMetrics();
        init((T) config);
        this.runing = true;
        _logger.info("Initialize split plugin complete, plugin name: {}", getPluginName());
    }

    private void registerPluginMetrics() {

        //插件匹配数
        this.splitPluginMatchedNum = metricCenter.registerMetric(MetricKey.replacePN(MetricKey.SPLIT_PLUGIN_MATCHED_NUM, getPluginName()), MetricCounter.class);

        //插件未匹配数
        this.splitPluginUnMatchedNum = metricCenter.registerMetric(MetricKey.replacePN(MetricKey.SPLIT_PLUGIN_UNMATCHED_NUM, getPluginName()), MetricCounter.class);

        //插件匹配时间
        this.splitPluginMatchTime = metricCenter.registerMetric(MetricKey.replacePN(MetricKey.SPLIT_PLUGIN_MATCH_TIME, getPluginName()), MetricGauge.class);
        this.splitPluginMatchTime.formula(new CalculationFormula<Long>() {
            private long lastTime;
            private long lastCount;

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
    }

    @Override
    public void close() {
        _logger.info("Close split plugin start, plugin name: {}", getPluginName());

        this.runing = false;
        try {
            this.stop();
        } catch (Exception e) {
            _logger.warn("Close the data split plugin: {} an exception occurred.", getPluginName(), e);
        }
        _logger.info("Close split plugin complete plugin name: {}", getPluginName());
    }

    @Override
    public boolean splitMatch(DataRecord record) throws Exception {
        if (runing) {
            long startMatchTime = System.currentTimeMillis();
            boolean matched = false;
            try {
                matched = this.match(record);
                if (matched) {
                    splitPluginMatchedNum.inc();
                } else {
                    splitPluginUnMatchedNum.inc();
                }
                return matched;
            } catch (Exception e) {
                throw e;
            } finally {
                long endMatchTime = System.currentTimeMillis();
                recordSpendTime(endMatchTime - startMatchTime);
                recordProcessCapacity(1);
            }
        }
        return false;
    }

    public abstract boolean match(DataRecord record);
}
