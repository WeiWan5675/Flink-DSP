package com.weiwan.dsp.console.model.dto;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.weiwan.dsp.api.config.flow.FlowConfig;
import com.weiwan.dsp.common.utils.ObjectUtil;
import com.weiwan.dsp.console.model.entity.Flow;
import com.weiwan.dsp.console.model.vo.FlowVo;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author: xiaozhennan
 * @Date: 2021/9/10 23:02
 * @Package: com.weiwan.dsp.console.model.dto
 * @ClassName: Flow
 * @Description:
 **/
@Data
public class FlowDTO implements Serializable {
    //自增ID
    private Integer id;
    //流程ID
    private String flowId;
    //流程名称
    private String flowName;
    //流程的完整JSON
    private FlowConfig flowConfig;
    //流程的前端JSON
    private JSONObject flowContent;
    //备注
    private String remarkMsg;
    //禁用标识
    private Integer disableMark;
    //更新标识
    private Integer updateMark;
    //创建用户
    private String createUser;
    //修改用户
    private String updateUser;
    //创建时间
    private Date createTime;

    private List<PluginDTO> plugins;
    //修改时间
    private Date updateTime;


    public static FlowDTO fromFlow(Flow dspFlow) {
        FlowDTO dspFlowDTO = new FlowDTO();
        dspFlowDTO.setId(dspFlow.getId());
        dspFlowDTO.setFlowId(dspFlow.getFlowId());
        dspFlowDTO.setFlowName(dspFlow.getFlowName());
        dspFlowDTO.setFlowConfig(ObjectUtil.deSerialize(dspFlow.getFlowJson(), FlowConfig.class));
        dspFlowDTO.setRemarkMsg(dspFlow.getRemarkMsg());
        dspFlowDTO.setDisableMark(dspFlow.getDisableMark());
        dspFlowDTO.setUpdateMark(dspFlow.getUpdateMark());
        dspFlowDTO.setCreateUser(dspFlow.getCreateUser());
        dspFlowDTO.setUpdateUser(dspFlow.getUpdateUser());
        dspFlowDTO.setCreateTime(dspFlow.getCreateTime());
        dspFlowDTO.setUpdateTime(dspFlow.getUpdateTime());
        return dspFlowDTO;
    }

    public static FlowDTO fromFlowVo(FlowVo flowVO) {
        FlowDTO flowDTO = new FlowDTO();
        flowDTO.setId(flowVO.getId());
        flowDTO.setFlowId(flowVO.getFlowId());
        flowDTO.setFlowName(flowVO.getFlowName());
        flowDTO.setDisableMark(flowVO.getDisableMark());
        flowDTO.setUpdateMark(flowVO.getUpdateMark());
        flowDTO.setRemarkMsg(flowVO.getRemarkMsg());
        flowDTO.setCreateUser(flowVO.getCreateUser());
        flowDTO.setUpdateUser(flowVO.getUpdateUser());
        flowDTO.setCreateTime(flowVO.getCreateTime());
        flowDTO.setFlowConfig(flowVO.getFlowConfig());
        flowDTO.setFlowContent(flowVO.getFlowContent());
        return flowDTO;
    }

    public static Flow toFlow(FlowDTO dspFlowDTO) {
        Flow flow = new Flow();
        flow.setId(dspFlowDTO.getId());
        flow.setFlowId(dspFlowDTO.getFlowId());
        flow.setFlowName(dspFlowDTO.getFlowName());
        flow.setFlowJson(ObjectUtil.serialize(dspFlowDTO.getFlowConfig()));
        flow.setRemarkMsg(dspFlowDTO.getRemarkMsg());
        flow.setDisableMark(dspFlowDTO.getDisableMark());
        flow.setUpdateMark(dspFlowDTO.getUpdateMark());
        flow.setCreateTime(dspFlowDTO.getCreateTime());
        flow.setUpdateTime(dspFlowDTO.getUpdateTime());
        flow.setCreateUser(dspFlowDTO.getCreateUser());
        flow.setUpdateUser(dspFlowDTO.getUpdateUser());
        flow.setFlowContent(ObjectUtil.serialize(dspFlowDTO.getFlowContent()));
        return flow;
    }

    public static FlowVo toFlowVo(FlowDTO flowDTO) {
        FlowVo flowVo = new FlowVo();
        flowVo.setId(flowDTO.getId());
        flowVo.setFlowId(flowDTO.getFlowId());
        flowVo.setFlowName(flowDTO.getFlowName());
        flowVo.setFlowConfig(flowDTO.getFlowConfig());
        flowVo.setDisableMark(flowDTO.getDisableMark());
        flowVo.setUpdateMark(flowDTO.getUpdateMark());
        flowVo.setRemarkMsg(flowDTO.getRemarkMsg());
        flowVo.setCreateTime(flowDTO.getCreateTime());
        flowVo.setCreateUser(flowDTO.getCreateUser());
        flowVo.setUpdateUser(flowDTO.getUpdateUser());
        return flowVo;
    }
}
