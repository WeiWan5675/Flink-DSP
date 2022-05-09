package com.weiwan.dsp.core.plugin;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.weiwan.dsp.api.context.EngineContext;
import com.weiwan.dsp.api.enums.MetricKey;
import com.weiwan.dsp.api.enums.UnresolvedType;
import com.weiwan.dsp.api.metrics.CalculationFormula;
import com.weiwan.dsp.api.metrics.MetricCounter;
import com.weiwan.dsp.api.metrics.MetricGauge;
import com.weiwan.dsp.api.plugin.PluginConfig;
import com.weiwan.dsp.api.plugin.OutputPlugin;
import com.weiwan.dsp.api.resolve.UnresolvedDataCollector;
import com.weiwan.dsp.core.engine.metric.MetricCenter;
import com.weiwan.dsp.api.pojo.DataRecord;
import com.weiwan.dsp.common.config.ConfigOption;
import com.weiwan.dsp.common.config.ConfigOptions;
import com.weiwan.dsp.common.constants.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/9 16:01
 * @description:
 */
public abstract class RichOutputPlugin<T extends PluginConfig> extends AbstractPlugin<T> implements OutputPlugin<DataRecord> {

    private static final Logger _logger = LoggerFactory.getLogger(RichOutputPlugin.class);
    private List<DataRecord> records;
    private volatile boolean runing;
    private UnresolvedDataCollector unresolvedDataCollector;
    private MetricCenter metricCenter;
    private LinkedBlockingQueue<DataRecord> queue;
    private MetricGauge<Integer> outputPluginQueueSize;
    private MetricGauge<Long> outputPluginSpentTime;
    private MetricCounter outputPluginFailNum;

    public static final ConfigOption<Boolean> BATCH_MODE = ConfigOptions.key("batchMode")
            .defaultValue(false)
            .required(false)
            .description("是否时批处理模式")
            .ok(Boolean.class);

    private static final ConfigOption<Integer> BATCH_SIZE = ConfigOptions.key("batchSize")
            .defaultValue(1000)
            .required(false)
            .description("每批数据的大小")
            .ok(Integer.class);

    private boolean batchMode;
    private int batchSize;
    private MetricCounter outputPluginSuccNum;
    private MetricGauge outputPluginRealEps;


    @Override
    public void open(PluginConfig config) {
        _logger.info("Initialize output plugin start, plugin name: {}", getPluginName());
        EngineContext context = getContext();
        this.unresolvedDataCollector = (UnresolvedDataCollector) context.getUnresolvedCollector(getNodeId());
        this.metricCenter = (MetricCenter) context.getMetricManager(getNodeId());
        this.batchMode = config.getVal(BATCH_MODE);
        this.batchSize = config.getVal(BATCH_SIZE);
        if (batchMode) {
            this.records = new ArrayList<>(this.batchSize);
            this.records.clear();
        }
        registerPluginMetrics();
        init((T) config);
        this.runing = true;
        _logger.info("Initialize output plugin complete, plugin name: {}", getPluginName());
    }

