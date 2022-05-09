package com.weiwan.dsp.console.event;

import com.google.common.eventbus.Subscribe;
import com.weiwan.dsp.console.model.dto.ApplicationDeployDTO;
import com.weiwan.dsp.console.model.entity.ApplicationDeploy;
import com.weiwan.dsp.console.service.ApplicationDeployService;
import com.weiwan.dsp.core.deploy.JobDeployExecutor;
import com.weiwan.dsp.core.event.BusEventListener;
import com.weiwan.dsp.core.event.JobChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: xiaozhennan
 * @Date: 2022/4/9 13:35
 * @Package: com.weiwan.dsp.console.event
 * @ClassName: JobChangeEventListener
 * @Description: 作业状态变化监控
 **/
@Component
public class JobChangeEventListener implements BusEventListener<JobChangeEvent.JobChangeEventWrap, JobChangeEvent.JobChangeEventWrap> {

    private static final Logger logger = LoggerFactory.getLogger(JobChangeEventListener.class);

    @Autowired
    private ApplicationDeployService applicationDeployService;

    @Override
    @Subscribe
    public void handleSuccessEvent(JobChangeEvent.JobChangeEventWrap successEventWarp) {
        JobChangeEvent event = successEventWarp.getEvent();
        JobDeployExecutor jobDeployExecutor = event.getJobDeployExecutor();
        String jobId = jobDeployExecutor.getJobId();
        ApplicationDeploy applicationDeploy = applicationDeployService.searchDeployByJobId(jobId);
        ApplicationDeployDTO applicationDeployDTO = ApplicationDeployDTO.fromApplicationDeploy(applicationDeploy);
        applicationDeployService.updateJobState(applicationDeployDTO, jobDeployExecutor.getApplicationState());
    }

}
