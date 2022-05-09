package com.weiwan.dsp.api.config.core;

import com.weiwan.dsp.common.config.ConfigOption;
import com.weiwan.dsp.common.config.ConfigOptions;

import java.util.Map;

/**
 * @Author: xiaozhennan
 * @Date: 2021/10/25 18:11
 * @ClassName: FlinkOnYarnClusterConfigs
 * @Description: Flink集群相关配置(yarn - application, yarn - per)
 **/
public class FlinkOnYarnClusterConfigs extends FlinkOnYarnConfigs {

    public static final ConfigOption<String> JOB_MANAGER_MEMORY_PROCESS_SIZE = ConfigOptions
            .key("jobmanager.memory.process.size")
            .fullKey("dsp.core.engineConfig.engineConfigs.jobmanager.memory.process.size")
            .defaultValue("1024m")
            .description("Flink运行时JobManager的内存大小")
            .ok(String.class);

    public static final ConfigOption<String> TASK_MANAGER_MEMORY_PROCESS_SIZE = ConfigOptions
            .key("taskmanager.memory.process.size")
            .fullKey("dsp.core.engineConfig.engineConfigs.taskmanager.memory.process.size")
            .defaultValue("1024m")
            .description("Flink运行时TaskManager的内存大小")
            .ok(String.class);


    public static final ConfigOption<String> TASK_MANAGER_JVM_ENV_OPTS = ConfigOptions.key("env.java.opts.taskmanager")
            .fullKey("dsp.core.engineConfig.engineConfigs.env.java.opts.taskmanager")
            .defaultValue("")
            .description("启动TaskManager时自定义的JVM参数")
            .ok(String.class);


    public static final ConfigOption<String> JOB_MANAGER_JVM_ENV_OPTS = ConfigOptions.key("env.java.opts.jobmanager")
            .fullKey("dsp.core.engineConfig.engineConfigs.env.java.opts.jobmanager")
            .defaultValue("")
            .description("启动JobManager时自定义的JVM参数")
            .ok(String.class);

    public static final ConfigOption<String> JOB_JVM_ENV_OPTS = ConfigOptions.key("env.java.opts")
            .fullKey("dsp.core.engineConfig.engineConfigs.env.java.opts")
            .defaultValue("")
            .description("启动Flink所有JVM进程时的自定义的JVM参数")
            .ok(String.class);


    public static final ConfigOption<Boolean> ENABLE_FLAME_GRAPH = ConfigOptions.key("rest.flamegraph.enabled")
            .fullKey("dsp.core.engineConfig.engineConfigs.rest.flamegraph.enabled")
            .defaultValue(false)
            .description("是否开启Flink火焰图功能")
            .ok(Boolean.class);


    public static final ConfigOption<Integer> TASK_NUM_TASK_SLOTS = ConfigOptions.key("taskmanager.numberOfTaskSlots")
            .fullKey("dsp.core.engineConfig.engineConfigs.taskmanager.numberOfTaskSlots")
            .defaultValue(1)
            .description("Flink集群TaskManager拥有得slot数量")
            .ok(Integer.class);

    public static final ConfigOption<Double> TASK_CPU_CORES = ConfigOptions.key("taskmanager.cpu.cores")
            .fullKey("dsp.core.engineConfig.engineConfigs.taskmanager.cpu.cores")
            .defaultValue(1.0d)
            .description("Flink集群TaskManager拥有得cpu-core数量")
            .ok(Double.class);

    public FlinkOnYarnClusterConfigs(Map<String, Object> configs) {
        super(configs);
    }


    public boolean isEnableFlameGraph() {
        return this.getVal(ENABLE_FLAME_GRAPH);
    }

    public String getTaskManagerMemorySize() {
        return this.getVal(TASK_MANAGER_MEMORY_PROCESS_SIZE);
    }

    public String getJobManagerMemorySize() {
        return this.getVal(JOB_MANAGER_MEMORY_PROCESS_SIZE);
    }

    public String getJobManagerJvmOpts() {
        return this.getVal(JOB_MANAGER_JVM_ENV_OPTS);
    }

    public String getTaskManagerJvmOpts() {
        return this.getVal(TASK_MANAGER_JVM_ENV_OPTS);
    }


    public Integer getTaskNumTaskSlots() {
        return this.getVal(TASK_NUM_TASK_SLOTS);
    }

    public Double getTaskCpuCores() {
        return this.getVal(TASK_CPU_CORES);
    }
}
