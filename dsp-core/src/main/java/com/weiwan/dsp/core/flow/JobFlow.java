package com.weiwan.dsp.core.flow;

import cn.hutool.core.util.ObjectUtil;
import com.weiwan.dsp.api.plugin.PluginConfig;
import com.weiwan.dsp.api.config.flow.NodeConfig;
import com.weiwan.dsp.api.enums.NodeType;
import com.weiwan.dsp.core.nodes.DspNode;

import java.util.*;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/15 10:15
 * @description:
 */
public class JobFlow {

    private String flowId;
    private String flowName;
    private Map<String, NodeConfig> nodeBag = new HashMap<>();
    private List<DspEdge> edges = new ArrayList<>();
    private List<NodeConfig> vertexs = new ArrayList<>();

    public JobFlow(String jobId, String jobName) {
        this.flowId = jobId;
        this.flowName = jobName;
    }

    public JobFlow() {
    }

    //添加一条连接两个节点的边
    public void addEdge(String headNodeId, String lastNodeId) {
        DspEdge dspEdge = null;
        NodeConfig headNodeConfig = nodeBag.get(headNodeId);
        NodeConfig lastNodeConfig = nodeBag.get(lastNodeId);

        //拆分节点的话,需要特殊处理,因为这里需要标记子流
        if (headNodeId.indexOf(".") != -1 && headNodeConfig == null) {
            //说明是拆分节点,需要特殊处理
            String[] split = headNodeId.split("\\.");
            String nodeId = split[0];
            String headNum = split[1];
            NodeConfig nodeConfig = nodeBag.get(nodeId);
            headNodeConfig = ObjectUtil.cloneByStream(nodeConfig);
            headNodeConfig.setNodeId(headNodeId);
            if (lastNodeConfig.getNodeType() == NodeType.SPLIT) {
                //这个时候我要
                String lsNodeId = lastNodeConfig.getNodeId();
                List<PluginConfig> plugins = lastNodeConfig.getPlugins();
                for (int i = 1; i < plugins.size(); i++) {
                    NodeConfig newNodeConfig = ObjectUtil.cloneByStream(lastNodeConfig);
                    String subNodeId = lsNodeId + "." + i;
                    newNodeConfig.setNodeId(subNodeId);
                    edges.add(new DspEdge(headNodeConfig, newNodeConfig));
                }
                return;
            }else{
                edges.add(new DspEdge(headNodeConfig, lastNodeConfig));
            }
            return;
        }
        if (lastNodeConfig.getNodeType() == NodeType.SPLIT) {
            //这个时候我要
            String nodeId = lastNodeConfig.getNodeId();
            List<PluginConfig> plugins = lastNodeConfig.getPlugins();
            for (int i = 1; i < plugins.size(); i++) {
                NodeConfig newNodeConfig = ObjectUtil.cloneByStream(lastNodeConfig);
                String subNodeId = nodeId + "." + i;
                newNodeConfig.setNodeId(subNodeId);
                edges.add(new DspEdge(headNodeConfig, newNodeConfig));
            }
            return;
        }

        if (lastNodeConfig.getNodeType() != NodeType.SPLIT && headNodeConfig.getNodeId().indexOf(".") == -1) {
            edges.add(new DspEdge(headNodeConfig, lastNodeConfig));
            return;
        }
    }

    //添加一组顶点
    public void addVertex(List<NodeConfig> vertexs) {
        if (vertexs != null && vertexs.size() > 0) {
            this.vertexs.addAll(vertexs);
        }
    }


    public List<NodeConfig> findReaderNodes() {
        return this.findNodes(NodeType.READER);
    }

    public List<NodeConfig> findProcessNodes() {
        return findNodes(NodeType.PROCESS);
    }

    public List<NodeConfig> findSpliterNodes() {
        return findNodes(NodeType.SPLIT);
    }

    public List<NodeConfig> findUnionNodes() {
        return findNodes(NodeType.UNION);
    }

    public List<NodeConfig> findWriterNodes() {
        return findNodes(NodeType.WRITER);
    }


    public List<NodeConfig> findNodes(NodeType nodeType) {
        List<NodeConfig> nodes = new ArrayList<>();
        for (String key : nodeBag.keySet()) {
            NodeConfig nodeConfig = nodeBag.get(key);
            if (nodeConfig.getNodeType() == NodeType.READER) {
                nodes.add(nodeConfig);
            }
        }
        return nodes;
    }


    public Map<String, DspNode> buildGraphNode() {
        Map<String, DspNode> dagGraph = new HashMap<>();
        //遍历所有的边,把每个边所有的节点创建出来
        for (DspEdge edge : edges) {
            NodeConfig headConfig = edge.getHead();
            NodeConfig lastConfig = edge.getLast();
            DspNode headNode = dagGraph.get(headConfig.getNodeId());
            DspNode lastNode = dagGraph.get(lastConfig.getNodeId());
            if (headNode == null) {
                headNode = DspNodeFactory.createNode(headConfig);
            }
            if (lastNode == null) {
                lastNode = DspNodeFactory.createNode(lastConfig);
            }

            if (headNode != null && !dagGraph.containsKey(headNode.getNodeId())) {
                dagGraph.put(headNode.getNodeId(), headNode);
            }

            if (lastNode != null && !dagGraph.containsKey(lastNode.getNodeId())) {
                dagGraph.put(lastNode.getNodeId(), lastNode);
            }
            lastNode.addParentsNode(headNode);
            headNode.addChildrenNode(lastNode);
        }
        return dagGraph;
    }


    public List<NodeConfig> getVertexs() {
        return vertexs;
    }


    public Map<String, NodeConfig> getNodeBag() {
        return nodeBag;
    }

    public void setNodeBag(Map<String, NodeConfig> nodeBag) {
        this.nodeBag = nodeBag;
    }

    public List<DspEdge> getEdges() {
        return edges;
    }

    public void setEdges(List<DspEdge> edges) {
        this.edges = edges;
    }

    public String getFlowName() {
        return flowName;
    }

    public String getFlowId() {
        return flowId;
    }
}
