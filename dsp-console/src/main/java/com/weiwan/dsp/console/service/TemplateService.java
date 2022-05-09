package com.weiwan.dsp.console.service;

import com.alibaba.fastjson.JSONObject;
import com.weiwan.dsp.api.enums.EngineMode;
import com.weiwan.dsp.api.enums.UnresolvedHandlerType;
import com.weiwan.dsp.common.config.ConfigOption;
import com.weiwan.dsp.console.model.vo.CoreConfigTemplateVo;

import java.util.List;
import java.util.Map;

/**
 * @author: xiaozhennan
 * @description:
 */
public interface TemplateService {
    JSONObject getFlinkEngineTemplate(EngineMode engineType);

    JSONObject getUnresolvedCollectorTemplate(UnresolvedHandlerType unresolvedHandlerType);

    CoreConfigTemplateVo getCoreConfigTemplate(Integer appId);
}
