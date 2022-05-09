package com.weiwan.dsp.console.service.impl.deploy;

import com.weiwan.dsp.core.deploy.JobDeployExecutor;
import com.weiwan.dsp.console.model.dto.ApplicationDeployDTO;

/**
 * @author: xiaozhennan
 * @description:
 */
public class CancelApplicationEvent extends ApplicationEvent {
    public CancelApplicationEvent(JobDeployExecutor deployExecutor, ApplicationDeployDTO applicationDeploy) {
        super(deployExecutor, applicationDeploy, ApplicationEventType.CANCEL);
    }

    @Override
    protected void doCancel(JobDeployExecutor deployExecutor) throws Exception {
        super.doCancel(deployExecutor);
    }
}
