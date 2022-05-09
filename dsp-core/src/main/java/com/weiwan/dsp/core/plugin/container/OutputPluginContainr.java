package com.weiwan.dsp.core.plugin.container;

import com.weiwan.dsp.api.config.flow.NodeConfig;
import com.weiwan.dsp.api.enums.MetricKey;
import com.weiwan.dsp.api.enums.UnresolvedType;
import com.weiwan.dsp.api.metrics.CalculationFormula;
import com.weiwan.dsp.api.metrics.MetricCounter;
import com.weiwan.dsp.api.metrics.MetricGauge;
import com.weiwan.dsp.api.plugin.OutputPlugin;
import com.weiwan.dsp.api.plugin.Plugin;
import com.weiwan.dsp.api.pojo.DataRecord;
import com.weiwan.dsp.api.resolve.UnresolvedDataCollector;
import com.weiwan.dsp.core.engine.metric.MetricCenter;
import com.weiwan.dsp.core.plugin.AbstractPlugin;
import com.weiwan.dsp.core.plugin.RichOutputPlugin;
import com.weiwan.dsp.core.pub.SpeedLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author: xiaozhennan
 * @date: 2021/6/15 14:17
 * @description:
 */
public class OutputPluginContainr extends PluginRuningContainer {
    private static final Logger logger = LoggerFactory.getLogger(OutputPluginContainr.class);
    private Map<RichOutputPlugin, LinkedBlockingQueue<DataRecord>> outputPlugins = new HashMap<>();
    private UnresolvedDataCollector unresolvedDataCollector;
    private MetricCenter metricCenter;
    private SpeedLimiter limiter;
    private ThreadPoolExecutor threadPoolExecutor;
    private int MAX_OUTPUT_THREAD_NUM = 10;
    private int outputThreadNum;
    private int outputQueueMaxRecordNum;
    private volatile boolean runing;
    //最大等待写出时间为十分钟, 超过十分钟意味着写出出现问题
    private static final long MAX_WAIT_WRITE_TIME = 1000 * 60 * 10;
    //不允许写出异常,如果允许写出错误,可以修改这个值
    private static final long MAX_FAIL_WRITE_NUM = -1;
    private MetricCounter outputTotalNum;
    private MetricCounter outputInputSucsNum;
    private MetricCounter outputInputFailNum;
    private MetricGauge<Long> outputRealEps;
    private MetricGauge<Long> outputSpentTime;

