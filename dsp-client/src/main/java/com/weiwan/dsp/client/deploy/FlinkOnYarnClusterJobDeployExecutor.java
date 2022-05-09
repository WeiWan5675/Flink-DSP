package com.weiwan.dsp.client.deploy;

import com.weiwan.dsp.api.config.core.Configs;
import com.weiwan.dsp.api.config.core.DspConfig;
import com.weiwan.dsp.api.config.core.FlinkOnYarnClusterConfigs;
import com.weiwan.dsp.api.config.core.FlinkOnYarnPerConfigs;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.*;
import org.apache.flink.yarn.Utils;
import org.apache.hadoop.yarn.conf.YarnConfiguration;

/**
 * @Author: xiaozhennan
 * @Date: 2021/10/25 18:37
 * @ClassName: FlinkOnYarnClusterJobDeployExecutor
 * @Description: FlinkOnYarn集群
 **/
public abstract class FlinkOnYarnClusterJobDeployExecutor extends FlinkJobDeployExecutor {

    private YarnConfiguration yarnConfiguration;
    private FlinkOnYarnClusterConfigs flinkOnYarnClusterConfigs;

    public FlinkOnYarnClusterJobDeployExecutor(DspConfig dspConfig) {
        super(dspConfig);
    }


    @Override
    public void init() throws Exception {
        super.init();
        this.flinkOnYarnClusterConfigs = new FlinkOnYarnClusterConfigs(getEngineConfigs());
        hadoopSetting(getFlinkConfiguration(), flinkOnYarnClusterConfigs);
        yarnSetting(getFlinkConfiguration(), flinkOnYarnClusterConfigs);
        this.yarnConfiguration = Utils.getYarnAndHadoopConfiguration(getFlinkConfiguration());
    }

    protected void flinkClusterSetting(Configuration flinkConfiguration, FlinkOnYarnClusterConfigs configs) {
        //开启火焰图
        flinkConfiguration.set(RestOptions.ENABLE_FLAMEGRAPH, configs.isEnableFlameGraph());
        //jobManagerJVM参数
        if (StringUtils.isNotBlank(configs.getJobManagerJvmOpts())) {
            flinkConfiguration.set(CoreOptions.FLINK_JM_JVM_OPTIONS, configs.getJobManagerJvmOpts());
        }
        //taskManagerJVM参数
        if (StringUtils.isNotBlank(configs.getTaskManagerJvmOpts())) {
            flinkConfiguration.set(CoreOptions.FLINK_TM_JVM_OPTIONS, configs.getTaskManagerJvmOpts());
        }
        //jobManagerJvm总内存
        if (StringUtils.isNotBlank(configs.getJobManagerMemorySize())) {
            flinkConfiguration.set(JobManagerOptions.TOTAL_PROCESS_MEMORY,
                    MemorySize.parse(configs.getJobManagerMemorySize(), MemorySize.MemoryUnit.MEGA_BYTES));
        }
        //taskManagerJvm总内存
        if (StringUtils.isNotBlank(configs.getTaskManagerMemorySize())) {
            flinkConfiguration.set(TaskManagerOptions.TOTAL_PROCESS_MEMORY,
                    MemorySize.parse(configs.getTaskManagerMemorySize(), MemorySize.MemoryUnit.MEGA_BYTES));
        }

        flinkConfiguration.set(TaskManagerOptions.NUM_TASK_SLOTS, configs.getTaskNumTaskSlots());
        flinkConfiguration.set(TaskManagerOptions.CPU_CORES, configs.getTaskCpuCores());


    }

    protected void hadoopSetting(Configuration flinkConfiguration, FlinkOnYarnClusterConfigs configs) {
        String envHadoopConfigDir = configs.getEnvHadoopConfigDir();
        if (StringUtils.isNotBlank(envHadoopConfigDir)) {
            flinkConfiguration.set(CoreOptions.FLINK_HADOOP_CONF_DIR, envHadoopConfigDir);
            System.setProperty(FlinkOnYarnClusterConfigs.ENV_HADOOP_CONFIG_DIR.key(), envHadoopConfigDir);
        }
    }

    protected void yarnSetting(Configuration flinkConfiguration, FlinkOnYarnClusterConfigs configs) {
        String envHadoopConfigDir = configs.getEnvHadoopConfigDir();
        if (StringUtils.isNotBlank(envHadoopConfigDir)) {
            flinkConfiguration.set(CoreOptions.FLINK_YARN_CONF_DIR, envHadoopConfigDir);
            System.setProperty(FlinkOnYarnClusterConfigs.ENV_HADOOP_CONFIG_DIR.key(), envHadoopConfigDir);
        }
    }


    public YarnConfiguration getYarnConfiguration() {
        return this.yarnConfiguration;
    }
}
