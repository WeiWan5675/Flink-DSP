package com.weiwan.dsp.console.model.vo;

import com.weiwan.dsp.api.config.console.TaskConfigs;
import com.weiwan.dsp.console.schedule.TaskType;
import lombok.Data;

import java.util.Date;

/**
 * @author Zhu Shaoqin
 * @email zsqmia@163.com
 * @date 2022/3/21 16:04
 * @description
 */
@Data
public class ScheduleTaskVo {
    private Integer id;
    private String taskId;
    private String taskName;
    private TaskType taskType;
    private String taskClass;
    private TaskConfigs taskConfigs = new TaskConfigs();
    private String taskDesc;
    private String taskCronExpr;
    private Integer taskStatus;
    private String createUser;
    private Date createTime;
    private Date updateTime;
}
