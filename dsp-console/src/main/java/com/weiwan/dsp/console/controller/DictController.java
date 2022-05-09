package com.weiwan.dsp.console.controller;

import com.alibaba.fastjson.JSONObject;
import com.weiwan.dsp.api.enums.*;
import com.weiwan.dsp.common.enums.DspResultStatus;
import com.weiwan.dsp.common.exception.DspConsoleException;
import com.weiwan.dsp.console.model.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: xiaozhennan
 * @description:
 */
@RestController
@RequestMapping("dict")
public class DictController {


    @GetMapping("/applicationType")
    public Result<List<JSONObject>> getApplicationType() {
        ApplicationType[] values = ApplicationType.values();
        List<JSONObject> types = new ArrayList<>();
        for (ApplicationType value : values) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", value.getType());
            jsonObject.put("code", value.getCode());
            types.add(jsonObject);
        }
        return Result.success(types);
    }

    @GetMapping("/applicationState")
    public Result<List<JSONObject>> getApplicationState() {
        ApplicationState[] values = ApplicationState.values();
        List<JSONObject> types = new ArrayList<>();
        for (ApplicationState value : values) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("state", value.getState());
            jsonObject.put("code", value.getCode());
            types.add(jsonObject);
        }
        return Result.success(types);
    }

    @GetMapping("/engineMode")
    public Result<List<JSONObject>> getEngineMode(@RequestParam Integer type) {
        EngineType engineType = EngineType.getEngineType(type);
        List<EngineMode> engineModes;
        switch (engineType) {
            case FLINK:
                engineModes = EngineMode.getFlinkEngineMode();
                break;
            case LOCAL:
                engineModes = EngineMode.getLocalEngineMode();
                break;
            case SPARK:
                engineModes = EngineMode.getSparkEngineMode();
                break;
            default:
                throw DspConsoleException.generateIllegalStateException(DspResultStatus.INTERNAL_SERVER_ERROR);
        }
        List<JSONObject> types = new ArrayList<>();
        for (EngineMode value : engineModes) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", value.getType());
            jsonObject.put("code", value.getCode());
            types.add(jsonObject);
        }
        return Result.success(types);
    }


    @GetMapping("/engineType")
    public Result<List<JSONObject>> getEngineType() {
        EngineType[] values = EngineType.values();
        List<JSONObject> types = new ArrayList<>();
        for (EngineType value : values) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("engineClass", value.getEngineClass());
            jsonObject.put("code", value.getCode());
            jsonObject.put("name", value.name());
            types.add(jsonObject);
        }
        return Result.success(types);
    }

    @GetMapping("/configType")
    public Result<List<JSONObject>> getConfigType() {
        ConfigType[] values = ConfigType.values();
        List<JSONObject> types = new ArrayList<>();
        for (ConfigType value : values) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", value.getType());
            jsonObject.put("code", value.getCode());
            types.add(jsonObject);
        }
        return Result.success(types);
    }


    @GetMapping("/nodeType")
    public Result<List<JSONObject>> getNodeType() {
        NodeType[] values = NodeType.values();

        List<JSONObject> types = new ArrayList<>();
        for (NodeType value : values) {
            if (value == NodeType.UNKNOWN) {
                continue;
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", value.getType());
            jsonObject.put("code", value.getCode());
            types.add(jsonObject);
        }
        return Result.success(types);
    }


    @GetMapping("/unresolvedHandlerType")
    public Result getUnresolvedHandlerType() {
        UnresolvedHandlerType[] values = UnresolvedHandlerType.values();
        return Result.success(values);
    }

}
