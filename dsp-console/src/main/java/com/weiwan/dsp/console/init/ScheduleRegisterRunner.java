package com.weiwan.dsp.console.init;

import com.weiwan.dsp.api.config.console.TaskConfigs;
import com.weiwan.dsp.common.utils.MD5Utils;
import com.weiwan.dsp.common.utils.ObjectUtil;
import com.weiwan.dsp.console.model.constant.ConsoleConstants;
import com.weiwan.dsp.console.model.entity.ScheduleTask;
import com.weiwan.dsp.console.schedule.ConsoleTaskContext;
import com.weiwan.dsp.console.schedule.SystemScheduleTask;
import com.weiwan.dsp.console.schedule.TaskType;
import com.weiwan.dsp.console.service.ScheduleTaskService;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: xiaozhennan
 * @Date: 2022/4/5 22:31
 * @Package: com.weiwan.dsp.console.init
 * @ClassName: ScheduleRegisterRunner
 * @Description:
 **/
@Component
@ConfigurationProperties("schedule")
public class ScheduleRegisterRunner implements CommandLineRunner {

    @Autowired
    private ScheduleTaskService scheduleJobService;

    private List<SystemScheduleTask> tasks;

    private static final Logger logger = LoggerFactory.getLogger(ScheduleRegisterRunner.class);

    @Autowired
    private Scheduler scheduler;

    @Override
    public void run(String... args) throws Exception {
        //启动系统的定时任务
        logger.info("start the system scheduling task");
        for (SystemScheduleTask task : tasks) {
            try {
                JobDetail jobDetail = getJobDetail(task);
                CronTrigger cronTrigger = getCronTrigger(task);
                scheduler.scheduleJob(jobDetail, cronTrigger);
                logger.info("schedule task: {}, task status: {}", task.getName(), "started");
            } catch (Exception e) {
                logger.warn("schedule task: {}, task status: {}", task.getName(), "failed", e);
            }
        }
        logger.info("The system scheduling task is started and completed");
        // 启动用户的定时任务
        scheduleJobService.start();
    }


    //组装JobDetail
    private JobDetail getJobDetail(SystemScheduleTask task) throws ClassNotFoundException {
        JobDataMap jobDataMap = new JobDataMap();
        Class<? extends Job> jobClass = null;
        jobClass = Class.forName(task.getClassName()).asSubclass(Job.class);
        ConsoleTaskContext<SystemScheduleTask> consoleTaskContext = new ConsoleTaskContext<>();
        consoleTaskContext.setScheduleJob(task);
        consoleTaskContext.setTaskType(TaskType.SYSTEM_TASK);
        if (task.getTaskConfigs() == null) {
            task.setTaskConfigs(new TaskConfigs());
        }
        consoleTaskContext.setTaskConfigs(task.getTaskConfigs());
        jobDataMap.put(ConsoleConstants.JOB_KEY, consoleTaskContext);
        return JobBuilder.newJob()
                .withIdentity(JobKey.jobKey(MD5Utils.md5(task.getName() + task.getClassName())))
                .setJobData(jobDataMap)
                .ofType(jobClass)
                .build();
    }

    //组装CronTrigger
    private CronTrigger getCronTrigger(SystemScheduleTask task) {
        CronTrigger cronTrigger = null;
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(task.getCronExpr());
        cronTrigger = TriggerBuilder.newTrigger()
                .withIdentity(TriggerKey.triggerKey(MD5Utils.md5(task.getName() + task.getClassName())))
                .withSchedule(cronScheduleBuilder)
                .build();
        return cronTrigger;
    }


    public List<SystemScheduleTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<SystemScheduleTask> tasks) {
        this.tasks = tasks;
    }
}
