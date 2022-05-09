package com.weiwan.dsp.core.plugin.container;

import com.weiwan.dsp.api.config.flow.NodeConfig;
import com.weiwan.dsp.api.enums.MetricKey;
import com.weiwan.dsp.api.enums.UnresolvedType;
import com.weiwan.dsp.api.metrics.CalculationFormula;
import com.weiwan.dsp.api.metrics.MetricCounter;
import com.weiwan.dsp.api.metrics.MetricGauge;
import com.weiwan.dsp.api.plugin.Plugin;
import com.weiwan.dsp.api.plugin.ProcessPlugin;
import com.weiwan.dsp.api.pojo.DataRecord;
import com.weiwan.dsp.api.resolve.UnresolvedDataCollector;
import com.weiwan.dsp.core.engine.metric.MetricCenter;
import com.weiwan.dsp.core.plugin.RichProcessPlugin;
import com.weiwan.dsp.core.pub.SpeedLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: xiaozhennan
 * @date: 2021/6/15 14:17
 * @description:
 */
public class ProcessPluginContainer extends PluginRuningContainer {
    private static final Logger logger = LoggerFactory.getLogger(ProcessPluginContainer.class);
    private SpeedLimiter limiter;
    private UnresolvedDataCollector unresolvedDataCollector;
    private MetricCenter metricCenter;
    private List<ProcessPlugin<DataRecord, DataRecord>> processPlugins = new ArrayList<>();
    private volatile boolean runing;
    private MetricCounter processTotalNum;
    private MetricCounter processTotalSucsNum;
    private MetricCounter processTotalFailNum;
    private MetricGauge<Long> processRealEps;
    private MetricGauge<Long> processSpentTime;
    private long currentProcessTime;
    @Override
    protected void open() {
        this.limiter = this.getSpeedLimiter();
        this.unresolvedDataCollector = this.getUnresolvedDataCollector();
        this.metricCenter = this.getMetricCenter();
        registerMetrics();
        List<Plugin> plugins = this.getPlugins();
        plugins.forEach(p -> {
            RichProcessPlugin abstractPlugin = (RichProcessPlugin) p;
            abstractPlugin.setContext(getContext());
            abstractPlugin.open(abstractPlugin.getPluginConfig());
            processPlugins.add(abstractPlugin);
        });
        this.runing = true;
    }

    private void registerMetrics() {

        //总处理记录数
        this.processTotalNum = metricCenter.registerMetric(MetricKey.PROCESS_TOTAL_NUM, MetricCounter.class);
        //总处理成功记录数
        this.processTotalSucsNum = metricCenter.registerMetric(MetricKey.PROCESS_TOTAL_SUCS_NUM, MetricCounter.class);
        //总处理失败记录数
        this.processTotalFailNum = metricCenter.registerMetric(MetricKey.PROCESS_TOTAL_FAIL_NUM, MetricCounter.class);
        //实时eps
        this.processRealEps = metricCenter.registerMetric(MetricKey.PROCESS_REAL_EPS, MetricGauge.class);
        this.processRealEps.formula(new CalculationFormula<Long>() {
            private long lastCount;
            private long lastTime;
            @Override
            public Long calculation() {
                final long currentCount = processTotalNum.getCount();
                final long currentTime = System.currentTimeMillis();
                if(lastCount == 0) this.lastCount = currentCount;
                if(lastTime == 0) this.lastTime = currentTime;
                long c = currentCount - lastCount;
                if(c == 0) return 0L;
                long t = currentTime - lastTime;
                if(t < 1000L) t = 1000L;
                this.lastTime = currentTime;
                this.lastCount = currentCount;
                return t != 0 ? c / (t / 1000) : 0L;
            }
        });
        //实时耗时
        this.processSpentTime = metricCenter.registerMetric(MetricKey.PROCESS_SPENT_TIME, MetricGauge.class);
        this.processSpentTime.formula(new CalculationFormula<Long>() {
            private long lastCount;
            private long lastTime;

            @Override
            public Long calculation() {
                final long currentCount = processTotalNum.getCount();
                final long spendTime = currentProcessTime;
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
    protected void stop() {
        this.runing = false;
        if (processPlugins != null && processPlugins.size() > 0) {
            for (ProcessPlugin<DataRecord, DataRecord> processPlugin : processPlugins) {
                try {
                    processPlugin.close();
                } catch (Exception e) {
                    logger.warn("关闭数据处理插件时出现错误,插件名称: {}", processPlugin.getPluginName());
                }
            }
        }
    }

    @Override
    public DataRecord process(DataRecord dataRecord) {
        if (!runing) {
            return dataRecord;
        }
        DataRecord record = dataRecord;
        boolean fail = false;
        limiter.limit();
        long startProcessTime = System.currentTimeMillis();
        for (ProcessPlugin<DataRecord, DataRecord> processPlugin : processPlugins) {
            try {
                record = processPlugin.process(record);
            } catch (Exception e) {
                fail = true;
                logger.error("数据处理发生异常, 异常插件: {}, 异常数据： {}", processPlugin.getPluginName(), dataRecord.toString(), e);
                unresolvedDataCollector.collect(UnresolvedType.FAIL, record, String.format("数据处理发生异常, 异常插件: %s, 异常信息: %s", processPlugin.getPluginName(), e));
            }
        }
        if (fail) {
            processTotalFailNum.inc();
        } else {
            processTotalSucsNum.inc();
        }
        long endProcessTime = System.currentTimeMillis();
        currentProcessTime += (endProcessTime - startProcessTime) + 1;
        processTotalNum.inc();
        return record;
    }

    public ProcessPluginContainer(NodeConfig nodeConfig, List<Plugin> plugins) {
        super(nodeConfig, plugins);
    }


}
