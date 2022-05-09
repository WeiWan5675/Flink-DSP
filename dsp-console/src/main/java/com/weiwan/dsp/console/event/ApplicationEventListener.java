package com.weiwan.dsp.console.event;

import com.google.common.eventbus.Subscribe;
import com.weiwan.dsp.api.enums.ApplicationState;
import com.weiwan.dsp.console.service.impl.deploy.ApplicationEventType;
import com.weiwan.dsp.core.deploy.JobDeployExecutor;
import com.weiwan.dsp.console.service.impl.deploy.ApplicationEvent;
import com.weiwan.dsp.core.event.BusEventListener;
import com.weiwan.dsp.console.model.dto.ApplicationDeployDTO;
import com.weiwan.dsp.console.service.ApplicationDeployService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
public class ApplicationEventListener implements BusEventListener<ApplicationEvent.ApplicationSuccessEvent, ApplicationEvent.ApplicationFailEvent> {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationEventListener.class);

    @Autowired
    private ApplicationDeployService applicationDeployService;


    @Override
    @Subscribe
    @Transactional(rollbackFor = Exception.class)
    public void handleSuccessEvent(ApplicationEvent.ApplicationSuccessEvent eventWarp) {
        ApplicationEvent applicationEvent = eventWarp.getEvent();
        //这里是用来通知,应用程序事件处理失败或者成功的地方
        JobDeployExecutor deployExecutor = applicationEvent.getDeployExecutor();
        ApplicationDeployDTO applicationDeploy = applicationEvent.getApplicationDeploy();
        logger.debug("Start processing job operation success event, job ID: {}, event type: {}", applicationDeploy.getJobId(), applicationEvent.getEventType().getType());
        try {
            ApplicationEventType eventType = applicationEvent.getEventType();
            switch (eventType) {
                case START:
                    applicationDeployService.updateJobStartTime(applicationDeploy);
                    applicationDeployService.updateJobRestartMark(applicationDeploy);
                    break;
                case CANCEL:
                case STOP:
                    applicationDeployService.updateJobStopTime(applicationDeploy);
                    break;
                case REFRESH:
                default:
            }
            applicationDeployService.updateJobState(applicationDeploy, deployExecutor.getApplicationState());
            handleExpiredDeployer(deployExecutor);
        } catch (Exception exception) {
            logger.debug("failed to process job operation success event, job ID: {}, event type: {}", applicationDeploy.getJobId(), applicationEvent.getEventType().getType(), exception);
        }
    }

    private void handleExpiredDeployer(JobDeployExecutor deployExecutor) {
        Map<String, JobDeployExecutor> deployExecutorMap = applicationDeployService.getDeployExecutorMap();
        ApplicationState applicationState = deployExecutor.getApplicationState();
        if (ApplicationState.isFinalState(applicationState)) {
            deployExecutorMap.remove(deployExecutor.getJobId());
        }
        if (ApplicationState.isRunningState(applicationState)) {
            deployExecutorMap.put(deployExecutor.getJobId(), deployExecutor);
        }
    }

    @Override
    @Subscribe
    @Transactional(rollbackFor = Exception.class)
    public void handleFailEvent(ApplicationEvent.ApplicationFailEvent eventWarp) {
        ApplicationEvent applicationEvent = eventWarp.getEvent();
        //这里是用来通知,应用程序事件处理失败或者成功的地方
        JobDeployExecutor deployExecutor = applicationEvent.getDeployExecutor();
        ApplicationDeployDTO applicationDeploy = applicationEvent.getApplicationDeploy();
        logger.debug("Start processing job operation failure events, job ID: {}, event type: {}", applicationDeploy.getJobId(), applicationEvent.getEventType().getType());
        try {
            applicationDeployService.updateJobState(applicationDeploy, deployExecutor.getApplicationState());
            Map<String, JobDeployExecutor> deployExecutorMap = applicationDeployService.getDeployExecutorMap();
            ApplicationEventType eventType = applicationEvent.getEventType();
            switch (eventType) {
                case START:
                    deployExecutorMap.remove(applicationDeploy.getJobId());
                    deployExecutor.closeDeploy();
                    applicationDeployService.updateJobStopTime(applicationDeploy);
                    break;
                case STOP:
                case CANCEL:
                case REFRESH:
            }
            handleExpiredDeployer(deployExecutor);
            logger.debug("Job operation failed event, job ID: {}, event type: {}", applicationDeploy.getJobId(), applicationEvent.getEventType().getType());
        } catch (Exception exception) {
            if (logger.isDebugEnabled()) {
                logger.error("failed to process job operation failure event, job ID: {}, event type: {}", applicationDeploy.getJobId(), applicationEvent.getEventType().getType(), exception);
            }
        }
    }
}
