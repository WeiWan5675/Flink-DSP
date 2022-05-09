package com.weiwan.dsp.console.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@TableName("dsp_flow_plugin")
@Data
public class FlowPluginRef {

    private Integer id;
    private Integer flowPk;
    private String flowId;
    private String flowName;
    private Integer pluginJarPk;
    private String pluginJarId;
    private String pluginJarName;
    private Integer pluginPk;
    private String pluginId;
    private String pluginName;
    private String pluginClass;
    private Date createTime;
    private Date updateTime;
}
