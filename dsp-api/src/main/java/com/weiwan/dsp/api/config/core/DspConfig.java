package com.weiwan.dsp.api.config.core;

import com.weiwan.dsp.api.config.core.CoreConfig;
import com.weiwan.dsp.api.config.core.CustomConfig;
import com.weiwan.dsp.api.config.flow.FlowConfig;

import java.io.Serializable;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/2/20 11:35
 * @description:
 */
public class DspConfig implements Serializable {

    private FlowConfig flow;
    private CoreConfig core;
    private CustomConfig custom;
    private JobConfig job;

    public JobConfig getJob() {
        return job;
    }

    public void setJob(JobConfig job) {
        this.job = job;
    }

    public FlowConfig getFlow() {
        return flow;
    }

    public void setFlow(FlowConfig flow) {
        this.flow = flow;
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
}
