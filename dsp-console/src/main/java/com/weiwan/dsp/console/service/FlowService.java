package com.weiwan.dsp.console.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.weiwan.dsp.console.model.PageWrapper;
import com.weiwan.dsp.console.model.dto.FlowDTO;
import com.weiwan.dsp.console.model.entity.Flow;
import com.weiwan.dsp.console.model.query.FlowQuery;
import com.weiwan.dsp.console.model.vo.FlowVo;

/**
 * @author: xiaozhennan
 * @description:
 */
public interface FlowService {

    FlowVo findFlowById(Integer flowId);

    Flow createFlow(FlowDTO dspFlow);

    Flow updateFlow(FlowDTO dspFlow);

    boolean deleteFlow(Integer flowId);


    PageWrapper<FlowVo> search(FlowQuery query);

    void disableFlow(FlowDTO flowDTO);

    FlowDTO searchFlow(Integer flowId);

    void checkedChange(FlowVo flow);

    void uploadFlow(FlowDTO flowDTO);
}
