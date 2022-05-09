package com.weiwan.dsp.core.nodes;

import com.weiwan.dsp.api.config.flow.NodeConfig;
import com.weiwan.dsp.api.enums.NodeType;
import com.weiwan.dsp.api.node.Node;

import java.util.*;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/9 16:16
 * @description:
 */
public abstract class DspNode implements Node {

    private String nodeId;
    private String nodeName;
    private NodeType nodeType;
    private NodeConfig nodeConfig;
    private List<DspNode> parents = new ArrayList<>();
    private List<DspNode> children = new ArrayList<>();


    public DspNode(String nodeId, NodeConfig nodeConfig) {
        this.nodeConfig = nodeConfig;
        this.nodeId = nodeId;
        this.nodeType = nodeConfig.getNodeType();
        this.nodeName = nodeConfig.getNodeName();
    }

    public DspNode(){

    }

    public void addParentsNode(DspNode dspNode) {
        parents.add(dspNode);
    }

    public void addChildrenNode(DspNode dspNode) {
        children.add(dspNode);
    }

    public String getNodeId() {
        return nodeId;
    }



    public NodeConfig getNodeConfig() {
        return nodeConfig;
    }

    public List<DspNode> getParents() {
        return parents;
    }


    public List<DspNode> getChildren() {
        return children;
    }

    public String getNodeName() {
        return nodeName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DspNode dspNode = (DspNode) o;
        return Objects.equals(nodeId, dspNode.nodeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeId);
    }

    public NodeType getNodeType() {
        return nodeType;
    }

}
