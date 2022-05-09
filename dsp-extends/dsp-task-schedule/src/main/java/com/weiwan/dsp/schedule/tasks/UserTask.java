package com.weiwan.dsp.schedule.tasks;

import com.weiwan.dsp.api.config.console.TaskConfigs;

/**
 * @author Zhu Shaoqin
 * @email zsqmia@163.com
 * @date 2022/3/22 19:36
 * @description
 */
public abstract class UserTask {

    public abstract void execute(TaskConfigs taskConfigs);
}