    private void registerPluginMetrics() {

        //插件输出成功记录数
        this.outputPluginSuccNum = this.metricCenter.registerMetric(MetricKey.replacePN(MetricKey.OUTPUT_PLUGIN_SUCS_NUM, getPluginName()), MetricCounter.class);
        //插件输出失败记录数
        this.outputPluginFailNum = this.metricCenter.registerMetric(MetricKey.replacePN(MetricKey.OUTPUT_PLUGIN_FAIL_NUM, getPluginName()), MetricCounter.class);
        //插件输出队列长度
        this.outputPluginQueueSize = this.metricCenter.registerMetric(MetricKey.replacePN(MetricKey.OUTPUT_PLUGIN_QUEUE_SIZE, getPluginName()), MetricGauge.class);
        this.outputPluginQueueSize.formula(new CalculationFormula<Integer>() {
            @Override
            public Integer calculation() {
                return queue.size();
            }
        });
        //插件输出平均耗时
        this.outputPluginSpentTime = this.metricCenter.registerMetric(MetricKey.replacePN(MetricKey.OUTPUT_PLUGIN_SPENT_TIME, getPluginName()), MetricGauge.class);
        this.outputPluginSpentTime.formula(new CalculationFormula<Long>() {
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
        //插件实时EPS
        this.outputPluginRealEps = this.metricCenter.registerMetric(MetricKey.replacePN(MetricKey.OUTPUT_PLUGIN_REAL_EPS, getPluginName()), MetricGauge.class);
        this.outputPluginRealEps.formula(new CalculationFormula<Long>() {
            private long lastCount;
            private long lastTime;

            @Override
            public Long calculation() {
                final long currentCount = getProcessCapacity();
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


    }

    @Override
    public void close() {
        _logger.info("Close input plugin start, plugin name: {}", getPluginName());
        this.runing = false;
        try {
            Thread.sleep(3000L);
            if (_logger.isDebugEnabled()) {
                _logger.debug("When closing the data writing plugin, wait for 3 seconds to prevent the data from being written");
            }
        } catch (InterruptedException e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("An error occurred while waiting 3 seconds to close the write plug-in");
            }
        }
        try {
            checkAndWrite();
        } catch (Exception e) {
            _logger.warn("Close the data write out plugin, an error occurred during data write out, which may lead to data loss", e);
        }
        //关闭插件
        try {
            this.stop();
        } catch (Exception e) {
            _logger.warn("Close the data output plugin: {} an exception occurred.", getPluginName(), e);
        }
        _logger.info("Close Input Plugin Complete PluginName: {}", getPluginName());
    }

    @Override
    public void output(DataRecord obj) {
        if (runing) {
            //从队列取数据
            if(obj == null){
                try {
                    obj = queue.poll(3, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                }
                if(obj == null) return;
            }
            //判断是否是批处理模式
            if (!batchMode) {
                long startWriteTime = System.currentTimeMillis();
                try {
                    this.write(obj);
                    outputPluginSuccNum.inc();
                } catch (Exception e) {
                    _logger.error("Node: {}, plugin: {}, failed to write data synchronously, please check", getNodeName(), getPluginName(), e);
                    outputPluginFailNum.inc();
                    unresolvedDataCollector.collect(UnresolvedType.FAIL, obj, ExceptionUtil.getRootCauseMessage(e));
                }finally {
                    long endWriteTime = System.currentTimeMillis();
                    recordSpendTime(endWriteTime - startWriteTime);
                    recordProcessCapacity(1);
                }
            } else {
                //检查是否需要写出
                records.add(obj);
                try {
                    checkAndWrite();
                } catch (Exception e) {
                    _logger.error("Node: {}, plugin: {}, failed to batch write data asynchronous, please check", getNodeName(), getPluginName(), e);
                }
            }
        }
    }

    private void checkAndWrite() throws Exception {
        if (batchMode) {
            if (this.records.size() >= batchSize) {
                long startBatchWriteTime = System.currentTimeMillis();
                final int count = this.records.size();
                try {
                    this.batchWrite(this.records);
                    if (_logger.isDebugEnabled()) {
                        _logger.debug("Node: {}, plugin: {}, batch write data to external storage completed", getNodeName(), getPluginName() + Constants.HENG_GANG + getPluginId());
                    }
                    this.outputPluginSuccNum.inc(count);
                } catch (Exception e) {
                    _logger.warn("Node: {}, plugin: {}, Batch write data problems, call synchronous write data to try", getNodeName(), getPluginName() + Constants.HENG_GANG + getPluginId(), e);
                    try {
                        for (DataRecord record : records) {
                            write(record);
                            this.outputPluginSuccNum.inc();
                        }
                    } catch (Exception exception) {
                        _logger.error("Node: {} plugin: {} , An error occurred while trying to write data synchronously and the program terminated", getNodeName(), getPluginName() + Constants.HENG_GANG + getPluginId(), e);
                        this.outputPluginFailNum.inc(count);
                        throw exception;
                    }
                }finally {
                    long endBatchWriteTime = System.currentTimeMillis();
                    recordSpendTime(endBatchWriteTime - startBatchWriteTime);
                    recordProcessCapacity(this.records.size());
                }
                this.records.clear();
            }
        }
    }


    public abstract void write(DataRecord record) throws Exception;

    public abstract void batchWrite(List<DataRecord> records) throws Exception;


    public void setQueue(LinkedBlockingQueue<DataRecord> queue) {
            this.queue = queue;
    }

    public LinkedBlockingQueue<DataRecord> getQueue() {
        return this.queue;
    }
}
