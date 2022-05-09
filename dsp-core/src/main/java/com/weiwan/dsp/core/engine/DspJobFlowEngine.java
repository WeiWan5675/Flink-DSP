package com.weiwan.dsp.core.engine;

import cn.hutool.core.collection.CollUtil;
import com.weiwan.dsp.api.plugin.PluginConfig;
import com.weiwan.dsp.api.config.flow.FlowConfig;
import com.weiwan.dsp.api.config.flow.NodeConfig;
import com.weiwan.dsp.api.context.EngineContext;
import com.weiwan.dsp.api.context.DspSupport;
import com.weiwan.dsp.api.enums.NodeType;
import com.weiwan.dsp.api.plugin.Plugin;
import com.weiwan.dsp.api.plugin.PluginControl;
import com.weiwan.dsp.common.utils.ReflectUtil;
import com.weiwan.dsp.core.flow.JobFlow;
import com.weiwan.dsp.core.flow.DspJobFlowGraph;
import com.weiwan.dsp.core.nodes.BaseNode;
import com.weiwan.dsp.core.nodes.DspNode;
import com.weiwan.dsp.core.plugin.AbstractPlugin;
import com.weiwan.dsp.core.plugin.DspPluginManager;
import com.weiwan.dsp.core.plugin.PluginFactory;
import com.weiwan.dsp.core.pub.SystemEnvManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/16 10:17
 * @description:
 */
public abstract class DspJobFlowEngine implements DspSupport {

    private static final Logger _LOGGER = LoggerFactory.getLogger(DspJobFlowEngine.class);
    private DspJobFlowGraph dspJobFlowGraph;
    private DspPluginManager pluginManager = DspPluginManager.getInstance();
    private volatile boolean ready;
    private EngineContext engineContext;
    private String jobName;
    private String jobId;

    public static DspJobFlowGraph buildGraph(EngineContext engineContext) {
        _LOGGER.info("Engine started to build JOb Graph");
        _LOGGER.info("Current engine type: {}", engineContext.getEngineConfig().getEngineType());
        //这里把配置解析成Flow
        JobFlow jobFlow = new JobFlow(engineContext.getJobName(), engineContext.getJobId());
        FlowConfig flowConfig = engineContext.getFlowConfig();
        Map<String, NodeConfig> nodes = flowConfig.getNodes();
        jobFlow.setNodeBag(nodes);
        jobFlow.addVertex(jobFlow.findReaderNodes());
        //拿到Nodes, 解析Nodes的依赖关系


        for (String key : nodes.keySet()) {
            NodeConfig nodeConfig = nodes.get(key);
            String nodeId = nodeConfig.getNodeId();
            List<String> nodeDependents = nodeConfig.getNodeDependents();
            if (nodeDependents.size() == 0) {
                continue;
            }

            if (nodeConfig.getNodeType() == NodeType.UNION && nodeDependents.size() < 1) {
                throw new RuntimeException("Union node configuration error, a parent node must be configured");
            }

            if (nodeConfig.getNodeType() == NodeType.SPLIT && nodeDependents.size() != 1) {
                throw new RuntimeException("Split node configuration error, only one input node");
            }

            for (String nodeDependent : nodeDependents) {
                /**
                 * 1. 单节点对单节点
                 * 2. 单节点 -> Split节点
                 * 3. Split节点 -> Union节点
                 * 4. Split节点 -> Split节点
                 * 5. Split节点 -> 单节点
                 * 6. 单节点 -> Union节点
                 */

                //单节点是Split时,我正常添加三条边就可以
                jobFlow.addEdge(nodeDependent, nodeConfig.getNodeId());
            }

        }

        Map<String, DspNode> graphMap = jobFlow.buildGraphNode();
        DspJobFlowGraph dspJobFlowGraph = new DspJobFlowGraph(graphMap, jobFlow);
        _LOGGER.info("Job Graph Build Completed");
        return dspJobFlowGraph;
    }


    public abstract void init();

    public abstract void stop();

    public void execute(DspJobFlowGraph dspJobFlowGraph) {
        this.dspJobFlowGraph = dspJobFlowGraph;
        this.jobName = dspJobFlowGraph.getJobName();
        this.jobId = dspJobFlowGraph.getJobId();
        execute();
    }

    public void execute() {
        if (dspJobFlowGraph != null) {
            internalExecution();
        } else {
            throw new RuntimeException("FlowEngine cannot run because there is no executable job flow graph");
        }
    }

    private void internalExecution() {

        //准备节点
        prepareNode();

        //解析任务图(和计算框架结合)
        ReliableRuntimeEntrance entrance = this.parseGraph(dspJobFlowGraph);

        if (entrance != null && ready) {
            _LOGGER.info("Execute user program from safe job entry");
            entrance.start();
        }
        //后处理
        _LOGGER.info("User operating program has started running");
        postProcessing();
        _LOGGER.info("Flow Engine post-processing job has also been processed");
    }


