package com.weiwan.dsp.console.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author Zhu Shaoqin
 * @email zsqmia@163.com
 * @date 2022/3/21 14:29
 * @description
 */
@Data
@TableName("dsp_schedule_task")
public class ScheduleTask {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String taskId;
    private String taskName;
    private Integer taskType;
    private String taskClass;
    private String taskConfigs;
    private String taskDesc;
    private String taskCronExpr;
    private Integer taskStatus;
    private String createUser;
    private Date createTime;
    private Date updateTime;

}
