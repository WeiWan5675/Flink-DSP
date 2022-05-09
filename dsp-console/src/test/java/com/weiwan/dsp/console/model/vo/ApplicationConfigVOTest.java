package com.weiwan.dsp.console.model.vo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.weiwan.dsp.common.utils.ObjectUtil;
import junit.framework.TestCase;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @Author: xiaozhennan
 * @Date: 2021/11/9 23:06
 * @ClassName: ApplicationConfigVOTest
 * @Description:
 **/
public class ApplicationConfigVOTest extends TestCase {


    @Test
    public void testVO() throws JsonProcessingException {

        String p = "{\n" +
                "    \"jobId\": \"\",\n" +
                "    \"appName\": \"Test\",\n" +
                "    \"appType\": null,\n" +
                "    \"deployVo\": {},\n" +
                "    \"configVo\": {\n" +
                "        \"flowId\": 12,\n" +
                "        \"flowName\": \"Test1234352\",\n" +
                "        \"core\": {\n" +
                "            \"engineConfig\": {\n" +
                "                \"engineType\": {\n" +
                "                    \"engineClass\": \"com.weiwan.dsp.core.engine.flink.FlinkJobFlowEngine\",\n" +
                "                    \"code\": 1,\n" +
                "                    \"name\": \"FLINK\"\n" +
                "                },\n" +
                "                \"engineMode\": {\n" +
                "                    \"mode\": \"flink_standalone\",\n" +
                "                    \"code\": 6\n" +
                "                },\n" +
                "                \"engineConfigs\": {\n" +
                "                    \"dsp.core.engineConfig.engineConfigs.parallelism.default\": {\n" +
                "                        \"fullKey\": \"dsp.core.engineConfig.engineConfigs.parallelism.default\",\n" +
                "                        \"key\": \"parallelism.default\",\n" +
                "                        \"value\": null,\n" +
                "                        \"defaultValue\": 1,\n" +
                "                        \"optionals\": null,\n" +
                "                        \"required\": false,\n" +
                "                        \"description\": \"默认的作业并行度\",\n" +
                "                        \"type\": \"java.lang.Integer\",\n" +
                "                        \"objectDef\": null,\n" +
                "                        \"objects\": null,\n" +
                "                        \"valueFormat\": null\n" +
                "                    },\n" +
                "                    \"dsp.core.engineConfig.classloader.resolve-order\": {\n" +
                "                        \"fullKey\": \"dsp.core.engineConfig.classloader.resolve-order\",\n" +
                "                        \"key\": \"classloader.resolve-order\",\n" +
                "                        \"value\": null,\n" +
                "                        \"defaultValue\": {\n" +
                "                            \"mode\": \"parent-first\",\n" +
                "                            \"code\": 0\n" +
                "                        },\n" +
                "                        \"optionals\": [\n" +
                "                            {\n" +
                "                                \"mode\": \"parent-first\",\n" +
                "                                \"code\": 0\n" +
                "                            },\n" +
                "                            {\n" +
                "                                \"mode\": \"child-first\",\n" +
                "                                \"code\": 1\n" +
                "                            }\n" +
                "                        ],\n" +
                "                        \"required\": false,\n" +
                "                        \"description\": \"Flink运行时的类加载模式\",\n" +
                "                        \"type\": \"com.weiwan.dsp.api.enums.ResolveOrder\",\n" +
                "                        \"objectDef\": null,\n" +
                "                        \"objects\": null,\n" +
                "                        \"valueFormat\": null\n" +
                "                    },\n" +
                "                    \"dsp.core.engineConfig.engineConfigs.execution.checkpointing.enable\": {\n" +
                "                        \"fullKey\": \"dsp.core.engineConfig.engineConfigs.execution.checkpointing.enable\",\n" +
                "                        \"key\": \"execution.checkpointing.enable\",\n" +
                "                        \"value\": null,\n" +
                "                        \"defaultValue\": false,\n" +
                "                        \"optionals\": null,\n" +
                "                        \"required\": false,\n" +
                "                        \"description\": \"是否开启Flink的Checkpoint\",\n" +
                "                        \"type\": \"java.lang.Boolean\",\n" +
                "                        \"objectDef\": null,\n" +
                "                        \"objects\": null,\n" +
                "                        \"valueFormat\": null\n" +
                "                    },\n" +
                "                    \"dsp.core.engineConfig.engineConfigs.execution.checkpointing.mode\": {\n" +
                "                        \"fullKey\": \"dsp.core.engineConfig.engineConfigs.execution.checkpointing.mode\",\n" +
                "                        \"key\": \"execution.checkpointing.mode\",\n" +
                "                        \"value\": null,\n" +
                "                        \"defaultValue\": {\n" +
                "                            \"code\": 2,\n" +
                "                            \"mode\": \"exactly_once\"\n" +
                "                        },\n" +
                "                        \"optionals\": [\n" +
                "                            {\n" +
                "                                \"code\": 1,\n" +
                "                                \"mode\": \"at_least_once\"\n" +
                "                            },\n" +
                "                            {\n" +
                "                                \"code\": 2,\n" +
                "                                \"mode\": \"exactly_once\"\n" +
                "                            }\n" +
                "                        ],\n" +
                "                        \"required\": false,\n" +
                "                        \"description\": \"Flink进行Checkpoint的模式\",\n" +
                "                        \"type\": \"com.weiwan.dsp.api.enums.FlinkCheckpointMode\",\n" +
                "                        \"objectDef\": null,\n" +
                "                        \"objects\": null,\n" +
                "                        \"valueFormat\": null\n" +
                "                    },\n" +
                "                    \"dsp.core.engineConfig.engineConfigs.execution.checkpointing.interval\": {\n" +
                "                        \"fullKey\": \"dsp.core.engineConfig.engineConfigs.execution.checkpointing.interval\",\n" +
                "                        \"key\": \"execution.checkpointing.interval\",\n" +
                "                        \"value\": null,\n" +
                "                        \"defaultValue\": 5000,\n" +
                "                        \"optionals\": null,\n" +
                "                        \"required\": false,\n" +
                "                        \"description\": \"Flink进行Checkpoint的间隔时间（毫秒）\",\n" +
                "                        \"type\": \"java.lang.Long\",\n" +
                "                        \"objectDef\": null,\n" +
                "                        \"objects\": null,\n" +
                "                        \"valueFormat\": null\n" +
                "                    },\n" +
                "                    \"dsp.core.engineConfig.engineConfigs.execution.checkpointing.timeout\": {\n" +
                "                        \"fullKey\": \"dsp.core.engineConfig.engineConfigs.execution.checkpointing.timeout\",\n" +
                "                        \"key\": \"execution.checkpointing.timeout\",\n" +
                "                        \"value\": null,\n" +
                "                        \"defaultValue\": 30000,\n" +
                "                        \"optionals\": null,\n" +
                "                        \"required\": false,\n" +
                "                        \"description\": \"Flink进行Checkpoint的超时时间（毫秒）\",\n" +
                "                        \"type\": \"java.lang.Long\",\n" +
                "                        \"objectDef\": null,\n" +
                "                        \"objects\": null,\n" +
                "                        \"valueFormat\": null\n" +
                "                    },\n" +
                "                    \"dsp.core.engineConfig.engineConfigs.execution.savepoint.path\": {\n" +
                "                        \"fullKey\": \"dsp.core.engineConfig.engineConfigs.execution.savepoint.path\",\n" +
                "                        \"key\": \"execution.savepoint.path\",\n" +
                "                        \"value\": null,\n" +
                "                        \"defaultValue\": \"\",\n" +
                "                        \"optionals\": null,\n" +
                "                        \"required\": false,\n" +
                "                        \"description\": \"已经存在的Savepoint路径\",\n" +
                "                        \"type\": \"java.lang.String\",\n" +
                "                        \"objectDef\": null,\n" +
                "                        \"objects\": null,\n" +
                "                        \"valueFormat\": null\n" +
                "                    },\n" +
                "                    \"dsp.core.engineConfig.engineConfigs.execution.savepoint.ignore-unclaimed-state\": {\n" +
                "                        \"fullKey\": \"dsp.core.engineConfig.engineConfigs.execution.savepoint.ignore-unclaimed-state\",\n" +
                "                        \"key\": \"execution.savepoint.ignore-unclaimed-state\",\n" +
                "                        \"value\": null,\n" +
                "                        \"defaultValue\": true,\n" +
                "                        \"optionals\": null,\n" +
                "                        \"required\": false,\n" +
                "                        \"description\": \"是否忽略无法恢复的保存点\",\n" +
                "                        \"type\": \"java.lang.Boolean\",\n" +
                "                        \"objectDef\": null,\n" +
                "                        \"objects\": null,\n" +
                "                        \"valueFormat\": null\n" +
                "                    }\n" +
                "                }\n" +
                "            },\n" +
                "            \"metricConfig\": {\n" +
                "                \"reporter\": \"\",\n" +
                "                \"interval\": 10,\n" +
                "                \"host\": \"127.0.0.1\",\n" +
                "                \"port\": 9875\n" +
                "            },\n" +
                "            \"speedLimiter\": {\n" +
                "                \"enableSpeedLimiter\": true,\n" +
                "                \"readSpeed\": 999999,\n" +
                "                \"processSpeed\": 999999,\n" +
                "                \"writeSpeed\": 999999,\n" +
                "                \"speedSamplingInterval\": 30\n" +
                "            },\n" +
                "            \"unresolvedCollector\": {\n" +
                "                \"enableUnresolvedCollector\": true,\n" +
                "                \"collectorHandler\": \"com.weiwan.dsp.core.resolve.LogUnresolvedDataCollector\",\n" +
                "                \"maxSamplingRecord\": 1000,\n" +
                "                \"samplingInterval\": 60,\n" +
                "                \"handlerConfigs\": {\n" +
                "                    \"dsp.core.unresolved.handlerConfigs.logLevel\": {\n" +
                "                        \"fullKey\": \"dsp.core.unresolved.handlerConfigs.logLevel\",\n" +
                "                        \"key\": \"logLevel\",\n" +
                "                        \"value\": null,\n" +
                "                        \"defaultValue\": \"info\",\n" +
                "                        \"optionals\": null,\n" +
                "                        \"required\": false,\n" +
                "                        \"description\": \"日志未解析数据处理器的日志输出级别\",\n" +
                "                        \"type\": \"java.lang.String\",\n" +
                "                        \"objectDef\": null,\n" +
                "                        \"objects\": null,\n" +
                "                        \"valueFormat\": null\n" +
                "                    }\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "    },\n" +
                "    \"flowId\": 12,\n" +
                "    \"flowName\": \"Test1234352\",\n" +
                "    \"remarkMsg\": \"\"\n" +
                "}";


        ApplicationVO applicationConfigVO = ObjectUtil.contentToObject(p, ApplicationVO.class);

        System.out.println(applicationConfigVO);
    }

}