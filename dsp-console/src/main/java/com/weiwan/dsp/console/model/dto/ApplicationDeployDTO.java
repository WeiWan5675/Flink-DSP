package com.weiwan.dsp.console.model.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.weiwan.dsp.api.config.core.DspContextConfig;
import com.weiwan.dsp.api.enums.ApplicationState;
import com.weiwan.dsp.api.enums.EngineMode;
import com.weiwan.dsp.api.enums.EngineType;
import com.weiwan.dsp.common.utils.ObjectUtil;
import com.weiwan.dsp.console.model.entity.Application;
import com.weiwan.dsp.console.model.entity.ApplicationDeploy;
import com.weiwan.dsp.console.model.vo.ApplicationDeployVo;
import com.weiwan.dsp.core.utils.DspConfigFactory;
import lombok.Data;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

/**
 * @author: xiaozhennan
 * @description:
 */
@Data
public class ApplicationDeployDTO {
    //部署ID
    private Integer id;
    //作业ID
    private String jobId;
    //作业名称
    private String jobName;
    //关联的应用ID(一个应用只能启动一个部署)
    private Integer appId;
    //作业状态(这个状态会被更新)
    private ApplicationState jobState;
    //作业的JSON文件
    private DspContextConfig dspContext;
    //部署的引擎类型
    private EngineType engineType;
    //部署的引擎模式
    private EngineMode engineMode;
    //部署生成的作业文件
    private File jobFile;

    private String jobJsonMd5;

    private String webUrl;

    //需要重启的标识
    private Integer restartMark;
    //部署启动开始时间
    private Date startTime;
    //上一次停止时间
    private Date stopTime;
    //部署启动的用户
    private Integer deployUser;

    public static ApplicationDeployDTO fromApplicationDeploy(ApplicationDeploy applicationDeploy) {
        ApplicationDeployDTO dto = new ApplicationDeployDTO();
        dto.setId(applicationDeploy.getId());
        dto.setJobId(applicationDeploy.getJobId());
        dto.setJobName(applicationDeploy.getJobName());
        dto.setAppId(applicationDeploy.getAppId());
        dto.setJobState(ApplicationState.from(applicationDeploy.getJobState()));
        dto.setDspContext(ObjectUtil.deSerialize(applicationDeploy.getJobJson(), DspContextConfig.class));
        dto.setEngineType(EngineType.getEngineType(applicationDeploy.getEngineType()));
        dto.setEngineMode(EngineMode.getEngineMode(applicationDeploy.getEngineMode()));
        dto.setStartTime(applicationDeploy.getStartTime());
        dto.setStopTime(applicationDeploy.getEndTime());
        dto.setRestartMark(applicationDeploy.getRestartMark());
        dto.setWebUrl(applicationDeploy.getWebUrl());
        dto.setJobJsonMd5(applicationDeploy.getJobJsonMd5());
        dto.setJobFile(new File(applicationDeploy.getJobFile()));
        return dto;
    }

    public static ApplicationDeployDTO fromApplication(Application applicationDeploy) {
        ApplicationDeployDTO dto = new ApplicationDeployDTO();
        dto.setId(applicationDeploy.getId());
        dto.setJobId(applicationDeploy.getJobId());
        dto.setJobName(applicationDeploy.getAppName());
        dto.setAppId(applicationDeploy.getId());
        return dto;
    }

    public static ApplicationDeploy toApplicationDeploy(ApplicationDeployDTO dto) throws JsonProcessingException {
        ApplicationDeploy deploy = new ApplicationDeploy();
        deploy.setId(dto.getId());
        deploy.setAppId(dto.getAppId());
        deploy.setJobId(dto.getJobId());
        deploy.setJobName(dto.getJobName());
        deploy.setEngineType(dto.getEngineType().getCode());
        deploy.setEngineMode(dto.getEngineMode().getCode());
        deploy.setJobState(dto.getJobState().getCode());
        deploy.setJobJson(DspConfigFactory.dspConfigToContent(dto.getDspContext().getDsp()));
        deploy.setJobJsonMd5(dto.getJobJsonMd5());
        deploy.setRestartMark(dto.getRestartMark());
        deploy.setStartTime(dto.getStartTime());
        deploy.setEndTime(dto.getStopTime());
        deploy.setJobFile(dto.getJobFile().toString());
        deploy.setUserId(dto.getDeployUser());
        return deploy;
    }

    public static ApplicationDeployVo toApplicationDeployVo(ApplicationDeployDTO dto) {
        ApplicationDeployVo vo = new ApplicationDeployVo();
        vo.setId(dto.getId());
        vo.setJobId(dto.getJobId());
        vo.setAppId(dto.getAppId());
        vo.setJobId(dto.getJobId());
        vo.setJobName(dto.getJobName());
        vo.setEngineMode(dto.getEngineMode());
        vo.setEngineType(dto.getEngineType());
        vo.setRestartMark(dto.getRestartMark());
        if(dto.getStartTime() != null){
            vo.setStartTime(dto.getStartTime().getTime());
        }
        if(dto.getStopTime() != null){
            vo.setEndTime(dto.getStopTime().getTime());
        }
        vo.setAppState(dto.getJobState());
        vo.setWebUrl(dto.getWebUrl());
        if(dto.getJobFile() != null){
            vo.setJobFile(dto.getJobFile().toString());
        }
        return vo;
    }
}
