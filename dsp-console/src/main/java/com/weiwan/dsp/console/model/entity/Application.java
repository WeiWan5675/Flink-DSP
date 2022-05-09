package com.weiwan.dsp.console.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: xiaozhennan
 * @description:
 */
@Data
@TableName("dsp_application")
public class Application implements Serializable {

    private static final long serialVersionUID = -5530217780059396046L;
    //自增ID
    private Integer id;
    //应用ID(JOBID)
    private String jobId;
    //应用名称
    private String appName;
    //应用类型
    private Integer appType;
    //应用状态
    private Integer appState;
    //关联的部署ID
    private Integer deployId;
    //引擎类型
    private Integer engineType;
    //引擎模式
    private Integer engineMode;
    //关联的Flow的ID
    private Integer flowId;
    //关联的Flow的ID
    private String flowName;
    //核心配置
    private String coreConfig;
    //作业配置
    private String jobConfig;
    //自定义配置
    private String customConfig;
    //调度任务配置
    private String scheduleConfig;
    //备注
    private String remarkMsg;
    //创建者
    private Integer userId;
    //创建者姓名
    private String userName;
    //创建时间
    private Date createTime;
    //更新时间
    private Date updateTime;

}
