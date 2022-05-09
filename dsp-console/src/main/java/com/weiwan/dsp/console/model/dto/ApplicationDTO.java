package com.weiwan.dsp.console.model.dto;

import com.weiwan.dsp.api.config.core.CustomConfig;
import com.weiwan.dsp.api.config.core.CoreConfig;
import com.weiwan.dsp.api.config.core.JobConfig;
import com.weiwan.dsp.api.config.core.ScheduleConfig;
import com.weiwan.dsp.api.enums.ApplicationState;
import com.weiwan.dsp.api.enums.EngineMode;
import com.weiwan.dsp.api.enums.EngineType;
import com.weiwan.dsp.common.utils.ObjectUtil;
import com.weiwan.dsp.console.model.entity.Application;
import com.weiwan.dsp.api.enums.ApplicationType;
import com.weiwan.dsp.console.model.vo.ApplicationConfigVO;
import com.weiwan.dsp.console.model.vo.ApplicationTimerVo;
import com.weiwan.dsp.console.model.vo.ApplicationVO;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: xiaozhennan
 * @description:
 */
@Data
public class ApplicationDTO implements Serializable {


    private static final long serialVersionUID = -7411703241179960836L;

    private Integer id;
    private String jobId;
    private String appName;
    private ApplicationType appType;
    private ApplicationState applicationState;
    private Integer deployId;
    private ApplicationDeployDTO deployDTO;
    private EngineType engineType;
    private EngineMode engineMode;
    private Integer flowId;
    private String flowName;
    private FlowDTO flowDTO;
    private CoreConfig coreConfig;
    private JobConfig jobConfig;
    private CustomConfig customConfig;
    private ScheduleConfig scheduleConfig;
    private Integer userId;
    private String userName;
    private String remarkMsg;
    private Date createTime;
    private Date updateTime;


    public static ApplicationDTO fromDspApplicationVo(ApplicationVO application) {
        ApplicationDTO applicationDTO = new ApplicationDTO();
        applicationDTO.setId(application.getId());
        applicationDTO.setJobId(application.getJobId());
        applicationDTO.setAppName(application.getAppName());
        applicationDTO.setAppType(application.getAppType());
        applicationDTO.setApplicationState(application.getApplicationState());
        applicationDTO.setDeployId(application.getDeployId());
        applicationDTO.setEngineType(application.getEngineType());
        applicationDTO.setEngineMode(application.getEngineMode());
        if(application.getFlowId() != null && application.getFlowId().length > 0) applicationDTO.setFlowId(application.getFlowId()[0]);
        applicationDTO.setFlowName(application.getFlowName());
        ApplicationConfigVO configVo = application.getConfigVo();
        applicationDTO.setCoreConfig(configVo.getCore());
        applicationDTO.setJobConfig(configVo.getJob());
        applicationDTO.setCustomConfig(configVo.getCustom());
        ApplicationTimerVo timerVo = application.getTimerVo();
        ApplicationTimer startTimer = new ApplicationTimer();
        ApplicationTimer stopTimer = new ApplicationTimer();
        ApplicationTimer restartTimer = new ApplicationTimer();
        ScheduleConfig scheduleConfig = new ScheduleConfig();
        startTimer.setEnableTimer(timerVo.isEnableStartTimer());
        startTimer.setTimerCron(timerVo.getStartTimerCron());
        startTimer.setTimerType(ApplicationTimer.ApplicationTimerType.START_TIMER);
        startTimer.setJobId(application.getJobId());
        scheduleConfig.put(ApplicationTimer.ApplicationTimerType.START_TIMER.getType(), startTimer);
        stopTimer.setEnableTimer(timerVo.isEnableStopTimer());
        stopTimer.setTimerCron(timerVo.getStopTimerCron());
        stopTimer.setTimerType(ApplicationTimer.ApplicationTimerType.STOP_TIMER);
        stopTimer.setJobId(application.getJobId());
        scheduleConfig.put(ApplicationTimer.ApplicationTimerType.STOP_TIMER.getType(), stopTimer);
        restartTimer.setEnableTimer(timerVo.isEnableRestartTimer());
        restartTimer.setTimerCron(timerVo.getRestartTimerCron());
        restartTimer.setTimerType(ApplicationTimer.ApplicationTimerType.RESTART_TIMER);
        restartTimer.setJobId(application.getJobId());
        scheduleConfig.put(ApplicationTimer.ApplicationTimerType.RESTART_TIMER.getType(), restartTimer);
        applicationDTO.setScheduleConfig(scheduleConfig);
        applicationDTO.setRemarkMsg(application.getRemarkMsg());
        applicationDTO.setUserId(application.getUserId());
        applicationDTO.setUserName(application.getUserName());
        return applicationDTO;
    }


    public static Application toDspApplication(ApplicationDTO dto) {
        Application application = new Application();
        application.setJobId(dto.getJobId());
        application.setAppName(dto.getAppName());
        application.setAppType(dto.getAppType().getCode());
        application.setDeployId(dto.getDeployId());
        application.setEngineType(dto.getEngineType().getCode());
        application.setEngineMode(dto.getEngineMode().getCode());
        application.setFlowId(dto.getFlowId());
        application.setFlowName(dto.getFlowName());
        application.setCoreConfig(ObjectUtil.serialize(dto.getCoreConfig()));
        application.setJobConfig(ObjectUtil.serialize(dto.getJobConfig()));
        application.setCustomConfig(ObjectUtil.serialize(dto.getCustomConfig()));
        application.setScheduleConfig(ObjectUtil.serialize(dto.getScheduleConfig()));
        application.setRemarkMsg(dto.getRemarkMsg());
        application.setUserId(dto.getUserId());
        application.setUserName(dto.getUserName());
        application.setCreateTime(dto.getCreateTime());
        application.setUpdateTime(dto.getUpdateTime());
        application.setAppState(dto.getApplicationState().getCode());
        application.setId(dto.getId());
        return application;
    }

    public static ApplicationDTO fromDspApplication(Application app) {
        ApplicationDTO dto = new ApplicationDTO();
        dto.setId(app.getId());
        dto.setJobId(app.getJobId());
        dto.setAppName(app.getAppName());
        dto.setAppType(ApplicationType.from(app.getAppType()));
        dto.setApplicationState(ApplicationState.from(app.getAppState()));
        dto.setDeployId(app.getDeployId());
        dto.setEngineType(EngineType.getEngineType(app.getEngineType()));
        dto.setEngineMode(EngineMode.getEngineMode(app.getEngineMode()));
        dto.setFlowId(app.getFlowId());
        dto.setFlowName(app.getFlowName());
        if (app.getCoreConfig() != null) {
            dto.setCoreConfig(ObjectUtil.deSerialize(app.getCoreConfig(), CoreConfig.class));
        }
        if (app.getCustomConfig() != null) {
            dto.setCustomConfig(ObjectUtil.deSerialize(app.getCustomConfig(), CustomConfig.class));
        }
        if (app.getJobConfig() != null) {
            dto.setJobConfig(ObjectUtil.deSerialize(app.getJobConfig(), JobConfig.class));
        }
        if (app.getScheduleConfig() != null) {
            dto.setScheduleConfig(ObjectUtil.deSerialize(app.getScheduleConfig(), ScheduleConfig.class));
        }
        dto.setRemarkMsg(app.getRemarkMsg());
        dto.setCreateTime(app.getCreateTime());
        dto.setUpdateTime(app.getUpdateTime());
        dto.setUserId(app.getUserId());
        return dto;
    }
}
