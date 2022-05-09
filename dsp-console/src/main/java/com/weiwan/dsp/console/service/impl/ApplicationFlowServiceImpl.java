package com.weiwan.dsp.console.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.google.common.collect.Maps;
import com.weiwan.dsp.console.mapper.ApplicationFlowRefMapper;
import com.weiwan.dsp.console.model.dto.ApplicationDTO;
import com.weiwan.dsp.console.model.entity.ApplicationFlowRef;
import com.weiwan.dsp.console.service.ApplicationFlowService;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * @author: lion
 * @description:
 */
@Service
public class ApplicationFlowServiceImpl implements ApplicationFlowService {

    @Autowired
    private ApplicationFlowRefMapper applicationFlowRefMapper;

    @Override
    public int save(ApplicationDTO dto) {
        ApplicationFlowRef applicationFlowRef = new ApplicationFlowRef();
        applicationFlowRef.setAppId(dto.getId());
        applicationFlowRef.setAppName(dto.getAppName());
        applicationFlowRef.setJobId(dto.getJobId());
        applicationFlowRef.setFlowPk(dto.getFlowId());
        applicationFlowRef.setFlowId(dto.getFlowDTO().getFlowId());
        applicationFlowRef.setFlowName(dto.getFlowName());
        applicationFlowRef.setCreateTime(new Date());
        applicationFlowRef.setUpdateTime(new Date());
        return applicationFlowRefMapper.insert(applicationFlowRef);
    }

    @Override
    public boolean delete(Integer id) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("app_id", id);
        return applicationFlowRefMapper.deleteByMap(params) > 0;
    }

    @Override
    public int update(ApplicationDTO dto) {
        delete(dto.getId());
        return save(dto);
    }

    @Override
    public List<String> selectUsed(Integer id) {
        List<ApplicationFlowRef> applicationFlowRefs = new LambdaQueryChainWrapper<>(applicationFlowRefMapper)
                .eq(ApplicationFlowRef::getFlowId, id).list();
        List<String> result = Lists.newArrayList();
        if (CollectionUtils.isEmpty(applicationFlowRefs)) {
            return result;
        }
        applicationFlowRefs.forEach(item -> result.add(item.getAppName()));
        return result;
    }

    @Override
    public void updateFlowNameByAppIds(String newFlowName, List<Integer> appIds) {
        new LambdaUpdateChainWrapper<>(applicationFlowRefMapper)
                .set(ApplicationFlowRef::getFlowName, newFlowName)
                .in(ApplicationFlowRef::getAppId, appIds)
                .update();
    }

    @Override
    public void deleteByAppId(Integer appId) {
        new LambdaUpdateChainWrapper<>(applicationFlowRefMapper)
                .eq(ApplicationFlowRef::getAppId, appId)
                .remove();
    }
}
