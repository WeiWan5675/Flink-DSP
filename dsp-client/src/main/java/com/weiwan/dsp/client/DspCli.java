package com.weiwan.dsp.client;

import com.weiwan.dsp.api.config.core.DspConfig;
import com.weiwan.dsp.api.config.core.JobConfig;
import com.weiwan.dsp.api.constants.ClientConstants;
import com.weiwan.dsp.api.constants.CoreConstants;
import com.weiwan.dsp.api.enums.QuitCode;
import com.weiwan.dsp.api.enums.RunCmd;
import com.weiwan.dsp.client.command.ExecuteCommander;
import com.weiwan.dsp.client.deploy.DeployerFactory;
import com.weiwan.dsp.core.shell.options.DspCliOptions;
import com.weiwan.dsp.core.pub.DspContext;
import com.weiwan.dsp.core.utils.DspConfigFactory;
import com.weiwan.dsp.core.utils.DspJobUtil;
import com.weiwan.dsp.core.pub.Context;
import com.weiwan.dsp.api.constants.DspConstants;
import com.weiwan.dsp.common.exception.DspException;
import com.weiwan.dsp.common.exception.DspExpectedExecption;
import com.weiwan.dsp.common.utils.*;
import com.weiwan.dsp.core.shell.CommonOptionParser;
import com.weiwan.dsp.core.shell.DspOptionParser;
import com.weiwan.dsp.core.shell.ExtendedParser;
import com.weiwan.dsp.client.deploy.DeployRunner;
import com.weiwan.dsp.core.deploy.DeployExecutor;
import com.weiwan.dsp.core.pub.SystemEnvManager;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.client.cli.CliArgsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/2/9 18:39
 * @description:
 */
public class DspCli {

    private static final Logger logger = LoggerFactory.getLogger(DspCli.class);

    public static void main(String[] args) {
        if (args.length < 1) {
            logger.error("Client failed to start", new RuntimeException("You need to specify a run shell"));
            System.exit(QuitCode.ERROR.getCode());
        }

        if (StringUtils.isBlank(args[0])) {
            logger.error("Client failed to start", new RuntimeException("You need to specify a run shell"));
            System.exit(QuitCode.ERROR.getCode());
        }
        logger.info("Enter the starting point of the Dsp client shell line");
        logger.info("Parse the currently executed shell line parameters");

        //解析命令行参数
        CommandLine commandLine = null;
        String[] dspArgs = new String[0];
        String[] otherArgs = new String[0];
        try {
            dspArgs = Arrays.copyOfRange(args, 1, args.length);
            final ExtendedParser parser = new ExtendedParser();
            commandLine = DspOptionParser.parse(parser, DspOptionParser.getDspOptions(), dspArgs, false);
            otherArgs = parser.getNotParsedArgs();
        } catch (CliArgsException e) {
            logger.error("Failed to parse shell line parameters", e);
            System.exit(QuitCode.CMD_PARSING_ERROR.getCode());
        }

        RunCmd runCmd = RunCmd.from(args[0]);
        //创建命令行Option
        DspCliOptions option = new DspCliOptions(commandLine, dspArgs, otherArgs);
        Context context = DspContext.getInstance().getContext();
        context.set(DspConstants.USER_CLIENT_OPTION_KEY, option);
        //打印帮助信息
        printHelpInfo(option);

        //创建环境管理器
        SystemEnvManager envManager = SystemEnvManager.getInstance(System.getenv());
        //校验环境DspConstants.FLINK_HOME
        if (!checkEnvironmentSettings(runCmd, option)) {
            logger.error("The environment check failed, please check the environment configuration");
            System.exit(QuitCode.ENV_CHECK_FAILED.getCode());
        }
        //校验命令行参数
        if (!checkCommandLineOption(runCmd, option)) {
            logger.error("Command line parameter error, please check the shell line parameter setting");
            System.exit(QuitCode.CMD_PARSING_ERROR.getCode());
        }

        //加载配置
        DspConfig finalDspConfig = getDspConfig(runCmd, option, envManager);
        //初始化上下文
        initContext(finalDspConfig);
        /**
         * 创建Commander,并初始化
         * 1. 根据命令不同,对参数进行校验
         * 2. 根据命令,解析不同的参数,用来支持deploy
         */
        ExecuteCommander commander = null;
        try {
            commander = ExecuteCommander.createCommander(runCmd, option, finalDspConfig);
        } catch (DspExpectedExecption e) {
            System.exit(QuitCode.SUCCESS.getCode());
        } catch (Exception e) {
            System.exit(QuitCode.CMD_PARSING_ERROR.getCode());
        }

        /**
         * 创建一个DeployExecutor线程对象
         * 该对象为实际将JobConfig描述的任务部署到对应的部署环境中
         * 这个对象会与集群环境进行通信,通信时使用Commander中提供的参数
         */
        DeployExecutor deployExecutor = DeployerFactory.createDeployer(finalDspConfig);

        /**
         * DeployRunner中包含一个线程池,用来提交deployerExecutor
         * 在提交时需要进行一系列的校验工作,需要调用DeployExecutor
         * 这里就采用线程池的方式部署
         */
        try (DeployRunner deployRunner = new DeployRunner(deployExecutor, finalDspConfig)) {
            try {
                deployRunner.start();
            } catch (DspException e) {
                logger.error(e.getMessage(), e);
                System.exit(QuitCode.JOB_SUBMIT_ERROR.getCode());
            }
        }

        logger.info("Client Job is complete");
        logger.info("Quit! Bye bye");
        System.exit(QuitCode.SUCCESS.getCode());
    }

