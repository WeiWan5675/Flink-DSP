package com.weiwan.dsp.console.service.impl.deploy;

import com.weiwan.dsp.core.deploy.JobDeployExecutor;
import com.weiwan.dsp.console.model.dto.ApplicationDeployDTO;

/**
 * @author: xiaozhennan
 * @description:
 */
public class RefreshApplicationEvent extends ApplicationEvent {
    public RefreshApplicationEvent(JobDeployExecutor deployExecutor, ApplicationDeployDTO applicationDeploy) {
        super(deployExecutor, applicationDeploy, ApplicationEventType.REFRESH);
    }

    @Override
    protected void doRefresh(JobDeployExecutor deployExecutor) throws Exception {
        super.doRefresh(deployExecutor);
    }
}
