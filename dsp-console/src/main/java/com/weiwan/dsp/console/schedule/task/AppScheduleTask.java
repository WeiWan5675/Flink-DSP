package com.weiwan.dsp.console.schedule.task;


import com.weiwan.dsp.api.config.console.TaskConfigs;
import com.weiwan.dsp.console.schedule.ConsoleTask;
import com.weiwan.dsp.console.schedule.ConsoleTaskContext;

/**
 * @author Zhu Shaoqin
 * @email zsqmia@163.com
 * @date 2022/3/22 19:13
 * @description
 */
public abstract class AppScheduleTask extends ConsoleTask {

    public void startApp(TaskConfigs taskConfigs) {

    }

    public void stopApp(TaskConfigs taskConfigs) {

    }

    public void restartApp(TaskConfigs taskConfigs) {

    }

    @Override
    protected void executeInternal(ConsoleTaskContext context) {
        TaskConfigs configs = context.getTaskConfigs();
    }
}
