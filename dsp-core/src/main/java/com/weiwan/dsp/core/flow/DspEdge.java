package com.weiwan.dsp.core.flow;

import com.weiwan.dsp.api.config.flow.NodeConfig;

/**
 * @author: xiaozhennan
 * @date: 2021/6/8 9:22
 * @description:
 */
public class DspEdge {

    private NodeConfig head;
    private String headNum = "";
    private String lastNum = "";
    private NodeConfig last;

    public DspEdge(NodeConfig head, NodeConfig last) {
        this.head = head;
        this.last = last;
    }

    public DspEdge(NodeConfig head, NodeConfig last, String headNum, String lastNum) {
        this.headNum = headNum;
        this.lastNum = lastNum;
        this.head = head;
        this.last = last;
    }

    public DspEdge() {

    }

    public NodeConfig getHead() {
        return head;
    }

    public void setHead(NodeConfig head) {
        this.head = head;
    }

    public String getHeadNum() {
        return headNum;
    }

    public void setHeadNum(String headNum) {
        this.headNum = headNum;
    }

    public String getLastNum() {
        return lastNum;
    }

    public void setLastNum(String lastNum) {
        this.lastNum = lastNum;
    }

    public NodeConfig getLast() {
        return last;
    }

    public void setLast(NodeConfig last) {
        this.last = last;
    }
}
