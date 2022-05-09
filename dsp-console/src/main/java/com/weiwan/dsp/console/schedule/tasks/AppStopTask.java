package com.weiwan.dsp.console.schedule.tasks;

import com.weiwan.dsp.api.config.console.TaskConfigs;
import com.weiwan.dsp.console.model.dto.ApplicationDTO;
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
 * @date 2022/4/14 16:45
 * @description
 */
public class AppStopTask extends AppScheduleTask {

    private static final Logger logger = LoggerFactory.getLogger(AppStopTask.class);

    @Override
    protected void executeInternal(ConsoleTaskContext context) {
        //停止哪个app (jobId)
        TaskConfigs taskConfigs = context.getTaskConfigs();
        String jobId = taskConfigs.getStringVal("jobId");
        try {
            logger.info("App stop timer start running, job id: {}", jobId);
            ApplicationService applicationService = SpringUtils.getBean(ApplicationService.class);
            ApplicationDeployService deployService = SpringUtils.getBean(ApplicationDeployService.class);
            //怎么停止
            ApplicationDTO applicationDTO = applicationService.searchByJobId(jobId);
            deployService.stopApp(applicationDTO);
            logger.info("App stop timer is running finished, job id: {}", jobId);
        } catch (Exception e) {
            logger.error("App stop timer is running failed, job id: {}", jobId, e);
        }
    }
}
