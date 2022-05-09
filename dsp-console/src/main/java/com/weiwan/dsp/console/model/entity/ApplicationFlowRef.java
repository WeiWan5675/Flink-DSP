package com.weiwan.dsp.console.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author lion
 * @description 应用流程关联表
 */
@Data
@TableName("dsp_application_flow")
public class ApplicationFlowRef {
    /**
     * 主键id
     */
    private Integer id;
    /**
     * 应用id
     */
    private Integer appId;
    /**
     * 应用名称
     */
    private String appName;
    /**
     * 作业id
     */
    private String jobId;
    /**
     * 流程主键id
     */
    private Integer flowPk;
    /**
     * 流程id
     */
    private String flowId;
    /**
     * 流程名称
     */
    private String flowName;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
}
