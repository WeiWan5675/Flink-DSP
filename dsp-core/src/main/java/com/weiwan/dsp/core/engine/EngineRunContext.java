package com.weiwan.dsp.core.engine;

import com.weiwan.dsp.api.config.core.*;
import com.weiwan.dsp.api.config.flow.FlowConfig;
import com.weiwan.dsp.api.config.flow.NodeConfig;
import com.weiwan.dsp.api.context.EngineContext;
import com.weiwan.dsp.api.resolve.UnresolvedDataCollector;
import com.weiwan.dsp.api.metrics.MetricManager;
import com.weiwan.dsp.common.utils.CheckTool;
import com.weiwan.dsp.common.utils.ObjectUtil;
import com.weiwan.dsp.core.engine.metric.MetricManagerFactory;
import com.weiwan.dsp.core.resolve.UnresolvedDataCollectorFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/12 15:30
 * @description: DSP运行上下文
 */
public class EngineRunContext implements EngineContext {


    private FlowConfig flowConfig;

    private CoreConfig coreConfig;
    private MetricConfig metricConfig;
    private EngineConfig engineConfig;
    private UnresolvedCollectorConfig unresolvedCollectorConfig;
    private SpeedLimiterConfig speedLimiterConfig;

    private JobConfig jobConfig;
    private String jobName;
    private String jobId;

    private CustomConfig customConfig;

    private Map<String, UnresolvedDataCollector> unresolvedDataCollectorMap = new ConcurrentHashMap<>();
    private Map<String, MetricManager> metricManagerMap = new ConcurrentHashMap<>();

    public EngineRunContext(DspConfig dspConfig) {
        this.flowConfig = dspConfig.getFlow();

        this.coreConfig = dspConfig.getCore();
        this.metricConfig = coreConfig.getMetricConfig();
        this.engineConfig = coreConfig.getEngineConfig();
        this.unresolvedCollectorConfig = coreConfig.getUnresolvedCollector();
        this.speedLimiterConfig = coreConfig.getSpeedLimiter();
        this.jobConfig = dspConfig.getJob();
        this.jobName = jobConfig.getJobName();
        this.jobId = jobConfig.getJobId();

        this.customConfig = dspConfig.getCustom();
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }


    @Override
    public FlowConfig getFlowConfig() {
        return this.flowConfig;
    }

    @Override
    public NodeConfig getNodeConfig(String nodeId) {
        FlowConfig flowConfig = this.getFlowConfig();
        Map<String, NodeConfig> nodes = flowConfig.getNodes();
        return nodes.get(nodeId);
    }


    @Override
    public MetricConfig getMetricConfig() {
        return this.metricConfig;
    }

    @Override
    public UnresolvedCollectorConfig getUnresolvedCollectorConfig() {
        return this.unresolvedCollectorConfig;
    }

    @Override
    public SpeedLimiterConfig getSpeedLimiterConfig() {
        return this.speedLimiterConfig;
    }

    @Override
    public JobConfig getJobConfig() {
        return this.jobConfig;
    }

    @Override
    public CoreConfig getCoreConfig() {
        return this.coreConfig;
    }

    @Override
    public EngineConfig getEngineConfig() {
        return this.engineConfig;
    }

    @Override
    public MetricManager getMetricManager(String nodeId) {
        String originNodeId = getOriginNodeId(nodeId);
        MetricManager metricManager = metricManagerMap.get(originNodeId);
        if (metricManager != null) {
            return metricManager;
        }
        metricManager = getsAnMetricManager(originNodeId);
        if (metricManager != null) metricManagerMap.put(originNodeId, metricManager);
        return metricManagerMap.get(originNodeId);
    }

    private String getOriginNodeId(String nodeId) {
        CheckTool.checkArgument(nodeId != null, "获取原始节点ID出错, 必须提供一个节点ID");
        String[] split = nodeId.split("\\.");
        if (split.length > 1) {
            nodeId = split[0];
        }
        return nodeId;
    }

    private MetricManager getsAnMetricManager(String nodeId) {
        return MetricManagerFactory.createMetricManager(nodeId, this);
    }


    @Override
    public UnresolvedDataCollector getUnresolvedCollector(final String nodeId) {
        String originNodeId = getOriginNodeId(nodeId);
        UnresolvedDataCollector collectory = unresolvedDataCollectorMap.get(originNodeId);
        if (collectory != null) {
            return collectory;
        }
        //获取一个未解析收集器
        collectory = getsAnUnresolvedCollector(originNodeId);
        if (collectory != null) unresolvedDataCollectorMap.put(originNodeId, collectory);
        return unresolvedDataCollectorMap.get(originNodeId);
    }

    private UnresolvedDataCollector getsAnUnresolvedCollector(String nodeId) {
        //根据配置的异常日志解析器，创建对应的异常处理器
        NodeConfig nodeConfig = getNodeConfig(nodeId);
        CoreConfig coreConfig = getCoreConfig();
        UnresolvedCollectorConfig systemUnresolvedConfig = coreConfig.getUnresolvedCollector();
        UnresolvedCollectorConfig nodeUnresolvedConfig = nodeConfig.getUnresolvedCollectorConfig();
        UnresolvedCollectorConfig unresolvedCollectorConfig = ObjectUtil.mergeObjects(systemUnresolvedConfig, nodeUnresolvedConfig);
        //这里拿到节点的未解析配置,那么我们就可以创建对应的未解析器了

        return UnresolvedDataCollectorFactory.createCollectory(unresolvedCollectorConfig, nodeId, nodeConfig.getNodeName(), nodeConfig.getNodeType(), jobId, jobName);
    }

    @Override
    public String getJobName() {
        return this.jobName;
    }

    @Override
    public String getJobId() {
        return this.jobId;
    }

    @Override
    public CustomConfig getCustomConfig() {
        return this.customConfig;
    }


}
