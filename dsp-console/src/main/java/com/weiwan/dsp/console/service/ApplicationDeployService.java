package com.weiwan.dsp.console.service;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.weiwan.dsp.api.enums.ApplicationState;
import com.weiwan.dsp.console.model.dto.FlowDTO;
import com.weiwan.dsp.console.model.vo.ApplicationVO;
import com.weiwan.dsp.core.deploy.JobDeployExecutor;
import com.weiwan.dsp.console.model.dto.ApplicationDTO;
import com.weiwan.dsp.console.model.dto.ApplicationDeployDTO;
import com.weiwan.dsp.console.model.entity.Application;
import com.weiwan.dsp.console.model.entity.ApplicationDeploy;
import com.weiwan.dsp.console.model.query.ApplicationDeployQuery;

import java.util.List;
import java.util.Map;

/**
 * @author: xiaozhennan
 * @description:
 */
public interface ApplicationDeployService {

    public boolean startApp(ApplicationDTO jobId);

    public boolean stopApp(ApplicationDTO applicationDTO);

    public boolean cancelApp(String jobId);

    public ApplicationState getAppState(String jobId);

    public boolean restartApp(String jobId);

    public boolean deleteDeploy(Application deployId);

    public Page<ApplicationDeploy> searchDeploy(ApplicationDeployQuery deployQuery);

    public ApplicationDeploy searchDeployByJobId(String jobId);

    /**
     * 查询表中是否有部署配置
     * @param jobId
     * @return
     */
    boolean isDeployed(String jobId);

    void updateDeploy(ApplicationDTO dto) throws Exception;

    ApplicationDeployDTO createDeploy(ApplicationDTO dto);

    ApplicationDeployDTO searchById(Integer deployId);

    void markAppChange(List<Integer> collect);

    ApplicationDeployDTO searchByAppId(Integer id);

    Map<String, JobDeployExecutor> getDeployExecutorMap();

    void updateJobState(ApplicationDeployDTO id, ApplicationState state);

    List<ApplicationDeployDTO> searchUnsettledDeploy();

    void refreshDeploy(Integer deployId);

    void updateJobStartTime(ApplicationDeployDTO applicationDeploy);

    void updateJobStopTime(ApplicationDeployDTO applicationDeploy);

    void updateJobRestartMark(ApplicationDeployDTO applicationDeploy);

    void updateDeployFlowByAppId(Integer appId, FlowDTO flowDTO) throws JsonProcessingException;
}
