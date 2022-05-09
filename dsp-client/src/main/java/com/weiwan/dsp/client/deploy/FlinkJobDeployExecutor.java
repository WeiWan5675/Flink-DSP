package com.weiwan.dsp.client.deploy;

import com.weiwan.dsp.api.config.core.*;
import com.weiwan.dsp.api.enums.FlinkCheckpointMode;
import com.weiwan.dsp.api.enums.ResolveOrder;
import com.weiwan.dsp.core.deploy.JobDeployExecutor;
import com.weiwan.dsp.core.pub.SystemEnvManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.client.deployment.application.ApplicationConfiguration;
import org.apache.flink.configuration.*;
import org.apache.flink.runtime.jobgraph.SavepointConfigOptions;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.ExecutionCheckpointingOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/30 14:51
 * @description:
 */
public abstract class FlinkJobDeployExecutor extends JobDeployExecutor {
    private Logger _logger = LoggerFactory.getLogger(FlinkJobDeployExecutor.class);

    private Configuration configuration;
    private FlinkConfigs flinkConfigs;

    public FlinkJobDeployExecutor(DspConfig dspConfig) {
        super(dspConfig);
    }

    @Override
    public void init() throws Exception {
        //1. 加载默认的配置文件
        //2. 处理EngineConfig里的keyv
        this.configuration = GlobalConfiguration.loadConfiguration(SystemEnvManager.getInstance().getFlinkConfDir());
        this.flinkConfigs = new FlinkConfigs(engineConfig.getEngineConfigs());
    }

    @Override
    public void close() throws Exception {

    }


    public Configuration getFlinkConfiguration() {
        return configuration;
    }

    public void setFlinkConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    protected void checkpointSetting(Configuration flinkConfiguration, FlinkConfigs configs) {
        if (configs.isEnableCheckpoint()) {
            //checkpoint间隔
            flinkConfiguration.set(ExecutionCheckpointingOptions.CHECKPOINTING_INTERVAL,
                    Duration.ofMillis(configs.getCheckpointInterval()));
            //checkpoint超时时间
            flinkConfiguration.set(ExecutionCheckpointingOptions.CHECKPOINTING_TIMEOUT,
                    Duration.ofMillis(configs.getCheckpointTimeout()));
            //checkpoint模式
            FlinkCheckpointMode checkpointMode = configs.getCheckpointMode();
            flinkConfiguration.set(ExecutionCheckpointingOptions.CHECKPOINTING_MODE,
                    CheckpointingMode.valueOf(checkpointMode.name()));

        } else {
            configs.remove(ExecutionCheckpointingOptions.CHECKPOINTING_INTERVAL.key());
            configs.remove(ExecutionCheckpointingOptions.CHECKPOINTING_TIMEOUT.key());
            configs.remove(ExecutionCheckpointingOptions.CHECKPOINTING_MODE.key());
            configs.remove(ExecutionCheckpointingOptions.MAX_CONCURRENT_CHECKPOINTS.key());
            configs.remove(ExecutionCheckpointingOptions.MIN_PAUSE_BETWEEN_CHECKPOINTS.key());
            configs.remove(ExecutionCheckpointingOptions.TOLERABLE_FAILURE_NUMBER.key());
            configs.remove(ExecutionCheckpointingOptions.EXTERNALIZED_CHECKPOINT.key());
            configs.remove(ExecutionCheckpointingOptions.ENABLE_UNALIGNED.key());
            configs.remove(ExecutionCheckpointingOptions.ALIGNED_CHECKPOINT_TIMEOUT.key());
            configs.remove(ExecutionCheckpointingOptions.ALIGNMENT_TIMEOUT.key());
            configs.remove(ExecutionCheckpointingOptions.FORCE_UNALIGNED.key());
            configs.remove(ExecutionCheckpointingOptions.CHECKPOINT_ID_OF_IGNORED_IN_FLIGHT_DATA.key());
            configs.remove(ExecutionCheckpointingOptions.ENABLE_CHECKPOINTS_AFTER_TASKS_FINISH.key());

        }
    }

    protected SavepointRestoreSettings savepointSetting(Configuration flinkConfiguration, FlinkConfigs configs) {
        if (StringUtils.isNotBlank(configs.getSavepointPath())) {
            flinkConfiguration.set(SavepointConfigOptions.SAVEPOINT_IGNORE_UNCLAIMED_STATE, configs.isEnableIgnoreSavepointState());
            flinkConfiguration.set(SavepointConfigOptions.SAVEPOINT_PATH, configs.getSavepointPath());
            return SavepointRestoreSettings.forPath(configs.getSavepointPath(), configs.isEnableIgnoreSavepointState());
        } else {
            configs.remove(SavepointConfigOptions.SAVEPOINT_IGNORE_UNCLAIMED_STATE.key());
            configs.remove(SavepointConfigOptions.SAVEPOINT_PATH.key());
            return SavepointRestoreSettings.none();
        }
    }


    protected void internalSetting(Configuration flinkConfiguration, final FlinkConfigs configs) {
        flinkConfiguration.set(PipelineOptions.NAME, jobName);
        ResolveOrder resolveOrder = configs.getResolveOrder();
        flinkConfiguration.set(CoreOptions.CLASSLOADER_RESOLVE_ORDER, resolveOrder.getType());
        flinkConfiguration.set(PipelineOptionsInternal.PIPELINE_FIXED_JOB_ID, jobId);
        flinkConfiguration.set(CoreOptions.CHECK_LEAKED_CLASSLOADER, false);
        flinkConfiguration.set(CoreOptions.DEFAULT_PARALLELISM, configs.getJobParallelism());
        flinkConfiguration.set(ApplicationConfiguration.APPLICATION_MAIN_CLASS, JobDeployExecutor.DSP_RUNTIME_MAIN_CLASS);
        flinkConfiguration.set(DeploymentOptions.ATTACHED, false);

        Set<String> flinkKeys = flinkConfiguration.keySet();
        for (String cKey : flinkKeys) {
            configs.remove(cKey);
        }
    }


}
