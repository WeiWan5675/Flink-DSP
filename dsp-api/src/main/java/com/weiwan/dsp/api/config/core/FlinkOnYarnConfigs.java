package com.weiwan.dsp.api.config.core;

import com.weiwan.dsp.common.config.ConfigOption;
import com.weiwan.dsp.common.config.ConfigOptions;

import java.util.List;
import java.util.Map;

/**
 * @Author: xiaozhennan
 * @Date: 2021/10/15 19:01
 * @Package: com.weiwan.dsp.api.config.core
 * @ClassName: FlinkOnYarnConfigs
 * @Description: OnYarn配置项
 **/
public class FlinkOnYarnConfigs extends FlinkConfigs {

    public static final ConfigOption<String> YARN_QUEUE = ConfigOptions.key("yarn.application.queue")
            .fullKey("dsp.core.engineConfig.engineConfigs.yarn.application.queue")
            .defaultValue("root")
            .description("Flink运行在Yarn上时使用的资源队列")
            .ok(String.class);


    public static final ConfigOption<String> ENV_YARN_CONFIG_DIR = ConfigOptions.key("env.yarn.conf.dir")
            .fullKey("dsp.core.engineConfig.engineConfigs.env.yarn.conf.dir")
            .defaultValue("")
            .description("默认的YarnConfDir, 如果没用配置会从环境变量中获取")
            .ok(String.class);


    public static final ConfigOption<String> ENV_HADOOP_CONFIG_DIR = ConfigOptions.key("env.hadoop.conf.dir")
            .fullKey("dsp.core.engineConfig.engineConfigs.env.hadoop.conf.dir")
            .defaultValue("")
            .description("默认的HadoopConfDir, 如果没用配置会从环境变量中获取")
            .ok(String.class);


    @Override
    public void loadOptions(List<ConfigOption> options) {
        super.loadOptions(options);
        options.add(YARN_QUEUE);
    }


    public FlinkOnYarnConfigs(Map<String, Object> configs) {
        super(configs);
    }


    public String getYarnQueue() {
        return this.getVal(YARN_QUEUE);
    }

    public String getEnvYarnConfigDir() {
        return this.getVal(ENV_YARN_CONFIG_DIR);
    }

    public String getEnvHadoopConfigDir() {
        return this.getVal(ENV_HADOOP_CONFIG_DIR);
    }
}
