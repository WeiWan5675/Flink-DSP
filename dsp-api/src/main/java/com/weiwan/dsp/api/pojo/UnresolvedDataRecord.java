package com.weiwan.dsp.api.pojo;

import com.alibaba.fastjson.JSONObject;
import com.weiwan.dsp.api.enums.NodeType;

import java.io.Serializable;

/**
 * @author: xiaozhennan
 * @date: 2021/6/2 14:24
 * @description:
 */
public class UnresolvedDataRecord<T extends Serializable> implements Serializable {

    private static final long serialVersionUID = -2370969593642301560L;

    public UnresolvedDataRecord() {
    }

    public UnresolvedDataRecord(T dataRecord, String unresolvedType) {
        this.dataRecord = dataRecord;
        this.unresolvedType = unresolvedType;

    }

    public UnresolvedDataRecord(T dataRecord, String unresolvedType, String jobId, String jobName, String nodeId, String nodeName, NodeType nodeType, long timestamp, String msg) {
        this.dataRecord = dataRecord;
        this.unresolvedType = unresolvedType;
        this.jobId = jobId;
        this.jobName = jobName;
        this.nodeId = nodeId;
        this.nodeName = nodeName;
        this.nodeType = nodeType;
        this.timestamp = timestamp;
        this.msg = msg;
    }



    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    private T dataRecord;
    private String unresolvedType;
    private NodeType nodeType;
    private String nodeName;
    private String jobName;
    private String jobId;
    private String nodeId;
    private long timestamp;
    private String msg;


    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public T getDataRecord() {
        return dataRecord;
    }

    public void setDataRecord(T dataRecord) {
        this.dataRecord = dataRecord;
    }

    public String getUnresolvedType() {
        return unresolvedType;
    }

    public void setUnresolvedType(String unresolvedType) {
        this.unresolvedType = unresolvedType;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
