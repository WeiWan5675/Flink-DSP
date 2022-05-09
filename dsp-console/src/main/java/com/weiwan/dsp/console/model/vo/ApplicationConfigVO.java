package com.weiwan.dsp.console.model.vo;

import com.weiwan.dsp.api.config.core.CoreConfig;
import com.weiwan.dsp.api.config.core.CustomConfig;
import com.weiwan.dsp.api.config.core.JobConfig;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: xiaozhennan
 * @Date: 2021/10/31 16:13
 * @ClassName: ApplicationConfigVO
 * @Description:
 **/
public class ApplicationConfigVO  implements Serializable {
    private Integer flowId;
    private String flowName;
    private CoreConfig core;
    private CustomConfig custom;
    private JobConfig job;

    public Integer getFlowId() {
        return flowId;
    }

    public void setFlowId(Integer flowId) {
        this.flowId = flowId;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public CoreConfig getCore() {
        return core;
    }

    public void setCore(CoreConfig core) {
        this.core = core;
    }

    public CustomConfig getCustom() {
        return custom;
    }

    public void setCustom(CustomConfig custom) {
        this.custom = custom;
    }

    public JobConfig getJob() {
        return job;
    }

    public void setJob(JobConfig job) {
        this.job = job;
    }
}
