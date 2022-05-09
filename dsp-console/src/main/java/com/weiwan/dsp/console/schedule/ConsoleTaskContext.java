package com.weiwan.dsp.console.schedule;

import com.weiwan.dsp.api.config.console.TaskConfigs;
import com.weiwan.dsp.console.model.entity.ScheduleTask;
import org.quartz.JobExecutionContext;

/**
 * @author Zhu Shaoqin
 * @email zsqmia@163.com
 * @date 2022/3/22 18:41
 * @description
 */
public class ConsoleTaskContext<T> {
    private JobExecutionContext jobExecutionContext;
    private T scheduleTask;
    private TaskType taskType;
    private TaskConfigs taskConfigs = new TaskConfigs();

    public ConsoleTaskContext() {
    }

    public ConsoleTaskContext(JobExecutionContext jobExecutionContext, T scheduleTask) {
        this.jobExecutionContext = jobExecutionContext;
        this.scheduleTask = scheduleTask;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public TaskConfigs getTaskConfigs() {
        return taskConfigs;
    }

    public void setTaskConfigs(TaskConfigs taskConfigs) {
        this.taskConfigs = taskConfigs;
    }

    public JobExecutionContext getJobExecutionContext() {
        return jobExecutionContext;
    }

    public void setJobExecutionContext(JobExecutionContext jobExecutionContext) {
        this.jobExecutionContext = jobExecutionContext;
    }

    public T getScheduleJob() {
        return scheduleTask;
    }

    public void setScheduleJob(T scheduleTask) {
        this.scheduleTask = scheduleTask;
    }
}