    private void prepareNode() {

        if (dspJobFlowGraph.getDspNodeMap() == null || dspJobFlowGraph.getDspNodeMap().isEmpty()) {
            throw new RuntimeException("Unable to find any nodes, please check the program");
        }
        //创建所有的插件
        Map<String, DspNode> dspNodeMap = dspJobFlowGraph.getDspNodeMap();
        for (String nodeId : dspNodeMap.keySet()) {
            DspNode dspNode = dspNodeMap.get(nodeId);
            NodeConfig nodeConfig = dspNode.getNodeConfig();
            String nodeName = nodeConfig.getNodeName();
            NodeType nodeType = nodeConfig.getNodeType();

            List<PluginConfig> pluginConfigs = nodeConfig.getPlugins();
            //注册插件,这里防止重复的插件多次加载
//            pluginManager.registerPlugins(PluginMan);
            DspPluginManager pluginManager = DspPluginManager.getInstance();
            SystemEnvManager systemEnvManager = SystemEnvManager.getInstance();
            String dspPluginDir = systemEnvManager.getDspPluginDir();
            pluginManager.registerPlugins(new File(dspPluginDir));
            //创建插件
            Set<Plugin> pluginSet = createPlugins(pluginConfigs);

            //设置插件中的节点属性
            if (!pluginSet.isEmpty()) {
                preparePlugins(nodeId, nodeName, nodeType, pluginSet);
            }

            //转层baseNode
            BaseNode baseNode = (BaseNode) dspNode;
            //把插件设置到节点中

            //处理插件
            List<Plugin> plugins = new ArrayList(pluginSet);
            for (int i = 0; i < plugins.size(); i++) {
                PluginControl pluginControl = (PluginControl) plugins.get(0);
                pluginControl.setPluginId(i);
            }

            baseNode.setPlugins(plugins);
            //设置节点的上下文对象
            baseNode.setContext(engineContext);
        }
        //准备完成
        ready = true;
    }

    private Set<Plugin> createPlugins(List<PluginConfig> plugins) {
        Set<Plugin> pluginSet = new LinkedHashSet<Plugin>();
        try {
            if (plugins == null || plugins.size() == 0) {
                return pluginSet;
            }

            PluginFactory pluginFactory = PluginFactory.getInstance();
            for (PluginConfig pluginConfig : plugins) {
                Plugin plugin = pluginFactory.createPlugin(pluginConfig);
                if (plugin != null) {
                    pluginSet.add(plugin);
                }
            }
        } catch (Exception e) {
            _LOGGER.error("Error creating plugins", e);
            throw new RuntimeException("Unable to create plugins", e);
        }
        return pluginSet;
    }


    private void preparePlugins(String nodeId, String nodeName, NodeType nodeType, Set<Plugin> pluginSet) {
        for (Plugin plugin : pluginSet) {
            //插件创建后的后处理工作,这里主要是和节点相结合,把节点信息保存进去
            if (plugin instanceof AbstractPlugin) {
                AbstractPlugin abstractPlugin = (AbstractPlugin) plugin;
                ReflectUtil.setFieldValue(abstractPlugin, "nodeId", nodeId);
                ReflectUtil.setFieldValue(abstractPlugin, "nodeName", nodeName);
                ReflectUtil.setFieldValue(abstractPlugin, "nodeType", nodeType);
                ReflectUtil.setFieldValue(abstractPlugin, "jobName", jobName);
                ReflectUtil.setFieldValue(abstractPlugin, "jobId", jobId);
            }
        }
    }


    protected Map<String, DspNode> findTheNextLayer(Map<String, DspNode> nodeMap, Map<String, DspNode> tmpNodeMap) {
        for (DspNode node : nodeMap.values()) {
            List<DspNode> children = node.getChildren();
            if (CollUtil.isNotEmpty(children)) {
                if (tmpNodeMap == null) tmpNodeMap = new LinkedHashMap<>();
                final Map<String, DspNode> finalTmpNodeMap = tmpNodeMap;
                children.forEach(c -> finalTmpNodeMap.put(c.getNodeId(), c));
            }
        }
        return tmpNodeMap;
    }


    /**
     * 解析任务图,返回任务的入口
     *
     * @param graph
     * @return 任务入口封装
     */
    public abstract ReliableRuntimeEntrance parseGraph(DspJobFlowGraph graph);


    /**
     * 任务启动后处理工作
     */
    protected abstract void postProcessing();

    /**
     * 启动任务
     */
    protected abstract void run() throws Exception;


    private void runJob() throws Exception {
        if (dspJobFlowGraph != null && ready == true) {
            this.run();
        }
    }

    @Override
    public EngineContext getContext() {
        return this.engineContext;
    }

    @Override
    public void setContext(EngineContext context) {
        this.engineContext = context;
    }

    protected List<DspNode> layerNodeSorting(Map<String, DspNode> nodeMap) {
        LinkedHashMap<String, DspNode> dspNodes = new LinkedHashMap<>();
        LinkedList<DspNode> nodeList = new LinkedList<>(nodeMap.values());
        while (nodeList.size() != 0) {
            DspNode first = nodeList.getFirst();
            List<DspNode> parents = first.getParents();
            AtomicBoolean atomicBoolean = new AtomicBoolean(false);
            for (DspNode parent : parents) {
                if (dspNodes.get(parent.getNodeId()) == null) {
                    if (nodeMap.get(parent.getNodeId()) != null) {
                        atomicBoolean.set(true);
                    }
                }
            }

            if (atomicBoolean.get()) {
                //存在依赖,并且还没有解决
                DspNode dspNode = nodeList.removeFirst();
                nodeList.addLast(dspNode);
            } else {
                DspNode dspNode = nodeList.removeFirst();
                dspNodes.put(dspNode.getNodeId(), dspNode);
            }
        }
        return new ArrayList<>(dspNodes.values());
    }
}
