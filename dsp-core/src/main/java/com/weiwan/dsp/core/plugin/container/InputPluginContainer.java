package com.weiwan.dsp.core.plugin.container;

import com.weiwan.dsp.api.config.flow.NodeConfig;
import com.weiwan.dsp.api.enums.MetricKey;
import com.weiwan.dsp.api.metrics.CalculationFormula;
import com.weiwan.dsp.api.metrics.MetricCounter;
import com.weiwan.dsp.api.metrics.MetricGauge;
import com.weiwan.dsp.api.plugin.InputPlugin;
import com.weiwan.dsp.api.plugin.Launcher;
import com.weiwan.dsp.api.plugin.Plugin;
import com.weiwan.dsp.api.pojo.DataRecord;
import com.weiwan.dsp.api.resolve.UnresolvedDataCollector;
import com.weiwan.dsp.core.engine.metric.MetricCenter;
import com.weiwan.dsp.core.plugin.AbstractPlugin;
import com.weiwan.dsp.core.pub.DataLauncher;
import com.weiwan.dsp.core.plugin.RichInputPlugin;
import com.weiwan.dsp.core.pub.SpeedLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: xiaozhennan
 * @date: 2021/6/15 14:16
 * @description:
 */
public class InputPluginContainer extends PluginRuningContainer {

    private static final Logger logger = LoggerFactory.getLogger(InputPluginContainer.class);
    private InputPlugin<DataRecord> inputPlugin;
    private DataLauncher<DataRecord> launcher;
    private SpeedLimiter limiter;
    private UnresolvedDataCollector unresolvedDataCollector;
    private MetricCenter metricCenter;
    private LinkedBlockingQueue<DataRecord> dataQueue;
    private ThreadPoolExecutor threadPoolExecutor;
    private static final int MAX_INPUT_THREAD_NUM = 1;
    private volatile boolean runing;


    private MetricCounter inputTotalNum;
    private MetricCounter inputTotalSucsNum;
    private MetricCounter inputTotalFailNum;
    private MetricGauge<Long> inputRealEps;
    private MetricGauge<Long> inputSpentTime;
    private MetricGauge<Integer> inputQueueSize;



    @Override
    protected void open() {
        this.inputPlugin = (InputPlugin) this.getPlugin();
        this.limiter = this.getSpeedLimiter();
        this.unresolvedDataCollector = this.getUnresolvedDataCollector();
        this.metricCenter = this.getMetricCenter();
        registerInputMetrics();

        this.dataQueue = new LinkedBlockingQueue<>();
        this.launcher = new DataLauncher<>(dataQueue, limiter);
        this.threadPoolExecutor = new ThreadPoolExecutor(MAX_INPUT_THREAD_NUM, MAX_INPUT_THREAD_NUM + 1,
                3000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

        //这里目前只支持单个Input/不支持并行Input, Flink可以通过调整并行度,后续考虑这里可以支持开启并行拉取
        RichInputPlugin richInputPlugin = (RichInputPlugin) inputPlugin;
        richInputPlugin.setContext(getContext());
        richInputPlugin.open(richInputPlugin.getPluginConfig());
        this.runing = true;
        InputDataThread inputDataThread = new InputDataThread(inputPlugin, launcher);
        threadPoolExecutor.submit(inputDataThread);
    }

    private void registerInputMetrics() {
        //总输入记录数
        this.inputTotalNum = metricCenter.registerMetric(MetricKey.INPUT_TOTAL_NUM, MetricCounter.class);

        //总输入成功记录数
        this.inputTotalSucsNum = metricCenter.registerMetric(MetricKey.INPUT_TOTAL_SUCC_NUM, MetricCounter.class);

        //总输入失败记录数
        this.inputTotalFailNum = metricCenter.registerMetric(MetricKey.INPUT_TOTAL_FAIL_NUM, MetricCounter.class);

        //实时EPS 流量 / 统计秒 = 每秒eps
        this.inputRealEps = metricCenter.registerMetric(MetricKey.INPUT_REAL_EPS, MetricGauge.class);
        this.inputRealEps.formula(new CalculationFormula<Long>() {
            private long lastCount;
            private long lastTime;

            @Override
            public Long calculation() {
                final long currentCount = inputTotalNum.getCount();
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

        //节点耗时 读取时间毫秒数 / 流量 = 每条读取耗时
        this.inputSpentTime = metricCenter.registerMetric(MetricKey.INPUT_SPENT_TIME, MetricGauge.class);
        this.inputSpentTime.formula(new CalculationFormula<Long>() {
            private long lastCount;
            private long lastTime;

            @Override
            public Long calculation() {
                final long currentCount = inputTotalNum.getCount();
                final long spendTime = ((AbstractPlugin) inputPlugin).getSpendTime();
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

        //读取队列长度, 用来判断读取后发往下游算子是否淤积
        this.inputQueueSize = metricCenter.registerMetric(MetricKey.INPUT_QUEUE_SIZE, MetricGauge.class);
        this.inputQueueSize.formula(new CalculationFormula<Integer>() {
            @Override
            public Integer calculation() {
                return dataQueue.size();
            }
        });
    }

    @Override
    protected void stop() {
        this.runing = false;
        this.threadPoolExecutor.shutdown();
        try {
            awaitTermination();
        } catch (InterruptedException e) {
            this.threadPoolExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        if (inputPlugin != null) {
            AbstractPlugin abstractPlugin = (AbstractPlugin) inputPlugin;
            abstractPlugin.close();
        }

    }

    private void awaitTermination() throws InterruptedException {
        if (!this.threadPoolExecutor.awaitTermination(2L, TimeUnit.SECONDS)) {
            this.threadPoolExecutor.shutdownNow();
            if (!this.threadPoolExecutor.awaitTermination(3L, TimeUnit.SECONDS)) {
                this.threadPoolExecutor.shutdownNow();
                if (!this.threadPoolExecutor.awaitTermination(5L, TimeUnit.SECONDS)) {
                    System.err.println(this.getClass().getSimpleName() + ": Read threadPoolExecutor did not terminate");
                }
            }
        }
    }

    public InputPluginContainer(NodeConfig nodeConfig, List<Plugin> plugins) {
        super(nodeConfig, plugins);
    }

    @Override
    public DataRecord read() {
        if (!runing) return null;
        try {
            DataRecord record = dataQueue.poll(5, TimeUnit.SECONDS);
            if (record != null) {
                this.inputTotalNum.inc();
                this.inputTotalSucsNum.inc();
            }
            return record;
        } catch (Exception e) {
            this.inputTotalFailNum.inc();
            if (logger.isDebugEnabled()) {
                logger.debug("the current data read queue is empty");
            }
        }
        return null;
    }

    class InputDataThread implements Runnable {
        private InputPlugin<DataRecord> inputPlugin;
        private DataLauncher<DataRecord> launcher;

        public InputDataThread(InputPlugin<DataRecord> inputPlugin, DataLauncher<DataRecord> launcher) {
            this.inputPlugin = inputPlugin;
            this.launcher = launcher;
        }


        @Override
        public void run() {
            logger.info("The data reading thread starts to run and calls the plugin to read the data");
            try {
                if (runing && inputPlugin != null && launcher != null) {
                    inputPlugin.input(launcher);
                }
            } catch (Exception e) {
                logger.error("Exception reading data from input plugin using data launcher.", e);
                throw e;
            } finally {
                runing = false;
            }
        }

    }

    @Override
    public Launcher getDataLauncher() {
        return this.launcher;
    }

    public boolean isOpen() {
        return this.runing;
    }
}
