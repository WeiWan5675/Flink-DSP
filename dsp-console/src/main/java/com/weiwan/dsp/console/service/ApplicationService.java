package com.weiwan.dsp.console.service;

import com.weiwan.dsp.console.model.PageWrapper;
import com.weiwan.dsp.console.model.dto.ApplicationDTO;
import com.weiwan.dsp.console.model.entity.Application;
import com.weiwan.dsp.console.model.query.ApplicationQuery;
import com.weiwan.dsp.console.model.vo.ApplicationVO;

import java.util.List;

/**
 * @author: xiaozhennan
 * @description:
 */
public interface ApplicationService {

    Application createApp(ApplicationDTO appDto);

    boolean deleteApp(Integer appId);

    void updateApp(ApplicationDTO application);

    PageWrapper<ApplicationVO> searchApp(ApplicationQuery applicationQuery);

    ApplicationDTO searchById(Integer appId);

    List<Application> searchListByFlowId(Integer flowId);

    Application findOneByQuery(ApplicationQuery applicationQuery);

    void updateFlowNameByAppIds(String newFlowName, List<Integer> appIds);

    ApplicationDTO searchByJobId(String jobId);
}
