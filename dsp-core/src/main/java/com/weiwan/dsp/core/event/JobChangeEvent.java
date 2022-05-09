package com.weiwan.dsp.core.event;

import com.weiwan.dsp.core.deploy.JobDeployExecutor;
import com.weiwan.dsp.core.pub.BusEvent;

/**
 * @Author: xiaozhennan
 * @Date: 2022/4/9 13:28
 * @Package: com.weiwan.dsp.core.event
 * @ClassName: JobChangeEvent
 * @Description:
 **/
public class JobChangeEvent implements BusEvent {

    private JobDeployExecutor jobDeployExecutor;

    public JobChangeEvent(JobDeployExecutor jobDeployExecutor) {
        this.jobDeployExecutor = jobDeployExecutor;
    }

    public static class JobChangeEventWrap extends BusEventWarp<JobChangeEvent> {
        public JobChangeEventWrap(JobChangeEvent event) {
            super(event);
        }
    }


    public JobDeployExecutor getJobDeployExecutor() {
        return jobDeployExecutor;
    }

    public void setJobDeployExecutor(JobDeployExecutor jobDeployExecutor) {
        this.jobDeployExecutor = jobDeployExecutor;
    }
}
