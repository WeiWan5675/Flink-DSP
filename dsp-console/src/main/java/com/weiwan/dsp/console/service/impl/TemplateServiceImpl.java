package com.weiwan.dsp.console.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.weiwan.dsp.api.config.core.*;
import com.weiwan.dsp.api.enums.EngineMode;
import com.weiwan.dsp.api.enums.UnresolvedHandlerType;
import com.weiwan.dsp.common.config.ConfigOption;
import com.weiwan.dsp.console.model.vo.CoreConfigTemplateVo;
import com.weiwan.dsp.console.service.TemplateService;
import org.springframework.stereotype.Service;
import com.weiwan.dsp.core.resolve.http.HttpUnresolvedConfig;
import com.weiwan.dsp.core.resolve.jdbc.JdbcUnresolvedConfig;
import com.weiwan.dsp.core.resolve.kafka.KafkaUnresolvedConfig;
import com.weiwan.dsp.core.resolve.logging.LogUnresolvedConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author: xiaozhennan
 * @description:
 */
@Service
public class TemplateServiceImpl implements TemplateService {
    @Override
    public JSONObject getFlinkEngineTemplate(EngineMode engineMode) {
        JSONObject jsonObject = new JSONObject(true);
        List<ConfigOption> options = new ArrayList<ConfigOption>();
        switch (engineMode) {
            case FLINK_ON_YARN_PER:
                new FlinkOnYarnPerConfigs(new HashMap<>()).loadOptions(options);
                break;
            case FLINK_ON_YARN_SESSION:
                new FlinkOnYarnSessionConfigs(new HashMap<>()).loadOptions(options);
                break;
            case FLINK_ON_YARN_APPLICATION:
                new FlinkOnYarnApplicationConfigs(new HashMap<>()).loadOptions(options);
                break;
            case FLINK_ON_K8S_APPLICATION:
                break;
            case FLINK_ON_K8S_SESSION:
                break;
            case FLINK_STANDALONE:
                new FlinkStandaloneConfigs(new Configs()).loadOptions(options);
                break;
            case SPARK_ON_YARN_PER:
                break;
            case SPARK_ON_YARN_SESSION:
                break;
            case SPARK_ON_K8S:
                break;
            case LOCAL:
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + engineMode);
        }
        for (ConfigOption option : options) {
            jsonObject.put(option.key(), option);
        }
        return jsonObject;
    }

    @Override
    public JSONObject getUnresolvedCollectorTemplate(UnresolvedHandlerType unresolvedHandlerType) {
        JSONObject jsonObject = new JSONObject(true);
        List<ConfigOption> configOptions = new ArrayList<>();
        switch (unresolvedHandlerType) {
            case LOG_HANDLER:
                new LogUnresolvedConfig().loadOptions(configOptions);
                break;
            case HTTP_HANDLER:
                new HttpUnresolvedConfig().loadOptions(configOptions);
                break;
            case JDBC_HANDLER:
                new JdbcUnresolvedConfig().loadOptions(configOptions);
                break;
            case KAFKA_HANDLER:
                new KafkaUnresolvedConfig().loadOptions(configOptions);
                break;
        }
        for (ConfigOption configOption : configOptions) {
            jsonObject.put(configOption.key(), configOption);
        }
        return jsonObject;
    }

    @Override
    public CoreConfigTemplateVo getCoreConfigTemplate(Integer appId) {
        CoreConfigTemplateVo coreConfigTemplateVo = new CoreConfigTemplateVo();
        return coreConfigTemplateVo;
    }
}
