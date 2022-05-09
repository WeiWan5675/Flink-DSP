package com.weiwan.dsp.console.model.query;

import com.weiwan.dsp.console.schedule.TaskType;
import lombok.Data;

/**
 * @author Zhu Shaoqin
 * @email zsqmia@163.com
 * @date 2022/3/21 21:30
 * @description
 */
@Data
public class ScheduleTaskQuery extends PageQuery{
    private String id;
    private String taskId;
    private String taskName;
    private TaskType taskType;
    private Integer taskStatus;
}
