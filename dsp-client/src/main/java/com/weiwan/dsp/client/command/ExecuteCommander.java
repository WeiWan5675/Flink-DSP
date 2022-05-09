package com.weiwan.dsp.client.command;

import com.weiwan.dsp.api.config.core.CoreConfig;
import com.weiwan.dsp.api.config.core.DspConfig;
import com.weiwan.dsp.api.config.core.EngineConfig;
import com.weiwan.dsp.api.enums.EngineMode;
import com.weiwan.dsp.api.enums.EngineType;
import com.weiwan.dsp.api.enums.RunCmd;
import com.weiwan.dsp.core.shell.options.DspCliOptions;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/30 14:50
 * @description:
 */
public abstract class ExecuteCommander {

    protected final DspCliOptions cliOptions;
    protected final DspConfig dspConfig;
    protected final String[] originalArgs;
    protected final String[] otherArgs;
    protected final EngineConfig engineConfig;
    protected final EngineType engineType;
    protected final EngineMode engineMode;
    protected final CoreConfig coreConfig;

    protected RunCmd cmd;

    public ExecuteCommander(DspCliOptions cliOptions, DspConfig dspConfig) {
        this.cliOptions = cliOptions;
        this.originalArgs = cliOptions.getOriginalArgs();
        this.otherArgs = cliOptions.getOtherArgs();
        this.dspConfig = dspConfig;
        this.coreConfig = dspConfig.getCore();
        this.engineConfig = dspConfig.getCore().getEngineConfig();
        this.engineMode = engineConfig.getEngineMode();
        this.engineType = engineConfig.getEngineType();
    }

    public static ExecuteCommander createCommander(RunCmd runCmd, DspCliOptions option, DspConfig finalDspConfig) throws Exception {
        CoreConfig core = finalDspConfig.getCore();
        EngineConfig engineConfig = core.getEngineConfig();
        EngineType engineType = engineConfig.getEngineType();
        ExecuteCommander commander = null;
        switch (engineType) {
            case FLINK:
                commander = new FlinkCommander(option, finalDspConfig);
                break;
            case SPARK:
                commander = new SparkCommander(option, finalDspConfig);
                break;
            case LOCAL:
                commander = new LocalCommander(option, finalDspConfig);
                break;
            default:
                throw new RuntimeException("Unknown engine mode, Please check the configuration file");
        }
        if (commander != null) {
            commander.initCMD(runCmd);
        }
        return commander;
    }

    public abstract void initCMD(RunCmd runCmd) throws Exception;


    public DspCliOptions getCliOptions() {
        return cliOptions;
    }

    public DspConfig getJobConfig() {
        return dspConfig;
    }

    public String[] getOriginalArgs() {
        return originalArgs;
    }


    public EngineMode getEngineMode() {
        return this.engineMode;
    }

    public EngineType getEngineType() {
        return this.engineType;
    }

    public RunCmd getRunCmd() {
        return this.cmd;
    }

}
