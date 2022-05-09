package com.weiwan.dsp.console.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.weiwan.dsp.api.config.core.PluginConfigs;
import com.weiwan.dsp.api.config.flow.FlowConfig;
import com.weiwan.dsp.api.config.flow.NodeConfig;
import com.weiwan.dsp.api.enums.NodeType;
import com.weiwan.dsp.api.plugin.PluginConfig;
import com.weiwan.dsp.common.enums.DspResultStatus;
import com.weiwan.dsp.common.exception.DspConsoleException;
import com.weiwan.dsp.common.utils.*;
import com.weiwan.dsp.console.mapper.ApplicationFlowRefMapper;
import com.weiwan.dsp.console.mapper.FlowMapper;
import com.weiwan.dsp.console.mapper.FlowPluginRefMapper;
import com.weiwan.dsp.console.model.PageWrapper;
import com.weiwan.dsp.console.model.dto.FlowDTO;
import com.weiwan.dsp.console.model.dto.PluginDTO;
import com.weiwan.dsp.console.model.entity.*;
import com.weiwan.dsp.console.model.query.FlowQuery;
import com.weiwan.dsp.console.model.query.PageQuery;
import com.weiwan.dsp.console.model.vo.FlowVo;
import com.weiwan.dsp.console.service.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: xiaozhennan
 * @description:
 */
@Service
public class FlowServiceImpl extends ServiceImpl<FlowMapper, Flow> implements FlowService {
    private static final Logger logger = LoggerFactory.getLogger(FlowServiceImpl.class);

    @Autowired
    private FlowMapper flowMapper;

    @Autowired
    private ApplicationFlowRefMapper applicationFlowRefMapper;

    @Autowired
    private FlowPluginRefMapper flowPluginRefMapper;

    @Autowired
    private PluginService pluginService;

    @Autowired
    private UserService userService;

    @Autowired
    @Lazy
    private ApplicationDeployService applicationDeployService;

    @Autowired
    @Lazy
    private ApplicationService applicationService;
    @Autowired
    private ApplicationFlowService applicationFlowService;

