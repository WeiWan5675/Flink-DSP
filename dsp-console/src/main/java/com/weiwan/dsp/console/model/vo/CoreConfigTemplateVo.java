package com.weiwan.dsp.console.model.vo;

import com.alibaba.fastjson.JSON;
import com.weiwan.dsp.api.config.core.CoreConfig;
import com.weiwan.dsp.api.config.core.EngineConfig;
import com.weiwan.dsp.api.config.core.SpeedLimiterConfig;
import com.weiwan.dsp.api.config.core.UnresolvedCollectorConfig;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author: xiaozhennan
 * @description:
 */
public class CoreConfigTemplateVo extends TemplateVo {

    private Map<String, Object> catalog = new LinkedHashMap<>();

    public Map<String, Object> getCatalog() {
        return catalog;
    }

    public void setCatalog(Map<String, Object> catalog) {
        this.catalog = catalog;
    }

    public CoreConfigTemplateVo() {
        Map<String, Object> engineConfigMap = getEngineConfigMap();
        catalog.put(CoreConfig.ENGINE_CONFIG.fullKey(), engineConfigMap);
        Map<String, Object> speedLimiterMap = getSpeedLimiterConfigMap();
        catalog.put(CoreConfig.SPEED_LIMITER.fullKey(), speedLimiterMap);
        Map<String, Object> unresolvedCollectorMap = getUnresolvedCollectorMap();
        catalog.put(CoreConfig.UNRESOLVED_COLLECTOR.fullKey(), unresolvedCollectorMap);
        Map<String, Object> metricConfigMap = getMetricConfigMap();
        catalog.put(CoreConfig.METRIC_CONFIG.fullKey(), metricConfigMap);
    }

    private Map<String, Object> getMetricConfigMap() {
        Map<String, Object> metricConfigMap = new LinkedHashMap<>();
        return metricConfigMap;
    }

    private Map<String, Object> getUnresolvedCollectorMap() {
        Map<String, Object> unresolvedCollectorMap = new LinkedHashMap<>();
        unresolvedCollectorMap.put(UnresolvedCollectorConfig.ENABLE_COLLECTOR.fullKey(), UnresolvedCollectorConfig.ENABLE_COLLECTOR);
        unresolvedCollectorMap.put(UnresolvedCollectorConfig.COLLECTOR_HANDLER.fullKey(), getDictApi(UnresolvedCollectorConfig.COLLECTOR_HANDLER.key()));
        unresolvedCollectorMap.put(UnresolvedCollectorConfig.MAX_SAMPLING_RECORD.fullKey(), UnresolvedCollectorConfig.MAX_SAMPLING_RECORD);
        unresolvedCollectorMap.put(UnresolvedCollectorConfig.SAMPLING_INTERVAL.fullKey(), UnresolvedCollectorConfig.SAMPLING_INTERVAL);
        unresolvedCollectorMap.put(UnresolvedCollectorConfig.COLLECTOR_HANDLER_CONFIGS.fullKey(), getTemplateApi(UnresolvedCollectorConfig.COLLECTOR_HANDLER_CONFIGS.key()));
        return unresolvedCollectorMap;
    }

    private Map<String, Object> getSpeedLimiterConfigMap() {
        Map<String, Object> speedLimiterMap = new LinkedHashMap<>();
        speedLimiterMap.put(SpeedLimiterConfig.READ_SPEED.fullKey(), SpeedLimiterConfig.READ_SPEED);
        speedLimiterMap.put(SpeedLimiterConfig.PROCESS_SPEED.fullKey(), SpeedLimiterConfig.PROCESS_SPEED);
        speedLimiterMap.put(SpeedLimiterConfig.WRITE_SPEED.fullKey(), SpeedLimiterConfig.WRITE_SPEED);
        speedLimiterMap.put(SpeedLimiterConfig.SAMPLING_INTERVAL.fullKey(), SpeedLimiterConfig.SAMPLING_INTERVAL);
        return speedLimiterMap;
    }

    private Map<String, Object> getEngineConfigMap() {
        Map<String, Object> engineConfigMap = new LinkedHashMap<>();
        engineConfigMap.put(EngineConfig.ENGINE_TYPE.fullKey(), getDictApi(EngineConfig.ENGINE_TYPE.key()));
        engineConfigMap.put(EngineConfig.ENGINE_MODE.fullKey(), getDictApi(EngineConfig.ENGINE_MODE.key()));
        engineConfigMap.put(EngineConfig.ENGINE_CONFIGS.fullKey(), getTemplateApi(EngineConfig.ENGINE_CONFIGS.key()));
        return engineConfigMap;
    }


    public static void main(String[] args) {
        CoreConfigTemplateVo coreConfigTemplateVo = new CoreConfigTemplateVo();
        String jsonString = JSON.toJSONString(coreConfigTemplateVo);
        System.out.println(jsonString);
    }
}
