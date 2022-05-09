package com.weiwan.dsp.console.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.weiwan.dsp.api.config.core.ReferenceConfigs;
import com.weiwan.dsp.api.enums.ConfigCategory;
import com.weiwan.dsp.api.enums.ConfigType;
import com.weiwan.dsp.common.enums.DspResultStatus;
import com.weiwan.dsp.common.exception.DspConsoleException;
import com.weiwan.dsp.common.utils.CheckTool;
import com.weiwan.dsp.common.utils.MD5Utils;
import com.weiwan.dsp.common.utils.ObjectUtil;
import com.weiwan.dsp.console.mapper.ConfigMapper;
import com.weiwan.dsp.console.mapper.FlowConfigRefMapper;
import com.weiwan.dsp.console.model.PageWrapper;
import com.weiwan.dsp.console.model.entity.Config;
import com.weiwan.dsp.console.model.entity.FlowConfigRef;
import com.weiwan.dsp.console.model.query.ConfigQuery;
import com.weiwan.dsp.console.model.vo.ConfigVo;
import com.weiwan.dsp.console.service.ConfigService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ConfigServiceImpl implements ConfigService {

    private static final Logger logger = LoggerFactory.getLogger(ConfigServiceImpl.class);

    @Autowired
    private ConfigMapper configMapper;

    @Autowired
    private FlowConfigRefMapper flowConfigRefMapper;

    @Override
    public PageWrapper<ConfigVo> searchConfig(ConfigQuery query) {
        //分页查询配置列表
        Page<Config> page = new Page<>(query.getPageNo(), query.getPageSize());
        //根据query中的属性获取条件包装器
        LambdaQueryChainWrapper<Config> wrapper = new LambdaQueryChainWrapper<>(configMapper);
        if (query.getId() != null) {
            wrapper.eq(Config::getId, query.getId());
        }
        if (StringUtils.isNotBlank(query.getConfigId())) {
            wrapper.eq(Config::getConfigId, query.getConfigId());
        }
        if (StringUtils.isNotBlank(query.getConfigName())) {
            wrapper.eq(Config::getConfigName, query.getConfigName());
        }
        if (query.getConfigCategory() != null && query.getConfigCategory().length > 0) {
            wrapper.in(Config::getConfigCategory, Arrays.stream(query.getConfigCategory()).map(a -> a.getCode()).collect(Collectors.toList()));
        }
        if (query.getConfigType() != null && query.getConfigType().length > 0) {
            wrapper.in(Config::getConfigType, Arrays.stream(query.getConfigType()).map(a -> a.getCode()).collect(Collectors.toList()));
        }
        if (query.getDisableMark() != null) {
            wrapper.eq(Config::getDisableMark, query.getDisableMark());
        }
        page = wrapper.page(page);
        //查询结果转换成list<Config>
        List<Config> configs = new ArrayList<>();
        if (page.getRecords() != null) {
            configs = page.getRecords();
        }
        //查询每个configId在flow-config关联表中是否存在,并存储到map中
        List<Integer> configPks = new ArrayList<>();
        for (Config config : configs) {
            configPks.add(config.getId());
        }
        List<Integer> refConfigPks = flowConfigRefMapper.checkReferenceExist(configPks);
        List<ConfigVo> configVos = new ArrayList<>();
        //将list转换成list<ConfigVo>
        for (Config config : configs) {
            ConfigVo configVo = new ConfigVo();
            Integer configPk = config.getId();
            configVo.setId(configPk);
            configVo.setConfigId(config.getConfigId());
            configVo.setConfigName(config.getConfigName());
            configVo.setConfigCategory(ConfigCategory.getConfigCategory(config.getConfigCategory()));
            configVo.setConfigType(ConfigType.getConfigType(config.getConfigType()));
            configVo.setConfigMap(ObjectUtil.deSerialize(config.getConfigMap(), ReferenceConfigs.class));
            configVo.setDisableMark(config.getDisableMark());
            configVo.setUsingMark(refConfigPks.contains(configPk) ? 1 : 0);
            configVo.setRemarkMsg(config.getRemarkMsg());
            configVo.setCreateUser(config.getCreateUser());
            configVo.setCreateTime(config.getCreateTime());
            configVo.setUpdateTime(config.getUpdateTime());
            configVos.add(configVo);
        }
        //return wrapper
        return new PageWrapper<ConfigVo>(configVos, page.getTotal(), page.getCurrent(), page.getPages());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createConfig(ConfigVo configVo) {
        //给name判空
        String configName = configVo.getConfigName();
        if (StringUtils.isBlank(configName)) {
            throw DspConsoleException.generateIllegalStateException(DspResultStatus.PARAM_IS_NULL);
        }
        //校验name在数据库中是否存在,checkConfigName如果不为空,则不可创建
        Config checkConfigName = new LambdaQueryChainWrapper<Config>(configMapper)
                .eq(Config::getConfigName, configName)
                .one();
        CheckTool.checkIsNotNull(checkConfigName, DspResultStatus.CONFIG_EXISTS);

        //把configVo转换成config
        //根据name,createtime生成md5作为id
        Date createTime = new Date();
        String configId = MD5Utils.md5(String.format("%s-%s", configName, createTime));
        Config config = new Config();
        config.setConfigId(configId);
        config.setConfigName(configName);
        ConfigCategory configCategory = configVo.getConfigCategory();
        config.setConfigCategory(configCategory == null ? 3 : configCategory.getCode());
        ConfigType configType = configVo.getConfigType();
        config.setConfigType(configType == null ? 9999 : configType.getCode());
        //给configMap判空, 如果是空的, 就赋值一个新对象
        ReferenceConfigs configMap = configVo.getConfigMap();
        if (configMap == null) {
            configMap = new ReferenceConfigs();
        }
        config.setConfigMap(ObjectUtil.serialize(configMap));
        Integer disableMark = configVo.getDisableMark();
        config.setDisableMark(disableMark == null ? 0 : disableMark);
        config.setRemarkMsg(configVo.getRemarkMsg());
        config.setCreateUser(configVo.getCreateUser());
        config.setCreateTime(createTime);

        //保存到数据库
        configMapper.insert(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateConfig(ConfigVo configVo) {
        //校验是否存在同名config
        String configName = configVo.getConfigName();
        Integer configPk = configVo.getId();
        Config checkConfigName = new LambdaQueryChainWrapper<Config>(configMapper)
                .eq(Config::getConfigName, configName)
                .one();
        //如果vo中的name和checkConfigName中的name不一致,且checkConfigName不为空,则不可更改
        //如果可以更改,则先修改flow_config表中的configName
        if (checkConfigName != null) {
            if (checkConfigName.getId() != configPk) {
                throw DspConsoleException.generateIllegalStateException(DspResultStatus.CONFIG_EXISTS);
            }
        } else {
            //批量修改configName字段
            flowConfigRefMapper.updateBatchByConfigPk(configPk, configName);
        }
        //把vo转换成config
        Config config = new Config();
        config.setId(configPk);
        config.setConfigName(configName);
        ConfigCategory configCategory = configVo.getConfigCategory();
        config.setConfigCategory(configCategory == null ? 4 : configCategory.getCode());
        ConfigType configType = configVo.getConfigType();
        config.setConfigType(configType == null ? 9999 : configType.getCode());
        //给configMap判空, 如果是空的, 就赋值一个新对象
        ReferenceConfigs configMap = configVo.getConfigMap();
        if (configMap == null) {
            configMap = new ReferenceConfigs();
        }
        config.setConfigMap(ObjectUtil.serialize(configMap));
        Integer disableMark = configVo.getDisableMark();
        config.setDisableMark(disableMark == null ? 0 : disableMark);
        config.setRemarkMsg(configVo.getRemarkMsg());

        //根据id进行更新
        configMapper.updateById(config);
    }

    @Override
    public boolean deleteConfig(Integer configPk) {
        //判断配置是否被使用,如果被使用中,则不可删除
        List<FlowConfigRef> configRefs = new LambdaQueryChainWrapper<FlowConfigRef>(flowConfigRefMapper)
                .eq(FlowConfigRef::getConfigPk, configPk)
                .list();
        if (configRefs == null || configRefs.size() == 0) {
            configMapper.deleteById(configPk);
            return true;
        }
        return false;
    }
}
