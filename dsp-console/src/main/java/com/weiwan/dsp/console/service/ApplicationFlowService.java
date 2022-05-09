package com.weiwan.dsp.console.service;

import com.weiwan.dsp.console.model.dto.ApplicationDTO;

import java.util.List;

/**
 * @author lion
 * @description 应用流程关联Service
 */
public interface ApplicationFlowService {

    int save(ApplicationDTO applicationDTO);

    boolean delete(Integer id);

    int update(ApplicationDTO dto);

    List<String> selectUsed(Integer id);

    void updateFlowNameByAppIds(String newFlowName, List<Integer> appIds);

    void deleteByAppId(Integer appId);
}
