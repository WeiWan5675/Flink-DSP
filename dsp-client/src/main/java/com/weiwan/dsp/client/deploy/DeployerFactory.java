package com.weiwan.dsp.client.deploy;

import com.weiwan.dsp.api.config.core.DspConfig;
import com.weiwan.dsp.api.config.core.CoreConfig;
import com.weiwan.dsp.api.config.core.EngineConfig;
import com.weiwan.dsp.api.enums.EngineMode;
import com.weiwan.dsp.api.enums.EngineType;
import com.weiwan.dsp.common.exception.DspException;
import com.weiwan.dsp.core.deploy.DeployExecutor;

public class DeployerFactory {

    public static DeployExecutor createDeployer(DspConfig dspConfig) {
        if (dspConfig == null) {
            throw new RuntimeException("Cannot create deploy executor, dsp config cannot be empty");
        }
        CoreConfig core = dspConfig.getCore();
        EngineConfig engineConfig = core.getEngineConfig();
        EngineType engineType = engineConfig.getEngineType();
        EngineMode engineMode = engineConfig.getEngineMode();
        switch (engineType) {
            case FLINK:
                return createFlinkDeployer(dspConfig, engineMode);
            case SPARK:
                return createSparkDeployer(dspConfig, engineMode);
            case LOCAL:
                return createLocalDeployer(dspConfig, engineMode);
            default:
                throw DspException.generateIllegalStateException(String.format("无法找到引擎对应的Deployer, 引擎ID: %s", engineType.getEngineName()));
        }
    }

    private static DeployExecutor createLocalDeployer(DspConfig dspConfig, EngineMode engineMode) {
        return new LocalJobDeployExecutor(dspConfig);
    }

    private static DeployExecutor createSparkDeployer(DspConfig dspConfig, EngineMode engineMode) {
        return new SparkJobDeployExecutor(dspConfig);
    }


    private static DeployExecutor createFlinkDeployer(DspConfig dspConfig, EngineMode engineMode) {
        switch (engineMode) {
            case FLINK_STANDALONE:
                return new FlinkStandaloneJobDeployExecutor(dspConfig);
            case FLINK_ON_YARN_PER:
                return new FlinkYarnPerJobDeployExecutor(dspConfig);
            case FLINK_ON_YARN_SESSION:
                return new FlinkYarnSessionJobDeployExecutor(dspConfig);
            case FLINK_ON_YARN_APPLICATION:
                return new FlinkYarnApplicationJobDeployExecutor(dspConfig);
            case FLINK_ON_K8S_SESSION:
                return new FlinkK8SSessionJobDeployExecutor(dspConfig);
            case FLINK_ON_K8S_APPLICATION:
                return new FlinkK8SApplicationJobDeployExecutor(dspConfig);
            default:
                throw DspException.generateIllegalStateException("Engine Mode not yet supported");
        }
    }
}
