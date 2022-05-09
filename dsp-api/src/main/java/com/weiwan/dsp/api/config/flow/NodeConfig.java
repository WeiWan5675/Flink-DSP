package com.weiwan.dsp.api.config.flow;

import cn.hutool.core.clone.Cloneable;
import com.alibaba.fastjson.JSONObject;
import com.weiwan.dsp.api.plugin.PluginConfig;
import com.weiwan.dsp.api.config.core.SpeedLimiterConfig;
import com.weiwan.dsp.api.config.core.UnresolvedCollectorConfig;
import com.weiwan.dsp.api.enums.NodeType;
import com.weiwan.dsp.common.config.ConfigOption;
import com.weiwan.dsp.common.config.ConfigOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/2/20 11:35
 * @description:
 */
public class NodeConfig implements Serializable, Cloneable<NodeConfig> {


    private String nodeId;
    private NodeType nodeType;
    private String nodeName;
    private List<String> nodeDependents;
    private List<String> nodeOutputDependents;
    private String nodeDescription;
    private JSONObject nodeInfo;
    private SpeedLimiterConfig speedLimit;
    private UnresolvedCollectorConfig unresolvedCollectorConfig;
    private List<PluginConfig>  plugins;

    public NodeConfig() {
    }


    public String getNodeDescription() {
        return nodeDescription;
    }

    public void setNodeDescription(String nodeDescription) {
        this.nodeDescription = nodeDescription;
    }

    public NodeConfig(String nodeId, NodeType nodeType, String nodeName, List<String> nodeDependents, String nodeDescription, JSONObject nodeInfo, SpeedLimiterConfig speedLimit, UnresolvedCollectorConfig unresolvedCollectorConfig, List<PluginConfig> plugins) {
        this.nodeId = nodeId;
        this.nodeType = nodeType;
        this.nodeName = nodeName;
        this.nodeDependents = nodeDependents;
        this.nodeDescription = nodeDescription;
        this.nodeInfo = nodeInfo;
        this.speedLimit = speedLimit;
        this.unresolvedCollectorConfig = unresolvedCollectorConfig;
        this.plugins = plugins;
    }

    public NodeConfig(String nodeId, NodeType nodeType, String nodeName, List<String> nodeDependents, SpeedLimiterConfig speedLimit, UnresolvedCollectorConfig unresolvedCollectorConfig, List<PluginConfig> plugins) {
        this.nodeId = nodeId;
        this.nodeType = nodeType;
        this.nodeName = nodeName;
        this.nodeDependents = nodeDependents;
        this.speedLimit = speedLimit;
        this.unresolvedCollectorConfig = unresolvedCollectorConfig;
        this.plugins = plugins;
    }

    public JSONObject getNodeInfo() {
        return nodeInfo;
    }

    public void setNodeInfo(JSONObject nodeInfo) {
        this.nodeInfo = nodeInfo;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public List<String> getNodeDependents() {
        return nodeDependents;
    }

    public void setNodeDependents(List<String> nodeDependents) {
        this.nodeDependents = nodeDependents;
    }


    public List<PluginConfig> getPlugins() {
        return plugins;
    }

    public void setPlugins(List<PluginConfig> plugins) {
        this.plugins = plugins;
    }

    public SpeedLimiterConfig getSpeedLimit() {
        return speedLimit;
    }

    public void setSpeedLimit(SpeedLimiterConfig speedLimit) {
        this.speedLimit = speedLimit;
    }

    public UnresolvedCollectorConfig getUnresolvedCollectorConfig() {
        return this.unresolvedCollectorConfig;
    }

    public void setUnresolvedCollectorConfig(UnresolvedCollectorConfig unresolvedCollectorConfig) {
        this.unresolvedCollectorConfig = unresolvedCollectorConfig;
    }

    public List<String> getNodeOutputDependents() {
        return nodeOutputDependents;
    }

    public void setNodeOutputDependents(List<String> nodeOutputDependents) {
        this.nodeOutputDependents = nodeOutputDependents;
    }


    @Override
    public NodeConfig clone() {
        NodeConfig nodeConfig = new NodeConfig();
        nodeConfig.setNodeId(this.getNodeId());
        nodeConfig.setNodeDependents(this.getNodeDependents());
        nodeConfig.setNodeOutputDependents(this.getNodeOutputDependents());
        nodeConfig.setNodeType(this.getNodeType());
        nodeConfig.setNodeName(this.getNodeName());
        nodeConfig.setSpeedLimit(this.getSpeedLimit());
        nodeConfig.setNodeInfo(this.getNodeInfo());
        nodeConfig.setUnresolvedCollectorConfig(this.getUnresolvedCollectorConfig());
        return nodeConfig;
    }
}
