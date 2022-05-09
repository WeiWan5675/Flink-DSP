package com.weiwan.dsp.client.command;

import com.weiwan.dsp.api.config.core.DspConfig;
import com.weiwan.dsp.api.enums.EngineMode;
import com.weiwan.dsp.api.enums.EngineType;
import com.weiwan.dsp.api.enums.RunCmd;
import com.weiwan.dsp.core.shell.options.DspCliOptions;
import com.weiwan.dsp.common.utils.CheckTool;
import com.weiwan.dsp.core.shell.DspOptionParser;
import com.weiwan.dsp.core.shell.FlinkOptionParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.flink.client.cli.CliArgsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/30 14:50
 * @description:
 */
public class FlinkCommander extends ExecuteCommander {

    private static final Logger logger = LoggerFactory.getLogger(FlinkCommander.class);
    private CommandLine flinkCommandLine;
    private Options flinkOptions;

    public FlinkCommander(DspCliOptions option, DspConfig dspConfig) {
        super(option, dspConfig);
    }

    /**
     * 初始化deployer|环境信息等
     *
     * @param runCmd
     */
    public void initCMD(RunCmd runCmd) throws Exception {
        this.cmd = runCmd;
        parameterCheck(cliOptions);
        switch (runCmd) {
            case RUN:
                //rua的话, 就需要检查命令的合法性,已经run支持的参数
                parsingRunParameters();
                break;
            case INFO:
            case STOP:
            case CANCEL:
                //必须要指定jobId
                parsingAppointIdParameters();
                break;
            case LIST:
                parsingListParameters();
                //不需要做额外的初始化工作
                break;
            default:
                CheckTool.throwUnknownParameterException(String.format("Incorrect run command: %s please check!", runCmd.name()));
        }
    }

    private void parameterCheck(DspCliOptions option) {
        emptyParameterCheck(option);
        illegalParameterCheck(option);
    }

    public void parsingListParameters() throws Exception {
        Options listCommandOptions = FlinkOptionParser.getListCommandOptions();
        this.flinkOptions = listCommandOptions;
        parsingCommandLineOptions(listCommandOptions);
    }

    public void parsingAppointIdParameters() throws Exception {
        Options infoCommandOptions = FlinkOptionParser.getInfoCommandOptions();
        Options cancelCommandOptions = FlinkOptionParser.getCancelCommandOptions();
        Options stopCommandOptions = FlinkOptionParser.getStopCommandOptions();
        Options options = FlinkOptionParser.mergeOptions(cancelCommandOptions, infoCommandOptions);
        this.flinkOptions = FlinkOptionParser.mergeOptions(options, stopCommandOptions);
        parsingCommandLineOptions(options);
    }

    private void parsingCommandLineOptions(Options flinkOptions) throws CliArgsException {
        try {
            flinkCommandLine = FlinkOptionParser.parse(flinkOptions, otherArgs, false);
        } catch (CliArgsException e) {
            logger.error("解析命令行参数失败, 当前命令: {}, 原始参数: {}", cmd, otherArgs, e);
            DspOptionParser.printHelp();
            throw e;
        }
    }

    public void parsingRunParameters() throws Exception {
        //这里就已经要把默认的配置加载了.
        //这里还要把可以转换为flink的启动参数转换为启动参数
        //jobfile里边的参数 也要转换
        this.flinkOptions = FlinkOptionParser.getRunCommandOptions();
        parsingCommandLineOptions(flinkOptions);
    }

    public void emptyParameterCheck(DspCliOptions cliOption) {

    }

    public void illegalParameterCheck(DspCliOptions cliOption) {

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

    public Options getFlinkOptions() {
        return flinkOptions;
    }

    public void setFlinkOptions(Options flinkOptions) {
        this.flinkOptions = flinkOptions;
    }

    public CommandLine getFlinkCommandLine() {
        return flinkCommandLine;
    }

    public void setFlinkCommandLine(CommandLine flinkCommandLine) {
        this.flinkCommandLine = flinkCommandLine;
    }
}
