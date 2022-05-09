package com.weiwan.dsp.console.service.impl.deploy;

import com.weiwan.dsp.core.deploy.JobDeployExecutor;
import com.weiwan.dsp.console.model.dto.ApplicationDeployDTO;

/**
 * @author: xiaozhennan
 * @description:
 */
public class StopApplicationEvent extends ApplicationEvent {
    public StopApplicationEvent(JobDeployExecutor deployExecutor, ApplicationDeployDTO applicationDeploy) {
        super(deployExecutor, applicationDeploy, ApplicationEventType.STOP);
    }

    @Override
    protected void doStop(JobDeployExecutor deployExecutor) throws Exception {
        super.doStop(deployExecutor);
    }
}