    private static DspConfig getDspConfig(RunCmd runCmd, DspCliOptions option, SystemEnvManager envManager) {
        DspConfig finalDspConfig = null;
        try {
            if (runCmd == RunCmd.RUN) {
                finalDspConfig = generateAComprehensiveJobConfig(option, envManager);
            } else {
                finalDspConfig = loadDefaultProfile(envManager);
            }

            JobConfig job = finalDspConfig.getJob();
            String jobName = job.getJobName();
            String jobFile = null;
            String jobId = ClientConstants.JOB_ID.defaultValue();
            if (option.getJobId() == null) {
                jobId = generateJobID(jobName, jobFile);
            }
            job.setJobId(jobId);
            File jobTempFile = prepareJobFile(envManager.getJobFileDir(), finalDspConfig);
            //从临时文件中加载配置,这里生成下临时文件方便排查问题
            finalDspConfig = DspConfigFactory.load(jobTempFile);
        } catch (Exception e) {
            logger.error("An error occurred while parsing the configuration file or generating Job Config!", e);
            System.exit(QuitCode.CONFIG_PARSING_ERROR.getCode());
        }
        return finalDspConfig;
    }

    private static void initContext(DspConfig finalDspConfig) {
        Context context = DspContext.getInstance().getContext();
        context.setVal(ClientConstants.JOB_ID, finalDspConfig.getJob().getJobId());
        context.setVal(ClientConstants.JOB_NAME, finalDspConfig.getJob().getJobName());
    }

    private static String generateJobID(String jobName, String jobFile) {
        String jobId = null;
        if (jobName != null && jobFile != null) {
            if (jobFile.lastIndexOf(File.separator) != -1) {
                //包含了斜杠
                jobFile = jobFile.substring(jobFile.lastIndexOf(File.separator) + 1);
            }
            jobId = DspJobUtil.getJobID(jobName);
        }
        CheckTool.checkArgument(StringUtils.isNotBlank(jobId), "Unable to generate job ID");
        return jobId;
    }

    private static void printHelpInfo(DspCliOptions option) {
        if (option.hasOption(CommonOptionParser.HELP_OPTION)) {
            CommonOptionParser.printHelp();
            System.exit(QuitCode.SUCCESS.getCode());
        }
    }

    private static boolean checkCommandLineOption(RunCmd runCmd, DspCliOptions option) {
        if (runCmd == RunCmd.RUN) {
            if (StringUtils.isBlank(option.getJobFile())) {
                logger.error("Must specify a job profile");
                return false;
            }
        }
        if (runCmd == RunCmd.INFO || runCmd == RunCmd.STOP) {
            if (StringUtils.isBlank(option.getJobId())) {
                logger.error("Must specify a job ID");
                return false;
            }
        }

        return true;
    }

    private static boolean checkEnvironmentSettings(RunCmd runCmd, DspCliOptions option) {
        SystemEnvManager envManager = SystemEnvManager.getInstance();
        if (StringUtils.isBlank(envManager.getDspBaseDir())) {
            logger.error("Cannot find [" + DspConstants.DSP_BASE_DIR + "] from environment variables");
            return false;
        }
        if (StringUtils.isNotBlank(envManager.getDspConfDir())) {
            File file = new File(envManager.getDspConfDir());
            if (!file.exists() || !file.isDirectory()) {
                logger.error("Environment variable directory: [" + DspConstants.DSP_CONF_DIR + "] does not exist.");
                return false;
            }
        } else {
            logger.error("Cannot find [" + DspConstants.DSP_CONF_DIR + "] from environment variables");
            return false;
        }


//        if(StringUtils.isBlank(envManager.getFlinkHome())){
//            logger.error("Cannot find [" + DspConstants.FLINK_HOME + "] from environment variables");
//            return false;
//        }
//
//        if(StringUtils.isBlank(envManager.getEnv(DspConstants.FLINK_LOCAL_DIST_JAR))){
//            logger.error("Cannot find [" + DspConstants.FLINK_LOCAL_DIST_JAR + "] from Flink lib dir");
//            return false;
//        }

        return true;
    }


