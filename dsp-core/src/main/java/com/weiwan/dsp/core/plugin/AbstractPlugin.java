package com.weiwan.dsp.core.plugin;

import com.weiwan.dsp.api.plugin.PluginConfig;
import com.weiwan.dsp.api.context.EngineContext;
import com.weiwan.dsp.api.context.DspSupport;
import com.weiwan.dsp.api.enums.NodeType;
import com.weiwan.dsp.api.enums.PluginType;
import com.weiwan.dsp.api.plugin.Plugin;
import com.weiwan.dsp.core.engine.ext.EngineExtSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/9 14:01
 * @description:
 */
public abstract class AbstractPlugin<T extends PluginConfig> implements Plugin, DspSupport {
    private static final Logger _LOGGER = LoggerFactory.getLogger(AbstractPlugin.class);
    //这些在任务图解析时设置
    private EngineContext context;
    private String nodeId;
    private String nodeName;
    private NodeType nodeType;
    private String jobId;
    private String jobName;

    private PluginConfig pluginConfig;
    private Integer pluginId;
    private String pluginName;
    private PluginType pluginType;
    //运行时动态设置
    EngineExtSupport engineExtSupport;
    private long spendTime;
    private long processCapacity;
    @Override
    public EngineContext getContext() {
        return context;
    }

    @Override
    public void setContext(EngineContext context) {
        this.context = context;
    }


    @Override
    public PluginConfig getPluginConfig() {
        return pluginConfig;
    }

    @Override
    public void setPluginConfig(PluginConfig pluginConfig) {
        this.pluginConfig = pluginConfig;
        this.pluginName = pluginConfig.getPluginName();
        this.pluginType = pluginConfig.getPluginType();
    }

    @Override
    public String getPluginName() {
        return this.pluginName;
    }

    @Override
    public PluginType getPluginType() {
        return this.pluginType;
    }

    public String getNodeId() {
        return nodeId;
    }


    public String getNodeName() {
        return nodeName;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public String getJobId() {
        return jobId;
    }

    public String getJobName() {
        return jobName;
    }

    @Override
    public void setPluginName(String name) {
        this.pluginName = name;
    }

    @Override
    public Integer getPluginId() {
        return this.pluginId;
    }

    @Override
    public void setPluginId(Integer id) {
        this.pluginId = id;
    }

    @Override
    public void setPluginType(PluginType type) {
        this.pluginType = type;
    }

    public abstract void stop();

    public abstract void init(T config);

    protected EngineExtSupport getEngineExtSupport() {
        return engineExtSupport;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractPlugin)) return false;
        AbstractPlugin that = (AbstractPlugin) o;
        return Objects.equals(nodeId, that.nodeId) && Objects.equals(nodeName, that.nodeName) && nodeType == that.nodeType && Objects.equals(jobName, that.jobName) && Objects.equals(pluginConfig, that.pluginConfig) && Objects.equals(pluginId, that.pluginId) && Objects.equals(pluginName, that.pluginName) && pluginType == that.pluginType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeId, nodeName, nodeType, jobName, pluginConfig, pluginId, pluginName, pluginType);
    }

    public void recordSpendTime(long time){
        this.spendTime += time + 1;
    }

    public long getSpendTime() {
        return this.spendTime;
    }

    public void recordProcessCapacity(long capacity){
        this.processCapacity += capacity;
    }

    public long getProcessCapacity() {
        return processCapacity;
    }
}
