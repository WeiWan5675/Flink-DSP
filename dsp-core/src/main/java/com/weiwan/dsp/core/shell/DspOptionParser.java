package com.weiwan.dsp.core.shell;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * @Author: xiaozhennan
 * @Date: 2021/5/30 19:44
 * @Package: com.weiwan.dsp.core.runing
 * @ClassName: FlinkOptionParser
 * @Description: Flink参数解析器
 **/
public class DspOptionParser extends CommonOptionParser {


    //作业的配置文件
    public static final Option JOB_CONF_OPTION = new Option(
            "c",
            "conf",
            true,
            "Specify a definition file to describe the Flink DSP job, which can end with [. JSON |. Yaml |. Properties]");


    //作业ID
    public static final Option JOB_ID_OPTION = new Option(
            "jid",
            "jobId",
            true,
            "Use this option to specify a running job ID");


    public static final Option HADOOP_HOME_OPTION = new Option(
            "hadoopHome",
            true,
            "Specify HADOOP_HOME, if not specified, is obtained from the environment variable by default");


    public static final Option FLINK_HOME_OPTION = new Option(
            "flinkHome",
            true,
            "Specify FLINK_HOME, if not specified, is obtained from the environment variable by default");


    static {
        JOB_CONF_OPTION.setRequired(false);
        JOB_ID_OPTION.setRequired(false);
        HADOOP_HOME_OPTION.setRequired(false);
        FLINK_HOME_OPTION.setRequired(false);
    }


    public static Options getDspOptions() {
        return getOptions(
                getCommonOptions(),
                JOB_CONF_OPTION,
                JOB_ID_OPTION,
                HADOOP_HOME_OPTION,
                FLINK_HOME_OPTION,
                HELP_OPTION);
    }


}