    private static File prepareJobFile(String tmpJobDir, DspConfig dspConfig) throws IOException {
        //把合并后的配置转换成文本
        String content = DspConfigFactory.dspConfigToContent(dspConfig);
        //文本写入到本地临时文件
        String fileName = String.format(CoreConstants.JOB_FILE_FORMAT, dspConfig.getJob().getJobId());
        File file = DspConfigFactory.writeJobFile(tmpJobDir, fileName, content);
        Context context = DspContext.getInstance().getContext();
        context.setVal(ClientConstants.JOB_FILE, file.getAbsolutePath());
        context.setVal(ClientConstants.JOB_TMP_DIR, file.getParent());
        return file;
    }

    private static DspConfig generateAComprehensiveJobConfig(DspCliOptions option, SystemEnvManager envManager) throws Exception {
        //创建一个默认的配置
        DspConfig defaultConfig = loadDefaultProfile(envManager);
        //读取用户的job文件
        String jobFilePath = option.getJobFile().trim();
        DspConfig userDspConfig = loadUserProfile(jobFilePath);
        //合并用户配置和默认配置
        DspConfig dspConfig = mergeProfile(defaultConfig, userDspConfig);
        //设置其他属性
//        dspConfig.getJob().setJobFile(jobFilePath);
        return dspConfig;
    }

    private static DspConfig mergeProfile(DspConfig defaultConfig, DspConfig userDspConfig) {
        if (defaultConfig != null && userDspConfig != null) {
            logger.info("Merge configuration files to generate a final configuration file");
            return DspConfigFactory.mergeJobConfig(defaultConfig, userDspConfig);
        } else {
            logger.error("Error in merge job profile.");
            throw new DspException("Unable to merge the complete job file, please check the default configuration or user job profile");
        }

    }

    private static DspConfig loadUserProfile(String jobFilePath) throws IOException {
        logger.info("Load user profile: {}", jobFilePath);
        File jobFile = new File(jobFilePath);
        if (!FileUtil.existsFile(jobFile)) {
            logger.error("Cannot find the specified job profile: {}", jobFile);
            throw new DspException("Job profile does not exist");
        }
        String userJobContent = DspConfigFactory.readConfigFile(jobFile);
        //用户的job文件转换为map
        DspConfig userDspConfig = DspConfigFactory.loadFromJson(userJobContent);
        return userDspConfig;
    }

    private static DspConfig loadDefaultProfile(SystemEnvManager envManager) throws Exception {
        logger.info("Load default profile for classpath: {}", "conf/dsp-core.yaml");
        logger.info("Load default profile for conf path: {}", envManager.getDspConfDir() + "/dsp-core.yaml");
        DspConfig defaultConfig = DspConfigFactory.createDefaultConfig(envManager.getDspConfDir());
        return defaultConfig;
    }

    public static File writeJobFile(String tmpDir, String jobId, String content) throws IOException {
        String jobFileName = "job_" + jobId + ".json";
        File file = new File(tmpDir);
        File jobFile = null;
        if (file.exists() && file.isDirectory()) {
            //文件夹是存在的,那我们需要处理任务重复提交的问题
            String dateStr = DateUtils.getDateStr(new Date());
            String jobTmpDir = tmpDir + File.separator + dateStr;
            String jobFilePath = jobTmpDir + File.separator + jobFileName;
            FileUtil.delete(new File(jobTmpDir));
            FileUtil.createDir(jobTmpDir);
            jobFile = new File(jobFilePath);

        }
        logger.info("Write the job configuration file to the temporary file : {}", jobFile.getAbsoluteFile());
//        jobTempFile.deleteOnExit();
        if (!jobFile.exists()) {
            jobFile.createNewFile();
        }
        if (jobFile.canWrite()) {
            BufferedWriter bw = new BufferedWriter(new FileWriter(jobFile));
            bw.write(content);
            bw.close();
        }
        return jobFile;
    }
}
