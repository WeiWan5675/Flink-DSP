package com.weiwan.dsp.core.flow;

import com.weiwan.dsp.api.config.flow.NodeConfig;
import com.weiwan.dsp.api.enums.NodeType;
import com.weiwan.dsp.common.utils.ReflectUtil;
import com.weiwan.dsp.core.nodes.*;

/**
 * @author: xiaozhennan
 * @date: 2021/6/11 17:17
 * @description:
 */
public class DspNodeFactory {

    private static final String NODE_CONFIG_NAME = "nodeConfig";
    private static final String NODE_ID = "nodeId";
    private static final String NODE_NAME = "nodeName";
    private static final String NODE_TYPE = "nodeType";

    public static DspNode createNode(NodeConfig nodeConfig) {
        NodeType nodeType = nodeConfig.getNodeType();
        switch (nodeType) {
            case SPLIT:
                return createSplitNode(nodeConfig);
            case UNION:
                return createUnionNode(nodeConfig);
            case READER:
                return createReaderNode(nodeConfig);
            case WRITER:
                return createWriterNode(nodeConfig);
            case PROCESS:
                return createProcessNode(nodeConfig);
        }
        throw new RuntimeException("Unknown node type: " + nodeType.name());
    }

    public static DspNode createProcessNode(NodeConfig nodeConfig) {
        DspProcessNode dspProcessNode = new DspProcessNode();
        setProperty(nodeConfig, dspProcessNode);
        return dspProcessNode;
    }

    private static void setProperty(NodeConfig nodeConfig, DspNode node) {
        ReflectUtil.setFieldValue(DspNode.class, node, NODE_ID, nodeConfig.getNodeId());
        ReflectUtil.setFieldValue(DspNode.class, node, NODE_NAME, nodeConfig.getNodeName());
        ReflectUtil.setFieldValue(DspNode.class, node, NODE_TYPE, nodeConfig.getNodeType());
        ReflectUtil.setFieldValue(DspNode.class, node, NODE_CONFIG_NAME, nodeConfig);
    }

    public static DspNode createWriterNode(NodeConfig nodeConfig) {
        DspWriterNode dspWriterNode = new DspWriterNode();
        setProperty(nodeConfig, dspWriterNode);
        return dspWriterNode;
    }

    public static DspNode createReaderNode(NodeConfig nodeConfig) {
        DspReaderNode dspReaderNode = new DspReaderNode();
        setProperty(nodeConfig, dspReaderNode);
        return dspReaderNode;
    }

    public static DspNode createUnionNode(NodeConfig nodeConfig) {
        DspUnionNode dspUnionNode = new DspUnionNode();
        setProperty(nodeConfig, dspUnionNode);
        return dspUnionNode;
    }

    public static DspNode createSplitNode(NodeConfig nodeConfig) {
        DspSpliterNode dspSpliterNode = new DspSpliterNode();
        setProperty(nodeConfig, dspSpliterNode);
        return dspSpliterNode;
    }

}
