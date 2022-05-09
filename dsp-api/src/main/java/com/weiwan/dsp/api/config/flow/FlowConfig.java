package com.weiwan.dsp.api.config.flow;

import java.io.Serializable;
import java.util.Map;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/2/20 11:35
 * @description:
 */
public class FlowConfig implements Serializable {

    private Map<String, NodeConfig> nodes;

    public Map<String, NodeConfig> getNodes() {
        return nodes;
    }

    public void setNodes(Map<String, NodeConfig> nodes) {
        this.nodes = nodes;
    }

    public FlowConfig() {
    }

    public FlowConfig(Map<String, NodeConfig> nodes) {
        this.nodes = nodes;
    }
}