    @Override
    public FlowVo findFlowById(Integer flowId) {
        FlowQuery flowQuery = new FlowQuery();
        flowQuery.setId(flowId);
        Flow flow = getFlowQueryWrapper(flowQuery).one();
        FlowDTO flowDTO = FlowDTO.fromFlow(flow);
        FlowVo flowVo = FlowDTO.toFlowVo(flowDTO);
        return flowVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Flow createFlow(FlowDTO flowDTO) {
        FlowConfig flowConfig = flowDTO.getFlowConfig();
        CheckTool.checkNotNull(flowConfig, DspResultStatus.FLOW_NODES_IS_NULL);
        CheckTool.checkNotNull(flowConfig.getNodes(), DspResultStatus.FLOW_NODES_IS_NULL);
        FlowQuery flowQuery = new FlowQuery();
        flowQuery.setFlowName(flowDTO.getFlowName());
        Flow flow = getFlowQueryWrapper(flowQuery).one();
        if (flow != null) {
            throw DspConsoleException.generateIllegalStateException(DspResultStatus.FLOW_EXIST_NAME);
        }

        Set<String> pluginIds = checkFlowPlugin(flowConfig);

        String flowName = flowDTO.getFlowName() + new Date().getTime();
        String flowId = MD5Utils.md5(flowName);
        flowDTO.setFlowId(flowId);
        List<PluginDTO> plugins = pluginService.searchByClass(pluginIds);
        flowDTO.setPlugins(plugins);

        flowDTO.setUpdateTime(new Date());
        flowDTO.setCreateTime(new Date());
        flowDTO.setDisableMark(0);
        flowDTO.setUpdateMark(0);
        String userName = userService.getCurrentUser().getUsername();
        flowDTO.setCreateUser(userName);
        flowDTO.setUpdateUser(userName);
        Flow flowSave = FlowDTO.toFlow(flowDTO);
        save(flowSave);

        //保存dsp_flow_plugin表逻辑
        if (pluginIds != null && pluginIds.size() > 0) {
            //查询plugin表, 获取plugin的list
            List<Plugin> pluginList = pluginService.findPluginByPluginIds(pluginIds);
            //获取pluginJarIds的list
            Map<String, PluginJar> pluginJarMap = findPluginJarMap(pluginList);
            //组装FlowPluginRef
            List<FlowPluginRef> flowPluginRefs = new ArrayList<>();
            for (Plugin plugin : pluginList) {
                FlowPluginRef flowPluginRef = new FlowPluginRef();
                flowPluginRef.setFlowPk(flowSave.getId());
                flowPluginRef.setFlowId(flowId);
                flowPluginRef.setFlowName(flowDTO.getFlowName());
                flowPluginRef.setPluginPk(plugin.getId());
                flowPluginRef.setPluginId(plugin.getPluginId());
                flowPluginRef.setPluginName(plugin.getPluginName());
                flowPluginRef.setPluginClass(plugin.getPluginClass());
                String pluginJarId = plugin.getPluginJarId();
                PluginJar pluginJar = pluginJarMap.get(pluginJarId);
                flowPluginRef.setPluginJarId(pluginJarId);
                flowPluginRef.setPluginJarPk(pluginJar.getId());
                flowPluginRef.setPluginJarName(pluginJar.getPluginJarName());
                flowPluginRef.setCreateTime(new Date());
                flowPluginRefs.add(flowPluginRef);
            }
            //保存到dsp_flow_plugin表
            flowPluginRefMapper.insertBatch(flowPluginRefs);
        }
        return flowSave;
    }

    private Set<String> checkFlowPlugin(FlowConfig flowConfig) {
        Map<String, NodeConfig> nodes = flowConfig.getNodes();
        Set<String> pluginIds = new HashSet<>();
        for (String nodeId : nodes.keySet()) {
            NodeConfig nodeConfig = nodes.get(nodeId);
            List<PluginConfig> plugins = nodeConfig.getPlugins();
            NodeType nodeType = nodeConfig.getNodeType();
            //校验插件是否正确
            if (plugins == null) {
                if (nodeType != NodeType.UNION) {
                    throw DspConsoleException.generateIllegalStateException(DspResultStatus.PLUGIN_IS_REQUIRED);
                }
                continue;
            }
            //校验插件是否能够被加载
            for (PluginConfig pluginConfig : plugins) {
                boolean loaded = pluginService.checkIsLoaded(pluginConfig);
                if (!loaded) {
                    throw DspConsoleException.generateIllegalStateException(DspResultStatus.PLUGIN_ISNOT_LOAD);
                }
                pluginIds.add(pluginConfig.getPluginId());
            }
        }
        return pluginIds;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Flow updateFlow(FlowDTO flowDTO) {
        logger.info("start update flow, flow id: {}", flowDTO.getFlowId());
        FlowConfig flowConfig = flowDTO.getFlowConfig();

        // 检查flow节点是否为空
        CheckTool.checkNotNull(flowConfig, DspResultStatus.FLOW_NODES_IS_NULL);
        CheckTool.checkNotNull(flowConfig.getNodes(), DspResultStatus.FLOW_NODES_IS_NULL);

        //查询是否可以找到老的flow, 如果找不到就报错
        FlowQuery flowQuery = new FlowQuery();
        flowQuery.setId(flowDTO.getId());
        Flow oldFlow = getFlowQueryWrapper(flowQuery).one();
        if (oldFlow == null) {
            throw DspConsoleException.generateIllegalStateException(DspResultStatus.FLOW_IS_NOT_EXIST);
        }
        try {
            // 查询流程所有关联的app引用
            List<ApplicationFlowRef> applicationFlowRefs = new LambdaQueryChainWrapper<>(applicationFlowRefMapper)
                    .eq(ApplicationFlowRef::getFlowPk, flowDTO.getId())
                    .list();
            List<Integer> appIds = new ArrayList<>();
            if (applicationFlowRefs != null && applicationFlowRefs.size() > 0) {
                appIds = applicationFlowRefs.stream().map(a -> a.getAppId()).collect(Collectors.toList());
            }
            //比对flowConfig的md5, 如果不一致, 则需要更新flow_plugin表, 和给关联应用加updateMark
            boolean flowChangeFlag = checkFlowChange(flowConfig, oldFlow);
            if (flowChangeFlag) {
                // flow_plugin表删除旧关联记录
                new LambdaUpdateChainWrapper<>(flowPluginRefMapper)
                        .eq(FlowPluginRef::getFlowPk, flowDTO.getId())
                        .remove();
                Set<String> pluginIds = checkFlowPlugin(flowConfig);
                // 查询plugin和jar信息, 组装FlowPluginRef
                if (pluginIds != null && pluginIds.size() > 0) {
                    // 找到流程引用的所有插件
                    List<Plugin> pluginList = pluginService.findPluginByPluginIds(pluginIds);
                    //根据插件ID找到引用的所有插件jar
                    Map<String, PluginJar> pluginJarMap = findPluginJarMap(pluginList);
                    List<FlowPluginRef> flowPluginRefs = new ArrayList<>();
                    //重新组装插件流程引用
                    for (Plugin plugin : pluginList) {
                        FlowPluginRef flowPluginRef = new FlowPluginRef();
                        flowPluginRef.setFlowPk(flowDTO.getId());
                        flowPluginRef.setFlowId(flowDTO.getFlowId());
                        flowPluginRef.setFlowName(flowDTO.getFlowName());
                        flowPluginRef.setPluginPk(plugin.getId());
                        flowPluginRef.setPluginId(plugin.getPluginId());
                        flowPluginRef.setPluginName(plugin.getPluginName());
                        flowPluginRef.setPluginClass(plugin.getPluginClass());
                        flowPluginRef.setCreateTime(new Date());
                        String pluginJarId = plugin.getPluginJarId();
                        PluginJar pluginJar = pluginJarMap.get(pluginJarId);
                        flowPluginRef.setPluginJarId(pluginJarId);
                        flowPluginRef.setPluginJarPk(pluginJar.getId());
                        flowPluginRef.setPluginJarName(pluginJar.getPluginJarName());
                        flowPluginRefs.add(flowPluginRef);
                    }
                    // flow_plugin表添加新关联记录
                    flowPluginRefMapper.insertBatch(flowPluginRefs);
                }

                // 更新流程关联的作业部署
                for (Integer appId : appIds) {
                    applicationDeployService.updateDeployFlowByAppId(appId, flowDTO);
                }
            }
            // 如果有关联的应用且流名称改变, 则需要更新application和application_flow表中的流名称
            if (applicationFlowRefs != null && applicationFlowRefs.size() > 0) {
                String oldFlowName = applicationFlowRefs.get(0).getFlowName();
                String newFlowName = flowDTO.getFlowName();
                if (!newFlowName.equals(oldFlowName)) {
                    applicationService.updateFlowNameByAppIds(newFlowName, appIds);
                    applicationFlowService.updateFlowNameByAppIds(newFlowName, appIds);
                }
            }

            String currentUser = userService.getCurrentUser().getUsername();
            flowDTO.setUpdateMark(0);
            flowDTO.setUpdateUser(currentUser);
            // 更新流程表, 组装flow
            Flow flow = FlowDTO.toFlow(flowDTO);
            flowMapper.updateById(flow);
            return flow;
        } catch (Exception e) {
            logger.error("update flow failed, flow id: {}", flowDTO.getFlowId(), e);
        }
        return null;
    }

    private boolean checkFlowChange(FlowConfig flowConfig, Flow oldFlow) {
        String flowJson = oldFlow.getFlowJson();
        char[] flowJsonChars = flowJson.toCharArray();
        Arrays.sort(flowJsonChars);
        String oldFlowJsonMd5 = MD5Utils.md5(String.valueOf(flowJsonChars));
        char[] newFlowChars = ObjectUtil.serialize(flowConfig).toCharArray();
        Arrays.sort(newFlowChars);
        String newFlowJsonMd5 = MD5Utils.md5(String.valueOf(newFlowChars));
        boolean flowChangeFlag = !oldFlowJsonMd5.equalsIgnoreCase(newFlowJsonMd5);
        return flowChangeFlag;
    }

    private Map<String, PluginJar> findPluginJarMap(List<Plugin> pluginList) {
        Set<String> pluginJarIds = new HashSet<>();
        for (Plugin plugin : pluginList) {
            String pluginJarId = plugin.getPluginJarId();
            pluginJarIds.add(pluginJarId);
        }
        return pluginService.findPluginJarMapByJarIds(pluginJarIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteFlow(Integer flowId) {
        checkApplicationFlowUse(flowId);
        flowMapper.deleteById(flowId);
        //删除dsp_flow_plugin表中记录
        Wrapper<FlowPluginRef> flowPluginRefWrapper = new QueryWrapper<FlowPluginRef>()
                .eq("flow_pk", flowId);
        flowPluginRefMapper.delete(flowPluginRefWrapper);
        return true;
    }

    @Override
    public PageWrapper<FlowVo> search(FlowQuery flowQuery) {
        //查询分页
        Page<Flow> page = new Page<>(flowQuery.getPageNo(), flowQuery.getPageSize());
        LambdaQueryChainWrapper<Flow> queryWrapper = getFlowQueryWrapper(flowQuery);
        Page<Flow> flowPage = queryWrapper.page(page);
        //获取查询出来的所有记录
        List<Flow> flows = new ArrayList<>();
        if (flowPage.getRecords() != null) {
            flows = flowPage.getRecords();
        }
        //查询引用了flow的application -> applicationFlowRef对象 dsp_application_flow表
        List<ApplicationFlowRef> applicationFlowRefs = new ArrayList<>();
        List<FlowPluginRef> flowPluginRefs = new ArrayList<>();
        List<Integer> flowIds = new ArrayList<>();
        if (flows.size() != 0) {
            //根据插件jarId批量查询
            flowIds = flows.stream().map(p -> p.getId()).collect(Collectors.toList());
            applicationFlowRefs = findAppFlowRefsBYFlowIds(flowIds);
            //查询flow引用的plugin -> flowPluginRef对象 dsp_flow_plugin表
            flowPluginRefs = findPluginRefsByFlowIds(flowIds);
            if (applicationFlowRefs == null) applicationFlowRefs = new ArrayList<>();
            if (flowPluginRefs == null) flowPluginRefs = new ArrayList<>();
        }
        Map<Integer, FlowVo> assemblyMap = new LinkedHashMap<>();
        flows.forEach(flow -> {
            List<ApplicationFlowRef> applicationRefs = new ArrayList<>();
            List<FlowPluginRef> pluginRefs = new ArrayList<>();
            FlowVo flowVo = new FlowVo();
            flowVo.setApplicationRefs(applicationRefs);
            flowVo.setPluginRefs(pluginRefs);
            BeanUtils.copyProperties(flow, flowVo);
            String flowJson = flow.getFlowJson();
            FlowConfig flowConfig = ObjectUtil.deSerialize(flowJson, FlowConfig.class);
            flowVo.setFlowConfig(flowConfig);
            flowVo.setFlowContent(ObjectUtil.deSerialize(flow.getFlowContent(), JSONObject.class));
            assemblyMap.put(flow.getId(), flowVo);
        });

        Set<String> pluginIds = new HashSet<>();
        for (FlowPluginRef flowPluginRef : flowPluginRefs) {
            Integer flowPk = flowPluginRef.getFlowPk();
            if (flowPk != null) {
                FlowVo flowVo = assemblyMap.get(flowPk);
                if (flowVo != null) {
                    pluginIds.add(flowPluginRef.getPluginId());
                    flowVo.getPluginRefs().add(flowPluginRef);
                }
            }
        }

        for (ApplicationFlowRef applicationFlowRef : applicationFlowRefs) {
            Integer flowPk = applicationFlowRef.getFlowPk();
            if (flowPk != null) {
                FlowVo flowVo = assemblyMap.get(flowPk);
                if (flowVo != null) {
                    flowVo.getApplicationRefs().add(applicationFlowRef);
                }
            }
        }
        Collection<FlowVo> flowVos = assemblyMap.values();
        Map<String, PluginDTO> pluginMap = pluginService.findPluginMapByIds(pluginIds);
        if (pluginMap != null && pluginMap.size() > 0) {
            for (FlowVo value : flowVos) {
                FlowConfig flowConfig = value.getFlowConfig();
                Map<String, NodeConfig> nodes = flowConfig.getNodes();
                // 节点循环
                for (NodeConfig nodeConfig : nodes.values()) {
                    List<PluginConfig> plugins = nodeConfig.getPlugins();
                    // Plugins循环
                    for (PluginConfig plugin : plugins) {
                        PluginDTO pluginDTO = pluginMap.get(plugin.getPluginId());
                        PluginConfigs systemConfigs = pluginDTO.getPluginConfigs();
                        PluginConfigs userConfigs = plugin.getPluginConfigs();
                        plugin.setPluginDescription(pluginDTO.getPluginDescription());
                        for (String key : userConfigs.keySet()) {
                            JSONObject val = systemConfigs.getVal(key, JSONObject.class);
                            JSONObject userVal = userConfigs.getVal(key, JSONObject.class);
                            if (val != null && userVal != null) {
                                val.put("value", userVal.get("value"));
                                systemConfigs.put(key, val);
                            }
                        }
                        plugin.setPluginConfigs(systemConfigs);
                    }
                }
            }
        }
        return new PageWrapper<FlowVo>(new ArrayList<>(flowVos), flowPage.getTotal(), flowPage.getCurrent(), flowPage.getPages());
    }

    private List<ApplicationFlowRef> findAppFlowRefsBYFlowIds(List<Integer> flowIds) {
        List<ApplicationFlowRef> applicationFlowRefs = new LambdaQueryChainWrapper<ApplicationFlowRef>(applicationFlowRefMapper)
                .in(ApplicationFlowRef::getFlowPk, flowIds)
                .list();
        return applicationFlowRefs;
    }

    private List<FlowPluginRef> findPluginRefsByFlowIds(List<Integer> flowIds) {
        List<FlowPluginRef> flowPluginRefs = new LambdaQueryChainWrapper<FlowPluginRef>(flowPluginRefMapper)
                .in(FlowPluginRef::getFlowPk, flowIds)
                .list();
        return flowPluginRefs;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disableFlow(FlowDTO flowDTO) {
        Integer id = flowDTO.getId();
        checkApplicationFlowUse(id);
        Flow flow = getById(flowDTO.getId());
        if (flow == null) {
            throw DspConsoleException.generateIllegalStateException(DspResultStatus.FLOW_IS_NOT_EXIST);
        }
        flow.setDisableMark(flowDTO.getDisableMark() == 0 ? 1 : 0);
        LambdaUpdateChainWrapper updateChainWrapper = getFlowUpdateWrapper(flow);
        updateChainWrapper.update(flow);
    }

    @Override
    public FlowDTO searchFlow(Integer flowId) {
        CheckTool.checkIsNull(flowId, DspResultStatus.PARAM_IS_NULL);
        Flow flow = flowMapper.selectById(flowId);
        FlowDTO flowDTO = FlowDTO.fromFlow(flow);
        flowDTO.setFlowContent(ObjectUtil.deSerialize(flow.getFlowContent(), JSONObject.class));
        List<FlowPluginRef> pluginRefsByFlowIds = findPluginRefsByFlowIds(Arrays.asList(flow.getId()));
        List<String> pluginIds = pluginRefsByFlowIds.stream().map(f -> f.getPluginId()).collect(Collectors.toList());
        List<Plugin> plugins = pluginService.findPluginByPluginIds((new HashSet<>(pluginIds)));
        List<PluginDTO> pluginDTOS = new ArrayList<>();
        plugins.forEach(p -> pluginDTOS.add(PluginDTO.fromPlugin(p)));
        flowDTO.setPlugins(pluginDTOS);
        return flowDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkedChange(FlowVo flowVo) {
        if (flowVo.getId() != null) {
            Flow flow = getById(flowVo.getId());
            flow.setUpdateMark(0);
            updateById(flow);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadFlow(FlowDTO flowDTO) {
        //健壮性判断
        CheckTool.checkIsNull(flowDTO, DspResultStatus.PARAM_IS_NULL, "Empty flow info, please check and upload again");

        //校验flowName和flowId是否已存在 (flow表)
        String flowId = flowDTO.getFlowId();
        String flowName = flowDTO.getFlowName();
        List<Flow> flows = new LambdaQueryChainWrapper<>(flowMapper)
                .eq(Flow::getId, flowId)
                .or()
                .eq(Flow::getFlowName, flowName)
                .list();
        if (flows.size() > 0) {
            throw new DspConsoleException(DspResultStatus.FLOW_EXISTS, "Flow exists, flow name: " + flowName + ", flow id: " + flowId);
        }

        //查询数据库准备数据
        Map<String, Plugin> pluginClassMap = pluginService.findPluginClassMap();
        Set<String> pluginJarIds = new HashSet<>();
        pluginClassMap.values().forEach(p -> pluginJarIds.add(p.getPluginJarId()));
        Map<String, PluginJar> pluginJarMap = pluginService.findPluginJarMapByJarIds(pluginJarIds);
        Map<String, NodeConfig> nodeMap = flowDTO.getFlowConfig().getNodes();
        List<Plugin> plugins = new ArrayList<>();

        //校验插件
        for (String nodeName : nodeMap.keySet()) {
            NodeConfig nodeConfig = nodeMap.get(nodeName);
            List<PluginConfig> pluginConfigs = nodeConfig.getPlugins();
            for (PluginConfig pluginConfig : pluginConfigs) {
                String pluginClass = pluginConfig.getPluginClass();
                Plugin plugin = pluginClassMap.get(pluginClass);
                //校验插件是否存在
                CheckTool.checkIsNull(plugin, DspResultStatus.PLUGIN_IS_NOT_FOUNT, "Plugin is not found, plugin class: " + pluginClass);
                //校验插件是否禁用
                String pluginJarId = plugin.getPluginJarId();
                PluginJar pluginJar = pluginJarMap.get(pluginJarId);
                if (pluginJar.getDisableMark() == 1) {
                    throw new DspConsoleException(DspResultStatus.PLUGIN_IS_DISABLE, "Plugin is disabled, please enable plugin jar first, plugin jar id: " + pluginJarId);
                }
                //保存校验通过的plugin, 用于生成flowPluginRef对象
                plugins.add(plugin);
            }
        }

        //生成flow对象
        flowDTO.setDisableMark(0);
        flowDTO.setUpdateMark(0);
        String userName = userService.getCurrentUser().getUsername();
        flowDTO.setCreateUser(userName);
        flowDTO.setUpdateUser(userName);
        flowDTO.setUpdateTime(new Date());
        flowDTO.setCreateTime(new Date());
        Flow flowSave = FlowDTO.toFlow(flowDTO);
        flowSave.setId(null);
        //保存flow表
        save(flowSave);

        //生成flow_plugin对象
        if (plugins != null && plugins.size() > 0) {
            //组装FlowPluginRef
            List<FlowPluginRef> flowPluginRefs = new ArrayList<>();
            for (Plugin plugin : plugins) {
                FlowPluginRef flowPluginRef = new FlowPluginRef();
                flowPluginRef.setFlowPk(flowSave.getId());
                flowPluginRef.setFlowId(flowId);
                flowPluginRef.setFlowName(flowName);
                flowPluginRef.setPluginPk(plugin.getId());
                flowPluginRef.setPluginId(plugin.getPluginId());
                flowPluginRef.setPluginName(plugin.getPluginName());
                flowPluginRef.setPluginClass(plugin.getPluginClass());
                String pluginJarId = plugin.getPluginJarId();
                flowPluginRef.setPluginJarId(pluginJarId);
                PluginJar pluginJar = pluginJarMap.get(pluginJarId);
                flowPluginRef.setPluginJarPk(pluginJar.getId());
                flowPluginRef.setPluginJarName(pluginJar.getPluginJarName());
                flowPluginRef.setCreateTime(new Date());
                flowPluginRefs.add(flowPluginRef);
            }
            //保存到dsp_flow_plugin表
            flowPluginRefMapper.insertBatch(flowPluginRefs);
        }
    }


    private void checkApplicationFlowUse(Integer id) {
        LambdaQueryChainWrapper<ApplicationFlowRef> wrapper = new LambdaQueryChainWrapper<ApplicationFlowRef>(applicationFlowRefMapper);
        List<ApplicationFlowRef> applicationFlowRefs = wrapper.eq(ApplicationFlowRef::getFlowPk, id).list();
        if (applicationFlowRefs != null && applicationFlowRefs.size() > 0) {
            throw DspConsoleException.generateIllegalStateException(DspResultStatus.FLOW_IS_CITED);
        }
    }

    private LambdaUpdateChainWrapper getFlowUpdateWrapper(Flow flow) {
        LambdaUpdateChainWrapper<Flow> eq = new LambdaUpdateChainWrapper<>(flowMapper)
                .eq(CheckTool.checkNotNullOrEmpty(flow.getId()), Flow::getId, flow.getId());
        return eq;
    }

    private LambdaQueryChainWrapper<Flow> getFlowQueryWrapper(FlowQuery query) {
        LambdaQueryChainWrapper<Flow> wrapper = new LambdaQueryChainWrapper<>(flowMapper);
        if (query.getFlowName() != null) {
            wrapper.eq(Flow::getFlowName, query.getFlowName());
        }
        if (query.getFlowId() != null) {
            wrapper.eq(Flow::getFlowId, query.getFlowId());
        }
        if (query.getId() != null) {
            wrapper.eq(Flow::getId, query.getId());
        }
        if (query.getDisableMark() != null) {
            wrapper.eq(Flow::getDisableMark, query.getDisableMark());
        }
        String sortField = query.getSortField();
        PageQuery.SortOrder sortOrder = query.getSortOrder();
        if (StringUtils.isNotBlank(sortField)) {
            if (sortOrder == PageQuery.SortOrder.descend) {
                wrapper.orderByDesc(Flow::getCreateTime);
            } else {
                wrapper.orderByAsc(Flow::getCreateTime);
            }
        } else {
            wrapper.orderByDesc(Flow::getCreateTime);
        }
        return wrapper;
    }


}
