package com.weiwan.dsp.core.engine.flink;

import com.weiwan.dsp.api.config.core.EngineConfig;
import com.weiwan.dsp.api.config.core.FlinkConfigs;
import com.weiwan.dsp.api.config.flow.NodeConfig;
import com.weiwan.dsp.api.context.EngineContext;
import com.weiwan.dsp.api.enums.NodeType;
import com.weiwan.dsp.api.plugin.Plugin;
import com.weiwan.dsp.core.plugin.flink.ConnectorInputPlugin;
import com.weiwan.dsp.core.plugin.flink.ConnectorOutputPlugin;
import com.weiwan.dsp.api.pojo.DataRecord;
import com.weiwan.dsp.core.engine.flink.func.*;
import com.weiwan.dsp.core.flow.JobFlow;
import com.weiwan.dsp.core.engine.DspJobFlowEngine;
import com.weiwan.dsp.core.flow.DspJobFlowGraph;
import com.weiwan.dsp.core.engine.ReliableRuntimeEntrance;
import com.weiwan.dsp.core.nodes.*;
import com.weiwan.dsp.api.plugin.PluginContainer;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/2/9 18:16
 * @description: 数据流引擎
 */
public class FlinkJobFlowEngine extends DspJobFlowEngine {

    private static final Logger logger = LoggerFactory.getLogger(FlinkJobFlowEngine.class);
    private StreamExecutionEnvironment environment;
    private Map<String, DataStream<DataRecord>> streams = new HashMap<>();
    private EngineContext internalContext;
    private static final int MAX_FLOW_NUMBER_OF_LAYERS = 50;
    private EngineConfig engineConfig;
    private DspJobFlowGraph graph;

    @Override
    public void init() {
        this.internalContext = getContext();
        this.engineConfig = internalContext.getEngineConfig();
        this.environment = StreamExecutionEnvironment.getExecutionEnvironment();
        Integer parallelism = this.engineConfig.getEngineConfigs().getVal(FlinkConfigs.JOB_PARALLELISM);
        this.environment.setParallelism(parallelism == null ? 1 : parallelism);
    }

    @Override
    public void stop() {

    }

    @Override
    public ReliableRuntimeEntrance parseGraph(DspJobFlowGraph graph) {
        this.graph = graph;
        if (graph == null) {
            logger.warn("Dsp job flow cannot be empty");
            return null;
        }
        JobFlow jobFlow = graph.getDspFlow();
        if (jobFlow == null) {
            logger.warn("Dsp job flow cannot be empty");
            return null;
        }
        Map<String, DspNode> dspNodeMap = graph.getDspNodeMap();
        if (dspNodeMap == null) {
            logger.warn("The node list is empty. At least three nodes must be provided");
            return null;
        }
        if (jobFlow.getVertexs() == null) {
            logger.warn("Unable to find the starting position of the task graph.");
            return null;
        }
        logger.info("Start the analysis of Job Graph");
        List<NodeConfig> vertexs = jobFlow.getVertexs();
        Map<String, DspNode> vertexNodes = graph.getVertexNodes(vertexs);
        Map<String, DspNode> nodeMap = new LinkedHashMap<>(vertexNodes);
        int ergodicNum = 0;
        while (nodeMap != null && ergodicNum <= MAX_FLOW_NUMBER_OF_LAYERS) {
            nodeMap = ergodicFloor(nodeMap);
            ergodicNum = ergodicNum++;
        }

        logger.info("JobGraph parsing work has been completed, get a safe RuntimeEntrance");
        ReliableRuntimeEntrance reliableRuntimeEntrance = new ReliableRuntimeEntrance(this);
        return reliableRuntimeEntrance;
    }

    private Map<String, DspNode> ergodicFloor(Map<String, DspNode> nodeMap) {
        //处理顶点,同时把顶点下一层取出来
        Map<String, DspNode> tmpNodeMap = this.findTheNextLayer(nodeMap, null);
        //同层节点按依赖进行排序
        List<DspNode> dspNodes = this.layerNodeSorting(nodeMap);
        for (DspNode node : dspNodes) {
            //创建当前节点
            NodeType nodeType = node.getNodeType();
            PluginContainer container = node.getPluginContainer();
            switch (nodeType) {
                //这里要把节点转成实际的流
                case READER:
                    convertReaderNodeToSource(node, container);
                    break;
                case PROCESS:
                    convertProcessNodeToProcessFunc(node, container);
                    break;
                case WRITER:
                    convertWriterNodeToSink(node, container);
                    break;
                case SPLIT:
                    splitNodeToStreams(node, container);
                    break;
                case UNION:
                    unionNodesToOneStream(node, container);
                    break;
                default:
                    throw new RuntimeException("Illegal node type, please check!");
            }

        }
        return tmpNodeMap;
    }


    private void unionNodesToOneStream(DspNode node, PluginContainer container) {
        if (graph.completedId(node.getNodeId()) != null) {
            return;
        }
        DspUnionNode unionNode = (DspUnionNode) node;
        DataStreamUnioner dataStreamUnioner = new DataStreamUnioner(internalContext, container, environment);
        List<DspNode> unionNodeParents = unionNode.getParents();
//        unionNodeParents.forEach(u -> dataStreamUnioner.add(streams.get(u.getNodeId())));
        for (DspNode unionNodeParent : unionNodeParents) {
            DataStream<DataRecord> dataRecordDataStream = streams.get(unionNodeParent.getNodeId());
            dataStreamUnioner.add(dataRecordDataStream);
        }
        DataStream unionStream = dataStreamUnioner.union();
        streams.put(unionNode.getNodeId(), unionStream);
        graph.markCompleted(unionNode.getNodeId(), unionNode.getNodeId());
    }

