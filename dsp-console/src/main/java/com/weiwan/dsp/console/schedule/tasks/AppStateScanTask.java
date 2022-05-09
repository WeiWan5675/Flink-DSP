package com.weiwan.dsp.console.schedule.tasks;

import com.weiwan.dsp.console.schedule.ConsoleTaskContext;
import com.weiwan.dsp.console.schedule.task.AppReportTask;
import com.weiwan.dsp.console.service.ApplicationDeployService;
import com.weiwan.dsp.console.util.SpringUtils;
import com.weiwan.dsp.core.deploy.JobDeployExecutor;

import java.util.Map;

/**
 * @Author: xiaozhennan
 * @Date: 2022/4/9 14:25
 * @Package: com.weiwan.dsp.console.schedule.tasks
 * @ClassName: AppStateScanTask
 * @Description: 应用状态扫描
 **/
public class AppStateScanTask extends AppReportTask {
    @Override
    protected void executeInternal(ConsoleTaskContext context) {
        ApplicationDeployService deployService = SpringUtils.getBean(ApplicationDeployService.class);
        if(deployService != null) {
            Map<String, JobDeployExecutor> deployExecutorMap = deployService.getDeployExecutorMap();
            for (JobDeployExecutor deployExecutor : deployExecutorMap.values()) {
                try {
                    deployExecutor.refresh();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }
}
