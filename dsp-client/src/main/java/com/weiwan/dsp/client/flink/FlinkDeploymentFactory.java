package com.weiwan.dsp.client.flink;

import com.weiwan.dsp.api.enums.EngineMode;
import com.weiwan.dsp.client.command.FlinkCommander;

/**
 * @author: xiaozhennan
 * @date: 2021/6/3 14:35
 * @description: 创建flink部署工具
 */
public class FlinkDeploymentFactory {
    public static FlinkDeploymentTool createTool(FlinkCommander commander) {
        EngineMode engineMode = commander.getEngineMode();
        switch (engineMode) {
            case FLINK_ON_YARN_PER:
                return createYarnPreJobTool(commander);
            case FLINK_ON_YARN_SESSION:
                return createYarnSessionJobTool(commander);
            case FLINK_ON_YARN_APPLICATION:
                return createYarnApplicationJobTool(commander);
            case FLINK_STANDALONE:
                return createStandaloneJobTool(commander);
            case FLINK_ON_K8S_APPLICATION:
            case FLINK_ON_K8S_SESSION:
                return createKuberneterNativeSessionJobTool(commander);
            default:
                throw new UnsupportedOperationException("Unsupported EngineMode, please check the documentation");
        }
    }

    private static FlinkDeploymentTool createKuberneterNativeSessionJobTool(FlinkCommander commander) {
        return null;
    }

    public static FlinkDeploymentTool createStandaloneJobTool(FlinkCommander commander) {
        return new StandaloneJobTool();
    }

    public static FlinkDeploymentTool createYarnApplicationJobTool(FlinkCommander commander) {
        return new YarnApplicationJobTool();
    }

    public static FlinkDeploymentTool createYarnSessionJobTool(FlinkCommander commander) {
        return new YarnSessionJobTool();
    }

    public static FlinkDeploymentTool createYarnPreJobTool(FlinkCommander commander) {
        return new YarnPreJobTool();
    }


}
