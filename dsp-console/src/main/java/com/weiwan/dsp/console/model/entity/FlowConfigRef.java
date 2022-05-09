package com.weiwan.dsp.console.model.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("dsp_flow_config")
public class FlowConfigRef {
    private Integer id;
    private Integer configPk;
    private String configId;
    private String configName;
    private Integer flowPk;
    private String flowId;
    private String flowName;
    private String nodeId;
    private Integer pluginIndex;
    private String configKey;
    private Date createTime;
    private Date updateTime;
}
