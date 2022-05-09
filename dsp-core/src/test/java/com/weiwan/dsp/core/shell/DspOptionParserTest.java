package com.weiwan.dsp.core.shell;

import junit.framework.TestCase;
import org.apache.commons.cli.CommandLine;
import org.apache.flink.client.cli.CliArgsException;
import org.junit.Test;

import java.util.Arrays;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/6/1 14:31
 * @description:
 */
public class DspOptionParserTest extends TestCase {

    @Test
    public void testDpsOptionParser() throws CliArgsException {
        String[] args = Arrays.asList("-c", "D:\\develop\\github\\Flink-DSP\\conf\\example\\job-flow.json", "-hadoopHome", "./hadoopdir", "-j", "test.jar").toArray(new String[0]);

        CommandLine parse = FlinkOptionParser.parse(FlinkOptionParser.getRunCommandOptions(), args, false);

        if(parse.hasOption(DspOptionParser.JOB_CONF_OPTION.getOpt())){
            String optionValue = parse.getOptionValue(DspOptionParser.JOB_CONF_OPTION.getOpt());
            System.out.println(optionValue);
        }
        if(parse.hasOption(DspOptionParser.HADOOP_HOME_OPTION.getOpt())){
            String optionValue = parse.getOptionValue(DspOptionParser.HADOOP_HOME_OPTION.getOpt());
            System.out.println(optionValue);
        }

        if(parse.hasOption(FlinkOptionParser.JOB_ID_OPTION.getOpt())){
            String optionValue = parse.getOptionValue(FlinkOptionParser.JOB_ID_OPTION.getOpt());
            System.out.println(optionValue);
        }

    }
}