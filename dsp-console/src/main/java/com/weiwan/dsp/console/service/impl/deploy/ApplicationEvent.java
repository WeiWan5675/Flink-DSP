package com.weiwan.dsp.console.service.impl.deploy;

import com.weiwan.dsp.api.enums.ApplicationState;
import com.weiwan.dsp.core.deploy.DeployExecutor;
import com.weiwan.dsp.core.deploy.JobDeployExecutor;
import com.weiwan.dsp.core.event.BusEventWarp;
import com.weiwan.dsp.core.event.DspEventBus;
import com.weiwan.dsp.console.model.dto.ApplicationDeployDTO;
import com.weiwan.dsp.core.pub.BusEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.concurrent.TimeUnit;

/**
 * @author: xiaozhennan
 * @description:
 */
public abstract class ApplicationEvent implements Runnable, BusEvent {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationEvent.class);

    private static final String JOB_ID_PLACEHOLDER = "JobID";
    private final ApplicationEventType eventType;
    private final JobDeployExecutor deployExecutor;
    private final ApplicationDeployDTO applicationDeploy;

    public ApplicationEvent(DeployExecutor deployExecutor, ApplicationDeployDTO applicationDeploy, ApplicationEventType eventType) {
        this.deployExecutor = (JobDeployExecutor) deployExecutor;
        this.applicationDeploy = applicationDeploy;
        this.eventType = eventType;
    }


    @Override
    public void run() {
        //在线程得ContextMap中写入该event的JobID, 在dps-console中打印的日志会携带JobID
        try {
            MDC.put(JOB_ID_PLACEHOLDER, deployExecutor.getJobConfig().getJobId());
            LOGGER.info("start a job deployment operation, jobId: {}, operation type: {}", this.getDeployExecutor().getJobId(), this.getEventType().getType());
            if (!deployExecutor.canWeReady() || deployExecutor.isNeedRestart()) {
                deployExecutor.initDeploy();
            }
            switch (eventType) {
                case START:
                    doStart(deployExecutor);
                    break;
                case STOP:
                    doStop(deployExecutor);
                    break;
                case CANCEL:
                    doCancel(deployExecutor);
                    break;
                case REFRESH:
                    doRefresh(deployExecutor);
                    break;
                default:
                    throw new UnsupportedOperationException("未知的应用事件类型");
            }
            DspEventBus.pushEvent(new ApplicationSuccessEvent(this));
            LOGGER.info("Job deployment operation completed, jobID: {} , operation type: {}", this.getDeployExecutor().getJobId(), this.getEventType().getType());
            TimeUnit.MILLISECONDS.sleep(3000L);
        } catch (Exception e) {
            LOGGER.error("job deployment operation failed!", e);
            try {
                TimeUnit.MILLISECONDS.sleep(3000L);
                deployExecutor.modifyApplicationState(ApplicationState.FAILED, true);
                TimeUnit.MILLISECONDS.sleep(2000L);
                DspEventBus.pushEvent(new ApplicationFailEvent(this, e));
            } catch (Exception exception) {
            }
        } finally {
            MDC.remove(JOB_ID_PLACEHOLDER);
        }
    }

    protected void doStart(JobDeployExecutor deployExecutor) throws Exception {
        if (deployExecutor != null) {
            try {
                deployExecutor.start();
            } catch (Exception e) {
                LOGGER.error("执行ApplicationEvent异常!", e);
            }
        }
    }

    protected void doRefresh(JobDeployExecutor deployExecutor) throws Exception {
        if (deployExecutor != null) {
            try {
                deployExecutor.refresh();
            } catch (Exception e) {
                LOGGER.error("执行ApplicationEvent异常!", e);
            }
        }
    }


    protected void doCancel(JobDeployExecutor deployExecutor) throws Exception {
        if (deployExecutor != null) {
            try {
                deployExecutor.cancel();
            } catch (Exception e) {
                LOGGER.error("执行ApplicationEvent异常!", e);
            }
        }
    }

    protected void doStop(JobDeployExecutor deployExecutor) throws Exception {
        if (deployExecutor != null) {
            try {
                deployExecutor.stop();
            } catch (Exception e) {
                LOGGER.error("执行ApplicationEvent异常!", e);
            }
        }
    }

    public ApplicationEventType getEventType() {
        return eventType;
    }


    public static class ApplicationFailEvent extends BusEventWarp<ApplicationEvent> {

        public ApplicationFailEvent(ApplicationEvent event) {
            super(event, null);
        }

        public ApplicationFailEvent(ApplicationEvent event, Throwable throwable) {
            super(event);
            this.throwable = throwable;
        }

        public void setThrowable(Throwable throwable) {
            this.throwable = throwable;
        }
    }


    public static class ApplicationSuccessEvent extends BusEventWarp<ApplicationEvent> {

        public ApplicationSuccessEvent(ApplicationEvent event) {
            super(event);
        }
    }

    public JobDeployExecutor getDeployExecutor() {
        return deployExecutor;
    }

    public ApplicationDeployDTO getApplicationDeploy() {
        return applicationDeploy;
    }
}
