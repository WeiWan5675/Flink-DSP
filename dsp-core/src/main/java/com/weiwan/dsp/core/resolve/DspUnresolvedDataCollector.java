package com.weiwan.dsp.core.resolve;

import com.sun.istack.NotNull;
import com.weiwan.dsp.api.config.core.CollectorHandlerConfigs;
import com.weiwan.dsp.api.config.core.UnresolvedCollectorConfig;
import com.weiwan.dsp.api.enums.NodeType;
import com.weiwan.dsp.api.enums.UnresolvedType;
import com.weiwan.dsp.api.pojo.DataRecord;
import com.weiwan.dsp.api.pojo.UnresolvedDataRecord;
import com.weiwan.dsp.api.resolve.UnresolvedDataCollector;
import com.weiwan.dsp.core.pub.DspCounter;
import com.weiwan.dsp.core.pub.SpeedLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static com.weiwan.dsp.api.enums.UnresolvedType.USER;

/**
 * @author: xiaozhennan
 * @date: 2021/6/2 13:21
 * @description: 异常数据收集器的抽象类
 */
public abstract class DspUnresolvedDataCollector<T extends CollectorHandlerConfigs> implements UnresolvedDataCollector<DataRecord> {


    private static final Logger _LOGGER = LoggerFactory.getLogger(DspUnresolvedDataCollector.class);
    private static final int UNRESOLVED_QUEUE_MAX_RERORD = 1000;
    //如果是异步采样时,这个队列会被构造
    private LinkedBlockingQueue<UnresolvedDataRecord> unresolvedQueue;
    private DspCounter failCounter = new DspCounter();
    private DspCounter formatCounter = new DspCounter();
    private DspCounter ruleCounter = new DspCounter();
    private DspCounter unknownCounter = new DspCounter();
    private DspCounter allCounter = new DspCounter();
    private DspCounter userCounter = new DspCounter();
    private UnresolvedDataHandlerThread handler;
    private SpeedLimiter limiter;
    private UnresolvedCollectorConfig collectorConfig;
    private T collectorConfigs;
    private volatile boolean runing;
    private long maxSamplingRecord;
    private long samplingInterval;
    private String nodeId;
    private String nodeName;
    private NodeType nodeType;
    private String jobName;
    private String jobId;

    public DspUnresolvedDataCollector() {
    }

    public DspUnresolvedDataCollector(long maxSamplingRecord, long samplingInterval, String nodeId, String nodeName, NodeType nodeType, String jobId, String jobName) {
        this.maxSamplingRecord = maxSamplingRecord;
        this.samplingInterval = samplingInterval;
        this.nodeId = nodeId;
        this.nodeName = nodeName;
        this.nodeType = nodeType;
        this.jobId = jobId;
        this.jobName = jobName;
    }


    @Override
    public Map<UnresolvedType, Long> statistics() {
        Map<UnresolvedType, Long> unresolvedStatistics = new HashMap<>();
        unresolvedStatistics.put(UnresolvedType.FAIL, failCounter.get());
        unresolvedStatistics.put(UnresolvedType.FORMAT, formatCounter.get());
        unresolvedStatistics.put(UnresolvedType.RULE, ruleCounter.get());
        unresolvedStatistics.put(UnresolvedType.UNKNOWN, unknownCounter.get());
        unresolvedStatistics.put(USER, userCounter.get());
        return unresolvedStatistics;
    }


    @Override
    public long count(UnresolvedType type) {
        final DspCounter counter = getCounter(type);
        return counter == null ? 0 : counter.get();
    }

    private DspCounter getCounter(UnresolvedType type) {
        switch (type) {
            case FAIL:
                return failCounter;
            case RULE:
                return ruleCounter;
            case FORMAT:
                return formatCounter;
            case UNKNOWN:
                return unknownCounter;
            case USER:
                return userCounter;
        }
        return null;
    }

    @Override
    public boolean clean(UnresolvedType type) {
        final DspCounter counter = getCounter(type);
        if (counter != null) {
            counter.clean();
            return true;
        }
        return false;
    }

    @Override
    public long allCount() {
        return allCounter.get();
    }

    @Override
    public void open(UnresolvedCollectorConfig config) {
        this.collectorConfig = config;
        this.collectorConfigs = (T) config.getHandlerConfigs();
        this.unresolvedQueue = new LinkedBlockingQueue<UnresolvedDataRecord>(UNRESOLVED_QUEUE_MAX_RERORD);
        this.limiter = new SpeedLimiter(maxSamplingRecord, samplingInterval, TimeUnit.SECONDS);
        this.handler = new UnresolvedDataHandlerThread(this);
        this.init((T) collectorConfigs);
        this.runing = true;
        //启动异步处理线程
        handler.start();
    }

    /**
     * 初始化
     *
     * @param collectorConfig
     */
    public abstract void init(T collectorConfig);


    /**
     * 处理未解析日志
     *
     * @param record
     */
    public abstract void handler(UnresolvedDataRecord record);

    /**
     * 关闭
     */
    public abstract void stop();


    @Override
    public void close() {
        runing = false;
        try {
            this.stop();
        } finally {
            runing = false;
            if (handler.isAlive()) {
                handler.interrupt();
            }
        }
    }


    @Override
    public void collect(@NotNull UnresolvedType type, DataRecord dataRecord, String msg) {
        UnresolvedDataRecord unresolvedDataRecord;
        if (dataRecord != null) {
            long timeMillis = System.currentTimeMillis();
            unresolvedDataRecord = new UnresolvedDataRecord(dataRecord, type.getTypeName(type.name()), jobId, jobName, nodeId, nodeName, nodeType, timeMillis, msg);
        } else {
            return;
        }

        recordNotResolved(type);
        //1000最大值
        //30s
        this.collect(unresolvedDataRecord);
    }

    @Override
    public void collect(String type, DataRecord dataRecord, String msg) {
        UnresolvedDataRecord unresolvedDataRecord;
        if (dataRecord != null) {
            long timeMillis = System.currentTimeMillis();
            unresolvedDataRecord = new UnresolvedDataRecord(dataRecord, UnresolvedType.USER.getTypeName(type), jobId, jobName, nodeId, nodeName, nodeType, timeMillis, msg);
        } else {
            return;
        }
        recordNotResolved(UnresolvedType.USER);

        this.collect(unresolvedDataRecord);
    }

    private void recordNotResolved(UnresolvedType type) {
        DspCounter counter = getCounter(type);
        counter.increment();
        allCounter.increment();
    }


    private void collect(UnresolvedDataRecord unresolvedDataRecord) {
        unresolvedQueue.offer(unresolvedDataRecord);
    }


    class UnresolvedDataHandlerThread extends Thread {

        private final DspUnresolvedDataCollector<CollectorHandlerConfigs> dataCollector;

        public UnresolvedDataHandlerThread(DspUnresolvedDataCollector dspUnresolvedDataCollector) {
            this.dataCollector = dspUnresolvedDataCollector;
        }

        @Override
        public void run() {
            while (runing) {
                try {
                    UnresolvedDataRecord record = dataCollector.unresolvedQueue.poll(10, TimeUnit.SECONDS);
                    dataCollector.limiter.limit();
                    if (record != null) {
                        dataCollector.handler(record);
                    }
                } catch (InterruptedException e) {
                    if (_LOGGER.isDebugEnabled()) {
                        _LOGGER.debug("no unresolved data is currently generated");
                    }
                } catch (Exception e) {
                    if (_LOGGER.isDebugEnabled()) {
                        _LOGGER.debug("Exception occurred in processing unresolved log", e);
                    }
                }
            }
        }
    }


}
