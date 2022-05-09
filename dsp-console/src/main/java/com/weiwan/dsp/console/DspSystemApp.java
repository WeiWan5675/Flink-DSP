package com.weiwan.dsp.console;

import com.weiwan.dsp.common.constants.Constants;
import com.weiwan.dsp.api.constants.DspConstants;
import com.weiwan.dsp.common.utils.YamlUtils;
import com.weiwan.dsp.core.shell.DspOptionParser;
import com.weiwan.dsp.core.shell.ExtendedParser;
import org.apache.commons.cli.CommandLine;
import org.apache.flink.client.cli.CliArgsException;

import java.io.File;
import java.util.Map;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/28 16:52
 * @description: 系统操作入口
 */
public class DspSystemApp {

    public static void main(String[] args) {
        final ExtendedParser parser = new ExtendedParser();
        CommandLine commandLine = null;
        try {
            commandLine = DspOptionParser.parse(parser, DspOptionParser.getDspOptions(), args, false);
        } catch (CliArgsException e) {
            e.printStackTrace();
        }
        if (commandLine.hasOption(DspOptionParser.VERBOSE.getOpt())) {
            //打印版本号
            printVersion();
        }

        if (commandLine.hasOption(DspOptionParser.HELP_OPTION.getOpt())) {
            printHelp();
        }

    }


    private static void printHelp() {

    }


    public static void printVersion() {
        Map<String, String> yamlByFileName = YamlUtils.getYamlByStream(DspSystemApp.class.getClassLoader().getResourceAsStream("conf" + File.separator + DspConstants.DSP_CONSOLE_DEFAULT_CONFIG_FILE));
        String dspName = yamlByFileName.getOrDefault(DspConstants.DSP_NAME, Constants.UNKNOWN);
        String dspVersion = yamlByFileName.getOrDefault(DspConstants.DSP_VERSION, Constants.UNKNOWN);
        String dspBuildVersion = yamlByFileName.getOrDefault(DspConstants.DSP_BUILD_VERSION, Constants.UNKNOWN);
        String flinkVersion = yamlByFileName.getOrDefault(DspConstants.DSP_BUILD_FLINK_VERSION, Constants.UNKNOWN);
        String hadoopVersion = yamlByFileName.getOrDefault(DspConstants.DSP_BUILD_HADOOP_VERSION, Constants.UNKNOWN);
        String jdkVersion = yamlByFileName.getOrDefault(DspConstants.DSP_BUILD_JDK_VERSION, Constants.UNKNOWN);
        String mavenVersion = yamlByFileName.getOrDefault(DspConstants.DSP_BUILD_MAVEN_VERSION, Constants.UNKNOWN);

        System.out.println(String.format("%s Version: %s", dspName, dspVersion));
        System.out.println(String.format("%s Build Version: %s", dspName, dspBuildVersion));
        System.out.println(String.format("Build with:"));
        System.out.println(String.format("\tJdk Version: %s", jdkVersion));
        System.out.println(String.format("Create By:"));
        System.out.println(String.format("\tMavne Version: %s", mavenVersion));
        System.out.println(String.format("Other Version:"));
        System.out.println(String.format("\tFlink Version: %s", flinkVersion));
        System.out.println(String.format("\tHadoop Version: %s", hadoopVersion));
        System.out.println(String.format("The above is the version information of %s.", dspName));

    }
}
