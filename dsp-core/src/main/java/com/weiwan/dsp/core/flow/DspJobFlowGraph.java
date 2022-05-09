package com.weiwan.dsp.core.flow;

import com.weiwan.dsp.api.config.flow.NodeConfig;
import com.weiwan.dsp.core.nodes.DspNode;

import java.util.*;

/**
 * @author: xiaozhennan
 * @date: 2021/6/11 17:44
 * @description:
 */
public class DspJobFlowGraph {
    private String jobId;
    private String jobName;
    private Map<String, DspNode> dspNodeMap;
    private LinkedHashMap<String,String> dspNodeMark = new LinkedHashMap<>();
    private JobFlow jobFlow;

    public DspJobFlowGraph(Map<String, DspNode> dspNodeMap, JobFlow jobFlow) {
        this.dspNodeMap = dspNodeMap;
        this.jobFlow = jobFlow;
        this.jobId = jobFlow.getFlowId();
        this.jobName = jobFlow.getFlowName();
    }

    public Map<String, DspNode> getDspNodeMap() {
        return dspNodeMap;
    }

    public void setDspNodeMap(Map<String, DspNode> dspNodeMap) {
        this.dspNodeMap = dspNodeMap;
    }

    public JobFlow getDspFlow() {
        return jobFlow;
    }

    public void setDspFlow(JobFlow jobFlow) {
        this.jobFlow = jobFlow;
    }

    public Map<String, DspNode> getVertexNodes(List<NodeConfig> vertexs) {
        Map<String, DspNode> vertexMap = new LinkedHashMap<>();
        vertexs.forEach(v -> vertexMap.put(v.getNodeId(), dspNodeMap.get(v.getNodeId())));
        return vertexMap;
    }

    public String getJobName() {
        return this.jobName;
    }

    public String getJobId() {
        return this.jobId;
    }

    public String completedId(String nodeId) {
        if(this.dspNodeMark.containsKey(nodeId)){
            return dspNodeMark.get(nodeId);
        }
        return null;
    }

    public void markCompleted(String nodeId,String dspNode) {
        this.dspNodeMark.put(nodeId, dspNode);
    }
}
