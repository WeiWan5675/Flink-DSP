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
@TableName("dsp_application_deploy")
public class ApplicationDeploy implements Serializable {
    private static final long serialVersionUID = -4039033758465256627L;
    //部署ID
    private Integer id;
    //关联的应用ID(一个应用只能启动一个部署)
    private Integer appId;
    //作业ID
    private String jobId;
    //作业名称
    private String jobName;
    //作业状态(这个状态会被更新)
    private Integer jobState;
    //作业监控地址
    private String webUrl;
    //部署的引擎类型
    private Integer engineType;
    //部署的引擎模式
    private Integer engineMode;
    //作业的JSON文件
    private String jobJson;
    //作业的JSON文件内容的md5
    private String jobJsonMd5;
    //作业文件
    private String jobFile;
    //需要重启的标识
    private Integer restartMark;
    //部署启动开始时间
    private Date startTime;
    //上一次停止时间
    private Date endTime;
    //部署启动的用户
    private Integer userId;

}
