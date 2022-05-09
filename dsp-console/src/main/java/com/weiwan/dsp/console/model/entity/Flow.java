package com.weiwan.dsp.console.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: xiaozhennan
 * @description:
 */
@Data
@TableName("dsp_flow")
public class Flow implements Serializable {

    private static final long serialVersionUID = -4751198296053146549L;

    //自增ID
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    //流程ID
    private String flowId;
    //流程名称
    private String flowName;
    //流程的后端JSON
    private String flowJson;
    //流程的前端JSON
    private String flowContent;
    //备注
    private String remarkMsg;
    //禁用标识
    private Integer disableMark;
    //更新标识
    private Integer updateMark;
    //修改用户
    private String updateUser;
    //创建用户
    private String createUser;
    //创建时间
    private Date createTime;
    //修改时间
    private Date updateTime;

}
