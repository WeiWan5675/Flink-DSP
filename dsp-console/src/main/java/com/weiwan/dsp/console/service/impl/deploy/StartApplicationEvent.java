package com.weiwan.dsp.console.service.impl.deploy;

import com.weiwan.dsp.api.enums.ApplicationState;
import com.weiwan.dsp.core.deploy.JobDeployExecutor;
import com.weiwan.dsp.console.model.dto.ApplicationDeployDTO;

/**
 * @author: xiaozhennan
 * @description:
 */
public class StartApplicationEvent extends ApplicationEvent {

    public StartApplicationEvent(JobDeployExecutor deployExecutor, ApplicationDeployDTO applicationDeploy) {
        super(deployExecutor, applicationDeploy, ApplicationEventType.START);
    }

    @Override
    protected void doStart(JobDeployExecutor deployExecutor) throws Exception {
        try {
            deployExecutor.start();
        } catch (Exception exception) {
            throw exception;
        }
    }
}
