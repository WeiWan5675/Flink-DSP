package com.weiwan.dsp.api.config.core;


import com.weiwan.dsp.api.enums.FlinkCheckpointMode;
import com.weiwan.dsp.api.enums.ResolveOrder;
import com.weiwan.dsp.common.config.ConfigOption;
import com.weiwan.dsp.common.config.ConfigOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: xiaozhennan
 * @description:
 */
public class FlinkConfigs extends Configs {

    public static final ConfigOption<ResolveOrder> RESOLVE_ORDER = ConfigOptions
            .key("classloader.resolve-order")
            .fullKey("dsp.core.engineConfig.classloader.resolve-order")
            .defaultValue(ResolveOrder.PARENT_FIRST)
            .optionals(ResolveOrder.values())
            .description("Flink运行时的类加载模式")
            .ok(ResolveOrder.class);

    public static final ConfigOption<String> JOB_ID = ConfigOptions.key("$internal.pipeline.job-id")
            .fullKey("dsp.core.engineConfig.engineConfigs.$internal.pipeline.job-id")
            .defaultValue("00000000000000000000000000000000")
            .description("Flink运行时使用的JobId")
            .ok(String.class);

    public static final ConfigOption<Boolean> CLASSLOADER_LEAKED_CHECK = ConfigOptions.key("classloader.check-leaked-classloader")
            .fullKey("dsp.core.engineConfig.engineConfigs.classloader.check-leaked-classloader")
            .defaultValue(false)
            .description("类加载器泄露检查")
            .ok(Boolean.class);

    public static final ConfigOption<Integer> JOB_PARALLELISM = ConfigOptions.key("parallelism.default")
            .fullKey("dsp.core.engineConfig.engineConfigs.parallelism.default")
            .defaultValue(1)
            .description("默认的作业并行度")
            .ok(Integer.class);

    public static final ConfigOption<String> JOB_MAIN_CLASS = ConfigOptions.key("$internal.application.main")
            .fullKey("dsp.core.engineConfig.engineConfigs.$internal.application.main")
            .defaultValue("com.weiwan.dsp.runtime.app.DspRuntimeApp")
            .description("作业的运行的主类名称")
            .ok(String.class);

    public static final ConfigOption<String> JOB_DEPLOY_TARGET = ConfigOptions.key("execution.target")
            .fullKey("dsp.core.engineConfig.engineConfigs.execution.target")
            .defaultValue("yarn-per-job")
            .description("作业部署运行的模式")
            .ok(String.class);

    public static final ConfigOption<Boolean> JOB_DEPLOY_ATTACHED_MODE = ConfigOptions.key("execution.attached")
            .fullKey("dsp.core.engineConfig.engineConfigs.execution.attached")
            .defaultValue(false)
            .description("是否以附加模式运行任务")
            .ok(Boolean.class);



    public static final ConfigOption<Long> JOB_CHECKPOINT_INTERVAL = ConfigOptions.key("execution.checkpointing.interval")
            .fullKey("dsp.core.engineConfig.engineConfigs.execution.checkpointing.interval")
            .defaultValue(5000L)
            .description("Flink进行Checkpoint的间隔时间（毫秒）")
            .ok(Long.class);


    public static final ConfigOption<Long> JOB_CHECKPOINT_TIMEOUT = ConfigOptions.key("execution.checkpointing.timeout")
            .fullKey("dsp.core.engineConfig.engineConfigs.execution.checkpointing.timeout")
            .defaultValue(30000L)
            .description("Flink进行Checkpoint的超时时间（毫秒）")
            .ok(Long.class);


    public static final ConfigOption<FlinkCheckpointMode> JOB_CHECKPOINT_MODE = ConfigOptions.key("execution.checkpointing.mode")
            .fullKey("dsp.core.engineConfig.engineConfigs.execution.checkpointing.mode")
            .defaultValue(FlinkCheckpointMode.EXACTLY_ONCE)
            .description("Flink进行Checkpoint的模式")
            .optionals(FlinkCheckpointMode.values())
            .ok(FlinkCheckpointMode.class);


    public static final ConfigOption<Boolean> JOB_CHECKPOINT_ENABLE = ConfigOptions.key("execution.checkpointing.enable")
            .fullKey("dsp.core.engineConfig.engineConfigs.execution.checkpointing.enable")
            .defaultValue(false)
            .description("是否开启Flink的Checkpoint")
            .ok(Boolean.class);


    public static final ConfigOption<String> JOB_SAVEPOINT_PATH = ConfigOptions.key("execution.savepoint.path")
            .fullKey("dsp.core.engineConfig.engineConfigs.execution.savepoint.path")
            .defaultValue("")
            .description("已经存在的Savepoint路径")
            .ok(String.class);


    public static final ConfigOption<Boolean> JOB_SAVEPOINT_IGNORE_UNCLAIMED = ConfigOptions.key("execution.savepoint.ignore-unclaimed-state")
            .fullKey("dsp.core.engineConfig.engineConfigs.execution.savepoint.ignore-unclaimed-state")
            .defaultValue(true)
            .description("是否忽略无法恢复的保存点")
            .ok(Boolean.class);


    @Override
    public void loadOptions(List<ConfigOption> options) {
        options.add(JOB_PARALLELISM);
        options.add(RESOLVE_ORDER);
        options.add(JOB_CHECKPOINT_ENABLE);
        options.add(JOB_CHECKPOINT_MODE);
        options.add(JOB_CHECKPOINT_INTERVAL);
        options.add(JOB_CHECKPOINT_TIMEOUT);
        options.add(JOB_SAVEPOINT_PATH);
        options.add(JOB_SAVEPOINT_IGNORE_UNCLAIMED);
    }

    public FlinkConfigs(Map<String, Object> configs) {
        super(configs);
    }

    public ResolveOrder getResolveOrder() {
        return this.getVal(RESOLVE_ORDER);
    }


    public boolean isEnableCheckpoint() {
        return this.getVal(JOB_CHECKPOINT_ENABLE);
    }

    public Long getCheckpointInterval() {
        return this.getVal(JOB_CHECKPOINT_INTERVAL);
    }

    public Long getCheckpointTimeout() {
        return this.getVal(JOB_CHECKPOINT_TIMEOUT);
    }

    public FlinkCheckpointMode getCheckpointMode() {
        return this.getVal(JOB_CHECKPOINT_MODE);
    }

    public boolean isEnableIgnoreSavepointState() {
        return this.getVal(JOB_SAVEPOINT_IGNORE_UNCLAIMED);
    }

    public String getSavepointPath() {
        return this.getVal(JOB_SAVEPOINT_PATH);
    }

    public Integer getJobParallelism() {
        return this.getVal(JOB_PARALLELISM);
    }


}
