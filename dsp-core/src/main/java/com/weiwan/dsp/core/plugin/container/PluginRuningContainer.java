package com.weiwan.dsp.core.plugin.container;

import com.weiwan.dsp.api.config.core.CoreConfig;
import com.weiwan.dsp.api.config.core.SpeedLimiterConfig;
import com.weiwan.dsp.api.config.flow.NodeConfig;
import com.weiwan.dsp.api.context.EngineContext;
import com.weiwan.dsp.api.enums.NodeType;
import com.weiwan.dsp.api.plugin.Launcher;
import com.weiwan.dsp.api.plugin.Plugin;
import com.weiwan.dsp.api.plugin.PluginContainer;
import com.weiwan.dsp.api.pojo.DataRecord;
import com.weiwan.dsp.api.resolve.UnresolvedDataCollector;
import com.weiwan.dsp.common.utils.ObjectUtil;
import com.weiwan.dsp.common.utils.ReflectUtil;
import com.weiwan.dsp.core.engine.ext.EngineExtSupport;
import com.weiwan.dsp.core.engine.metric.MetricCenter;
import com.weiwan.dsp.core.plugin.AbstractPlugin;
import com.weiwan.dsp.api.constants.CoreConstants;
import com.weiwan.dsp.core.pub.SpeedLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: xiaozhennan
 * @date: 2021/6/15 13:58
 * @description:
 */
public abstract class PluginRuningContainer implements PluginContainer<DataRecord> {

    private static final Logger _LOGGER = LoggerFactory.getLogger(PluginRuningContainer.class);
    //构建时初始化
    private EngineContext context;
    private NodeConfig nodeConfig;
    private List<Plugin> plugins;
    private Plugin plugin;
    private boolean single;


    //init时初始化
    private UnresolvedDataCollector internalUnresolvedDataCollector;
    private MetricCenter internalMetricCenter;
    private SpeedLimiter internalSpeedLimiter;


    private EngineExtSupport engineExtSupport;

    @Override
    public void init() {
        //初始化未解析器
        this.internalUnresolvedDataCollector = (UnresolvedDataCollector) context.getUnresolvedCollector(nodeConfig.getNodeId());
        //初始化metricsManager
        this.internalMetricCenter = (MetricCenter) context.getMetricManager(nodeConfig.getNodeId());
        //初始化限速器
        this.internalSpeedLimiter = createSpeedLimit(context.getCoreConfig());
        //给每个插件设置EngineExtSupport
        if (single) {
            ReflectUtil.setFieldValue(AbstractPlugin.class, plugin, CoreConstants.ENGINE_EXT_SUPPORT_NAME, engineExtSupport);
        } else {
            plugins.forEach(p -> {
                ReflectUtil.setFieldValue(AbstractPlugin.class, p, CoreConstants.ENGINE_EXT_SUPPORT_NAME, engineExtSupport);
            });
        }
        //调用子类container的open()
        open();
    }

    private SpeedLimiter createSpeedLimit(CoreConfig coreConfig) {
        SpeedLimiterConfig systemSpeedLimiter = coreConfig.getSpeedLimiter();
        SpeedLimiterConfig nodeSpeedLimit = this.nodeConfig.getSpeedLimit();
        SpeedLimiterConfig speedLimiterConfig = ObjectUtil.mergeObjects(systemSpeedLimiter, nodeSpeedLimit);
        long speed = -1L;
        if (nodeConfig.getNodeType() == NodeType.READER) {
            speed = speedLimiterConfig.getReadSpeed();
        } else if (nodeConfig.getNodeType() == NodeType.PROCESS) {
            speed = speedLimiterConfig.getProcessSpeed();
        } else if (nodeConfig.getNodeType() == NodeType.WRITER) {
            speed = speedLimiterConfig.getWriteSpeed();
        }
        //如果限流速率小于0, 不开启限流
        if(speed < 0){
            speedLimiterConfig.setEnableLimiter(false);
        }
        //限流规则是: record/每秒
        long samplingInterval = speedLimiterConfig.getSamplingInterval();
        if (speed == -1) speed = 9999999999L;
        SpeedLimiter speedLimiter = new SpeedLimiter(speed, samplingInterval, TimeUnit.SECONDS);
        speedLimiter.setEnableSpeed(speedLimiterConfig.isEnableLimiter());
        return speedLimiter;
    }


    @Override
    public void close() {
        try {
            stop();
            if (this.internalUnresolvedDataCollector != null) {
                this.internalUnresolvedDataCollector.close();
                this.internalUnresolvedDataCollector = null;
            }
        } catch (Exception e) {
            _LOGGER.debug("Exception closing plugin container", e);
        }
    }

    protected abstract void open();

    protected abstract void stop();

    public PluginRuningContainer(NodeConfig nodeConfig, List<Plugin> plugins) {
        this.plugins = plugins;
        this.nodeConfig = nodeConfig;
        if (plugins != null && plugins.size() == 1) {
            this.single = true;
            this.plugin = plugins.get(0);
        }
    }


    public Launcher getDataLauncher() {
        throw new RuntimeException("This method must be overridden in the plugin container for data input.");
    }

    public DataRecord process(DataRecord dataRecord) {
        throw new RuntimeException("This method must be overridden in the plugin container for data process.");
    }

    public void write(DataRecord dataRecord) {
        throw new RuntimeException("This method must be overridden in the plugin container for data writer.");
    }

    @Override
    public EngineContext getContext() {
        return context;
    }

    @Override
    public void setContext(EngineContext context) {
        this.context = context;
    }


    public List<Plugin> getPlugins() {
        return plugins;
    }


    public NodeConfig getNodeConfig() {
        return nodeConfig;
    }

    protected Plugin getPlugin() {
        return this.plugin;
    }

    public DataRecord read() {
        throw new RuntimeException("This method must be overridden in the plugin container for data read.");
    }

    protected SpeedLimiter getSpeedLimiter() {
        return this.internalSpeedLimiter;
    }

    protected UnresolvedDataCollector getUnresolvedDataCollector() {
        return this.internalUnresolvedDataCollector;
    }

    protected MetricCenter getMetricCenter() {
        return this.internalMetricCenter;
    }


    public void setEngineSupport(EngineExtSupport engineExtSupport) {
        this.engineExtSupport = engineExtSupport;
    }

}
