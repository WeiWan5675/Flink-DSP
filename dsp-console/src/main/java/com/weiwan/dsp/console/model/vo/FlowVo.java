package com.weiwan.dsp.console.model.vo;

import com.alibaba.fastjson.JSONObject;
import com.weiwan.dsp.api.config.flow.FlowConfig;
import com.weiwan.dsp.console.model.entity.ApplicationFlowRef;
import com.weiwan.dsp.console.model.entity.FlowPluginRef;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author: xiaozhennan
 * @Date: 2021/10/31 18:48
 * @ClassName: FlowVo
 * @Description:
 **/
@Data
public class FlowVo {

    private Integer id;
    private String flowId;
    private String flowName;
    private Integer disableMark;
    private Integer updateMark;
    private List<ApplicationFlowRef> applicationRefs;
    private List<FlowPluginRef> pluginRefs;
    private String createUser;
    private String updateUser;
    private Date createTime;
    private FlowConfig flowConfig;
    private JSONObject flowContent;
    private String remarkMsg;
}
