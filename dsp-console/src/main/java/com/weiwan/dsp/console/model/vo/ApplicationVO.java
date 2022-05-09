package com.weiwan.dsp.console.model.vo;

import com.weiwan.dsp.api.enums.ApplicationState;
import com.weiwan.dsp.api.enums.ApplicationType;
import com.weiwan.dsp.api.enums.EngineMode;
import com.weiwan.dsp.api.enums.EngineType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: xiaozhennan
 * @Date: 2021/10/31 13:49
 * @ClassName: ApplicationVO
 * @Description:
 **/
@Data
public class ApplicationVO implements Serializable {
    //自增ID
    private Integer id;
    //应用ID(JOBID)
    private String jobId;
    //应用名称
    private String appName;
    //应用类型
    private ApplicationType appType;
    private ApplicationState applicationState;
    private ApplicationDeployVo deployVo;

    private ApplicationConfigVO configVo;
    private ApplicationTimerVo timerVo;
    private EngineType engineType;
    private EngineMode engineMode;

    private Integer[] flowId;

    private String flowName;

    private Integer deployId;

    private Date createTime;

    private Date updateTime;

    private Integer userId;

    private String userName;

    private String remarkMsg;
}
