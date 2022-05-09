package com.weiwan.dsp.console.schedule.task;

import com.weiwan.dsp.common.enums.DspResultStatus;
import com.weiwan.dsp.common.exception.DspConsoleException;
import com.weiwan.dsp.console.model.constant.ConsoleConstants;
import com.weiwan.dsp.console.model.entity.ScheduleTask;
import com.weiwan.dsp.console.schedule.ConsoleTask;
import com.weiwan.dsp.console.schedule.ConsoleTaskContext;
import com.weiwan.dsp.api.config.console.TaskConfigs;
import com.weiwan.dsp.console.schedule.SystemScheduleTask;
import com.weiwan.dsp.schedule.tasks.UserTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * @author Zhu Shaoqin
 * @email zsqmia@163.com
 * @date 2022/3/22 19:13
 * @description
 */
public class UserCustomTask extends ConsoleTask {

    private static final Logger logger = LoggerFactory.getLogger(UserCustomTask.class);

    @Override
    protected void executeInternal(ConsoleTaskContext context) {
        TaskConfigs taskConfigs = context.getTaskConfigs();
        Object scheduleJob = context.getScheduleJob();
        if (scheduleJob instanceof ScheduleTask) {
            ScheduleTask scheduleTask = (ScheduleTask) scheduleJob;
            runMethod(scheduleTask.getTaskClass(), taskConfigs);
        }
    }

    public void runMethod(String className, TaskConfigs taskConfigs) {
        Class<? extends UserTask> jobClass = null;
        Method method = null;
        try {
            jobClass = Class.forName(className).asSubclass(UserTask.class);
            method = jobClass.getDeclaredMethod(ConsoleConstants.USER_TASK_METHOD_NAME, TaskConfigs.class);
            //反射执行方法
            ReflectionUtils.makeAccessible(method);
            UserTask task = jobClass.newInstance();
            method.invoke(task, taskConfigs);
        } catch (Exception e) {
            logger.error("定时任务执行失败, 失败任务类名: {}", jobClass.getName(), e);
            throw new DspConsoleException(DspResultStatus.TASK_EXECUTE_FAILED);
        }
    }
}
