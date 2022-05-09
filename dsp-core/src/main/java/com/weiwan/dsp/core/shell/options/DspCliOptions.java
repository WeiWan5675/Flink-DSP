package com.weiwan.dsp.core.shell.options;

import com.weiwan.dsp.core.shell.DspOptionParser;
import org.apache.commons.cli.CommandLine;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/29 18:14
 * @description:
 */
public class DspCliOptions extends DspOptions{

    public DspCliOptions(CommandLine commandLine, String[] dspArgs, String[] otherArgs) {
        super(commandLine, dspArgs, otherArgs);
    }


    public String getJobFile(){
        return this.getOption(DspOptionParser.JOB_CONF_OPTION);
    }

    public String getJobId(){
        return this.getOption(DspOptionParser.JOB_ID_OPTION);
    }

    public String getFlinkHome() {
        return this.getOption(DspOptionParser.FLINK_HOME_OPTION);
    }

    public String getHadoopHome() {
        return this.getOption(DspOptionParser.HADOOP_HOME_OPTION);
    }
}
