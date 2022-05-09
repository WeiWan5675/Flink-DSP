package com.weiwan.dsp.core.shell.options;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/6/1 15:07
 * @description:
 */
public class DspOptions {

    protected final String[] args;
    private final String[] otherArgs;
    protected CommandLine commandLine;


    public DspOptions(CommandLine commandLine, String[] args, String[] otherArgs) {
        this.commandLine = commandLine;
        this.args = args;
        this.otherArgs = otherArgs;
    }


    public String getOption(Option option, String defaultVar) {
        String optVar = getOption(option);
        return optVar == null ? defaultVar : optVar;
    }

    public String getOption(Option option) {
        if (commandLine.hasOption(option.getOpt())) {
            return commandLine.getOptionValue(option.getOpt());
        }
        return null;
    }

    public CommandLine getCommandLine(){
        return this.commandLine;
    }

    public String[] getOriginalArgs(){
        return this.args;
    }

    public String[] getOtherArgs(){
        return this.otherArgs;
    }

    public boolean hasOption(Option option) {
        return commandLine.hasOption(option.getOpt());
    }
}
