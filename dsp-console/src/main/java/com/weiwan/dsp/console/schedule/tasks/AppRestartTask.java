package com.weiwan.dsp.console.schedule.tasks;

import com.weiwan.dsp.api.config.console.TaskConfigs;
import com.weiwan.dsp.console.schedule.ConsoleTaskContext;
import com.weiwan.dsp.console.schedule.task.AppScheduleTask;
import com.weiwan.dsp.console.service.ApplicationDeployService;
import com.weiwan.dsp.console.service.ApplicationService;
import com.weiwan.dsp.console.util.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Zhu Shaoqin
 * @email zsqmia@163.com
 * @date 2022/4/14 16:46
 * @description
 */
public class AppRestartTask extends AppScheduleTask {

    private static final Logger logger = LoggerFactory.getLogger(AppRestartTask.class);

    @Override
    protected void executeInternal(ConsoleTaskContext context) {
        TaskConfigs taskConfigs = context.getTaskConfigs();
        String jobId = taskConfigs.getStringVal("jobId");
        try {
            logger.info("App restart timer start running, job id: {}", jobId);
            ApplicationDeployService deployService = SpringUtils.getBean(ApplicationDeployService.class);
            deployService.restartApp(jobId);
            logger.info("App restart timer is running finished, job id: {}", jobId);
        } catch (Exception e) {
            logger.error("App restart timer is running failed, job id: {}", jobId, e);
        }
    }
}