    @Override
    protected void open() {
        this.unresolvedDataCollector = this.getUnresolvedDataCollector();
        this.limiter = this.getSpeedLimiter();
        this.metricCenter = this.getMetricCenter();
        List<Plugin> plugins = this.getPlugins();
        this.outputThreadNum = plugins.size();
        //做一层限制, 避免过多的输出插件
        if (outputThreadNum > MAX_OUTPUT_THREAD_NUM) {
            throw new RuntimeException("The number of output plugins exceeds the maximum allowed limit");
        }
        registerMetrics();
        //创建数据写出线程池
        this.threadPoolExecutor = new ThreadPoolExecutor(outputThreadNum, outputThreadNum + 1,
                3000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        //设置context&初始化插件
        plugins.forEach(plugin -> {
            RichOutputPlugin outputPlugin = (RichOutputPlugin) plugin;
            outputPlugin.setContext(getContext());
            LinkedBlockingQueue<DataRecord> queue = new LinkedBlockingQueue<>();
            outputPlugin.setQueue(queue);
            outputPlugin.open(outputPlugin.getPluginConfig());
            outputPlugins.put(outputPlugin, queue);
        });

        //提交作业线程
        outputPlugins.forEach((outputPlugin, queue) -> threadPoolExecutor.submit(new OutputDataThread(queue, outputPlugin)));
        //所有插件都正常打开后,才可以run,避免个别输出插件初始化失败导致脏数据。
        this.runing = true;
    }

    private void registerMetrics() {

        //输出总条数
        this.outputTotalNum = metricCenter.registerMetric(MetricKey.OUTPUT_TOTAL_NUM, MetricCounter.class);
        //输出节点输入成功条数
        this.outputInputSucsNum = metricCenter.registerMetric(MetricKey.OUTPUT_INPUT_SUCS_NUM, MetricCounter.class);
        //输出节点输入失败条数
        this.outputInputFailNum = metricCenter.registerMetric(MetricKey.OUTPUT_INPUT_FAIL_NUM, MetricCounter.class);
        //输出实时EPS
        this.outputRealEps = metricCenter.registerMetric(MetricKey.OUTPUT_REAL_EPS, MetricGauge.class);
        this.outputRealEps.formula(new CalculationFormula<Long>() {
            private long lastCount;
            private long lastTime;

            @Override
            public Long calculation() {
                final long currentCount = outputTotalNum.getCount();
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

        //输出实时耗时
        this.outputSpentTime = metricCenter.registerMetric(MetricKey.OUTPUT_SPENT_TIME, MetricGauge.class);
        this.outputSpentTime.formula(new CalculationFormula<Long>() {
            private long lastCount;
            private long lastTime;

            @Override
            public Long calculation() {
                final long currentCount = outputTotalNum.getCount();
                AtomicLong allSpentTime = new AtomicLong();
                outputPlugins.forEach((p , q) -> {
                    allSpentTime.addAndGet(((AbstractPlugin) p).getSpendTime());
                });
                final long currentProcessTime = allSpentTime.get() / outputPlugins.size();
                if (lastTime == 0 || lastCount == 0) {
                    this.lastTime = currentProcessTime;
                    this.lastCount = currentCount;
                    return currentProcessTime;
                }
                long c = currentCount - lastCount;
                if (currentProcessTime == 0 || currentCount == 0) return 1L;
                long t = currentProcessTime - lastTime;
                this.lastTime = currentProcessTime;
                this.lastCount = currentCount;
                return c != 0 ? t / c + 1: 1L;
            }
        });



    }

    @Override
    protected void stop() {
        try {
            //这里休眠1s,避免过快的停止导致无法退出
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
        }
        this.runing = false;
        try {
            TimeUnit.SECONDS.sleep(3);
            this.threadPoolExecutor.shutdown();
            //关闭线程池
            awaitTermination();
        } catch (InterruptedException e) {
            this.threadPoolExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        //关闭插件
        if (outputPlugins != null && outputPlugins.size() > 0) {
            outputPlugins.forEach((p, q) -> {
                AbstractPlugin plugin = (AbstractPlugin) p;
                plugin.close();
            });
        }
    }


    private void awaitTermination() throws InterruptedException {
        if (!this.threadPoolExecutor.awaitTermination(2L, TimeUnit.SECONDS)) {
            this.threadPoolExecutor.shutdownNow();
            if (!this.threadPoolExecutor.awaitTermination(3L, TimeUnit.SECONDS)) {
                this.threadPoolExecutor.shutdownNow();
                if (!this.threadPoolExecutor.awaitTermination(5L, TimeUnit.SECONDS)) {
                    logger.warn(this.getClass().getSimpleName() + ": Read threadPoolExecutor did not terminate");
                }
            }
        }
    }

    public OutputPluginContainr(NodeConfig nodeConfig, List<Plugin> plugins) {
        super(nodeConfig, plugins);
    }

    @Override
    public void write(DataRecord dataRecord) {
        if (dataRecord == null) {
            return;
        }
        if (runing) {
            limiter.limit();
            boolean fail = false;
            for (RichOutputPlugin outputPlugin : outputPlugins.keySet()) {
                try {
                    LinkedBlockingQueue queue = outputPlugin.getQueue();
                    boolean offer = queue.offer(dataRecord, 10, TimeUnit.SECONDS);
                    if (!offer) {
                        TimeUnit.SECONDS.sleep(2);
                        boolean offer2 = queue.offer(dataRecord);
                        if (!offer2) {
                            fail = true;
                            logger.warn("The queue is full, unable to write data to the plug-in queue, plugin name: {}", outputPlugin.getPluginName());
                        }
                    }
                } catch (Exception e) {
                    fail = true;
                    logger.warn("an interrupt error occurred while writing data", e);
                }
            }

            if (fail) {
                outputInputFailNum.inc();
                unresolvedDataCollector.collect(UnresolvedType.FAIL, dataRecord, "data write out exception");
            } else {
                outputInputSucsNum.inc();
            }
            outputTotalNum.inc();
        }
    }


    class OutputDataThread implements Runnable {
        private final Logger logger = LoggerFactory.getLogger(OutputDataThread.class);
        private LinkedBlockingQueue<DataRecord> queue;
        private OutputPlugin<DataRecord> outputPlugin;

        public OutputDataThread(LinkedBlockingQueue<DataRecord> queue, OutputPlugin<DataRecord> outputPlugin) {
            this.queue = queue;
            this.outputPlugin = outputPlugin;
        }

        @Override
        public void run() {
            logger.info("The data write thread starts working, and the current write queue plugin: {}", outputPlugin.getPluginConfig().getPluginName());
            while (runing) {
                if (outputPlugin != null) {
                    outputPlugin.output(null);
                }
            }
            //队列中得先都写出去
            while (queue.size() > 0) {
                outputPlugin.output(null);
            }
        }

    }
}