    private void splitNodeToStreams(DspNode node, PluginContainer container) {
        String splitNodeId = node.getNodeId().split("\\.")[0];
        //split节点有多个,只要其中一个初始化过,就不会在进行初始化了.
        if (graph.completedId(splitNodeId) != null) {
            streams.put(node.getNodeId(), streams.get(graph.completedId(splitNodeId)));
            return;
        }
        DspSpliterNode spliterNode = (DspSpliterNode) node;
        //获取拆分节点的父级节点
        List<DspNode> spliterNodeParents = spliterNode.getParents();
        DspNode dspNode = spliterNodeParents.get(0);
        //获取父级流
        DataStream<DataRecord> parentStream = streams.get(dspNode.getNodeId());
        //创建拆分算子.这里拆分算子是通过OutputTag + ProcessFunc的方式做的
        DataStreamSpliter spliter = new DataStreamSpliter(internalContext, container);
        //拆分算子添加到父级流上
        SingleOutputStreamOperator<DataRecord> process = parentStream.process(spliter).disableChaining();
        process.name(node.getNodeName());
        //从拆分算子中获取所有的tag
        Map<String, DspOutputTag> tags = spliter.getDspTags();
        //根据子节点id取对应的tag,然后把对应的流缓存
        if (null != tags && !tags.isEmpty()) {
            for (String tagKey : tags.keySet()) {
                DspOutputTag tag = tags.get(tagKey);
                if (tag != null) {
                    streams.put(tagKey, process.getSideOutput(tag.getTag()));
                }
            }
        }
        //标记节点处理了
        graph.markCompleted(splitNodeId, node.getNodeId());
    }

    private void convertWriterNodeToSink(DspNode node, PluginContainer container) {
        if (graph.completedId(node.getNodeId()) != null) return;
        DspWriterNode writerNode = (DspWriterNode) node;
        List<Plugin> plugins = writerNode.getPlugins();
        List<DspNode> writerNodeParents = writerNode.getParents();
        if (writerNodeParents != null) {
            DspNode parentNode = writerNodeParents.get(0);
            DataStream<DataRecord> parentStream = streams.get(parentNode.getNodeId());
            SinkFunction<DataRecord> writerSink = null;
            Plugin plugin = plugins.get(0);
            if (plugin instanceof ConnectorOutputPlugin) {
                ConnectorOutputPlugin outputPlugin = (ConnectorOutputPlugin) plugin;
                writerSink = outputPlugin.getSinkFunction(plugin.getPluginConfig());
            } else {
                writerSink = new WriterNodeSink(internalContext, container);
            }
            DataStreamSink<DataRecord> sink = parentStream.addSink(writerSink).disableChaining();
            sink.name(node.getNodeName());
            graph.markCompleted(node.getNodeId(), node.getNodeId());
        }
    }

    private void convertProcessNodeToProcessFunc(DspNode node, PluginContainer container) {
        if (graph.completedId(node.getNodeId()) != null) return;
        DspProcessNode processNode = (DspProcessNode) node;
        ProcessNodeFunc processNodeFunc = new ProcessNodeFunc(internalContext, container);
        List<DspNode> processNodeParents = processNode.getParents();
        if (processNodeParents != null) {
            DspNode parentNode = processNodeParents.get(0);
            DataStream<DataRecord> parentStream = streams.get(parentNode.getNodeId());
            SingleOutputStreamOperator<DataRecord> processStream = parentStream.process(processNodeFunc).disableChaining();
            processStream.name(node.getNodeName());
            streams.put(processNode.getNodeId(), processStream);
            graph.markCompleted(node.getNodeId(), node.getNodeId());
        }
    }

    private void convertReaderNodeToSource(DspNode node, PluginContainer container) {
        if (graph.completedId(node.getNodeId()) != null) return;
        DspReaderNode readerNode = (DspReaderNode) node;
        List<Plugin> plugins = readerNode.getPlugins();
        Plugin plugin = plugins.get(0);
        SourceFunction<DataRecord> source = null;
        if (plugin instanceof ConnectorInputPlugin) {
            ConnectorInputPlugin inputPlugin = (ConnectorInputPlugin) plugin;
            source = inputPlugin.getSourceFunction(plugin.getPluginConfig());
        } else {
            source = new ReaderNodeSource(internalContext, container);
        }
        DataStream<DataRecord> dataStream = environment.addSource(source, node.getNodeName()).disableChaining();
        streams.put(readerNode.getNodeId(), dataStream);
        graph.markCompleted(node.getNodeId(), node.getNodeId());
    }

    @Override
    protected void postProcessing() {

    }

    @Override
    protected void run() throws Exception {
        if (environment != null) {
            if (streams != null && !streams.isEmpty()) {
                realRun();
            }
        }
    }

    private void realRun() throws Exception {
        this.environment.execute();
    }


}
