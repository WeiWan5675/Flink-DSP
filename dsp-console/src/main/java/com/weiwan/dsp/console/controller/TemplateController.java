package com.weiwan.dsp.console.controller;

import com.alibaba.fastjson.JSONObject;
import com.weiwan.dsp.api.enums.EngineMode;
import com.weiwan.dsp.api.enums.EngineType;
import com.weiwan.dsp.api.enums.UnresolvedHandlerType;
import com.weiwan.dsp.console.model.Result;
import com.weiwan.dsp.console.model.vo.CoreConfigTemplateVo;
import com.weiwan.dsp.console.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.naming.OperationNotSupportedException;

/**
 * @author: xiaozhennan
 * @description:
 */
@RestController
@RequestMapping("template")
public class TemplateController {

    @Autowired
    private TemplateService templateService;


    @GetMapping("/engineConfigs")
    public Result<JSONObject> getEngineConfigsTemplate(@RequestParam Integer engineTypeCode, @RequestParam Integer engineModeCode) throws OperationNotSupportedException {
        EngineType engineType = EngineType.getEngineType(engineTypeCode);
        EngineMode engineMode = EngineMode.getEngineMode(engineModeCode);
        JSONObject template = null;
        switch (engineType) {
            case FLINK:
                template = templateService.getFlinkEngineTemplate(engineMode);
                break;
            case SPARK:
            case LOCAL:
                throw new OperationNotSupportedException("尚不支持的操作");
        }
        return Result.success(template);
    }


    @GetMapping("/collectorHandlerConfigs")
    public Result<JSONObject> getUnresolvedCollectorConfigsTemplate(@RequestParam String collectorClass) throws OperationNotSupportedException {
        UnresolvedHandlerType unresolvedHandlerType = UnresolvedHandlerType.from(collectorClass);
        JSONObject template;
        if (unresolvedHandlerType != null) {
            template = templateService.getUnresolvedCollectorTemplate(unresolvedHandlerType);
        } else {
            throw new OperationNotSupportedException("尚不支持的操作");
        }

        return Result.success(template);
    }


    @GetMapping("/coreConfig")
    public Result<CoreConfigTemplateVo> getCoreConfigTemplate(@RequestParam Integer appId){
        CoreConfigTemplateVo template = templateService.getCoreConfigTemplate(appId);
        return Result.success(template);
    }



    public Result<CoreConfigTemplateVo> getPluginConfig(@RequestParam Integer flowId, @RequestParam Integer pluginId){
        return null;
    }
}
