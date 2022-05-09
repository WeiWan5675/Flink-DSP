package com.weiwan.dsp.console.schedule;

import com.weiwan.dsp.api.config.console.TaskConfigs;
import com.weiwan.dsp.common.utils.ObjectUtil;
import com.weiwan.dsp.console.model.constant.ConsoleConstants;
import com.weiwan.dsp.console.model.entity.ScheduleTask;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author Zhu Shaoqin
 * @email zsqmia@163.com
 * @date 2022/3/22 18:38
 * @description
 */
public abstract class ConsoleTask<T> implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        ConsoleTaskContext taskContext = (ConsoleTaskContext) jobDataMap.get(ConsoleConstants.JOB_KEY);
        taskContext.setJobExecutionContext(jobExecutionContext);
        this.executeInternal(taskContext);
    }

    protected abstract void executeInternal(ConsoleTaskContext context);
}
