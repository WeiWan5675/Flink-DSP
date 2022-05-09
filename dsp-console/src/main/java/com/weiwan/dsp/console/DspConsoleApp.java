package com.weiwan.dsp.console;

import com.weiwan.dsp.api.constants.DspConstants;
import com.weiwan.dsp.common.utils.FileUtil;
import com.weiwan.dsp.common.utils.StringUtil;
import com.weiwan.dsp.common.utils.YamlUtils;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Import;
import springfox.documentation.oas.annotations.EnableOpenApi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * @author: xiaozhennan
 */
@EnableOpenApi
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, FlywayAutoConfiguration.class})
@MapperScan({"com.weiwan.dsp.console.mapper"})
@ServletComponentScan({"com.weiwan.dsp.console.filters", "com.weiwan.dsp.console.listeners"})
@Import(WebConfig.class)
public class DspConsoleApp {
    //CMD=java -Ddsp.base.dir=D:\develop\github\Flink-DSP -classpath ./lib/*.jar com.weiwan.dsp.console.DspConsoleApp

    public static final String[] ymlFiles = {
            "dsp-console.yaml",
            "dsp-console-db.yaml"
    };

    public static void main(String[] args) throws Exception {

        String baseDir = System.getProperty(DspConstants.DSP_BASE_DIR);
        if (StringUtils.isBlank(baseDir)) {
            System.err.println("Parameters [ " + DspConstants.DSP_BASE_DIR + " ] Can't be empty, can be used -D" + DspConstants.DSP_BASE_DIR + "");
            throw new RuntimeException(DspConstants.DSP_BASE_DIR + " is null");
        }

        String confDirVar = System.getProperty(DspConstants.DSP_CONF_DIR);
        if (StringUtils.isBlank(confDirVar)) {
            confDirVar = baseDir + File.separator + "conf";
        }

        String logDirVar = System.getProperty(DspConstants.DSP_LOG_DIR);
        if (StringUtils.isBlank(logDirVar)) {
            logDirVar = baseDir + File.separator + "logs";
        }
        final String confDir = confDirVar;
        final String logDir = logDirVar;
        System.setProperty(DspConstants.DSP_CONF_DIR, confDir);
        System.setProperty(DspConstants.DSP_LOG_DIR, logDir);
        String ymlLocations = Arrays.stream(ymlFiles)
                .map(str -> (confDir + File.separatorChar + str))
                .collect(Collectors.joining(","));

        Map<String, String> dspConfig = loadDspConfig(confDir);
        logSetting(dspConfig);
        Map<String, Object> configs = new HashMap<String, Object>();
        configs.put(DspConstants.SPRING_UPLOAD_MAX_FILE_SIZE, "1024MB");
        configs.put(DspConstants.SPRING_UPLOAD_MAX_REQUEST_SIZE, "1024MB");
        configs.put(DspConstants.SPRING_UPLOAD_ENABLED, true);
        System.setProperty(DspConstants.SPRING_SERVER_PORT, dspConfig.getOrDefault("dsp.console.server.port", "9876"));
        System.setProperty(DspConstants.SPRING_CONFIG_LOCATION, ymlLocations);
        System.out.println(DspConstants.DSP_BASE_DIR + " : " + baseDir);
        System.out.println(DspConstants.DSP_BASE_DIR + " : " + confDir);
        System.out.println(DspConstants.SPRING_CONFIG_LOCATION + " : " + ymlLocations);
        SpringApplication application = new SpringApplication(DspConsoleApp.class);
        application.setDefaultProperties(configs);
        application.run(args);
    }

    private static void logSetting(Map<String, String> dspConfig) {
        String baseDir = System.getProperty(DspConstants.DSP_BASE_DIR);
        String confDir = System.getProperty(DspConstants.DSP_CONF_DIR);
        String logDir = System.getProperty(DspConstants.DSP_LOG_DIR);

        String loggingConfig = dspConfig.get(DspConstants.DSP_CONSOLE_LOG_CONFIG);
        if (loggingConfig.contains("${dsp.conf.dir}")) {
            loggingConfig = loggingConfig.replace("${dsp.conf.dir}", confDir).replaceAll("[/\\\\]+", Matcher.quoteReplacement(File.separator)).replaceAll("[//]+", Matcher.quoteReplacement(File.separator));
        }

        if (!FileUtil.existsFile(loggingConfig)) {
            loggingConfig = "classpath:conf/" + DspConstants.DSP_CONSOLE_LOG_CONFIG_FILE;
        }
        String consoleLogDir = dspConfig.getOrDefault(DspConstants.DSP_CONSOLE_LOG_PATH, baseDir + File.separator + "logs");
        if (consoleLogDir.contains("${dsp.log.dir}")) {
            consoleLogDir = consoleLogDir.replace("${dsp.log.dir}", logDir).replaceAll("[/\\\\]+", Matcher.quoteReplacement(File.separator)).replaceAll("[//]+", Matcher.quoteReplacement(File.separator));
        }

        String jobLogDir = dspConfig.getOrDefault(DspConstants.DSP_JOB_LOG_PATH, baseDir + File.separator + "logs/jobs");
        if (jobLogDir.contains("${dsp.log.dir}")) {
            jobLogDir = jobLogDir.replace("${dsp.log.dir}", logDir).replaceAll("[/\\\\]+", Matcher.quoteReplacement(File.separator)).replaceAll("[//]+", Matcher.quoteReplacement(File.separator));
        }

        String logBackupDir = dspConfig.getOrDefault(DspConstants.DSP_BACKUP_LOG_PATH, baseDir + File.separator + "logs/backup");
        if (logBackupDir.contains("${dsp.log.dir}")) {
            logBackupDir = logBackupDir.replace("${dsp.log.dir}", logDir).replaceAll("[/\\\\]+", Matcher.quoteReplacement(File.separator)).replaceAll("[//]+", Matcher.quoteReplacement(File.separator));
        }
        File consoleLogDirFile = new File(consoleLogDir);
        if(!consoleLogDirFile.exists()){
            consoleLogDirFile.mkdirs();
        }
        File jobLogDirFile = new File(jobLogDir);
        if(!jobLogDirFile.exists()){
            jobLogDirFile.mkdirs();
        }
        File logBackupDirFile = new File(logBackupDir);
        if(!logBackupDirFile.exists()){
            logBackupDirFile.mkdirs();
        }
        System.setProperty(DspConstants.SPRING_LOGGING_CONFIG, loggingConfig);
        System.setProperty(DspConstants.DSP_CONSOLE_LOG_PATH, consoleLogDir);
        System.setProperty(DspConstants.DSP_JOB_LOG_PATH, jobLogDir);
        System.setProperty(DspConstants.DSP_BACKUP_LOG_PATH, logBackupDir);
        //这里是为了MDC在子线程时也可也正常获得JobID
        System.setProperty("log4j2.isThreadContextMapInheritable", "true");
    }

    private static Map<String, String> loadDspConfig(String confDir) throws FileNotFoundException {
        File dspConsoleFile = new File(confDir + File.separator + "dsp-console.yaml");
        FileInputStream dspConsoleInputStream = new FileInputStream(dspConsoleFile);
        return YamlUtils.getYamlByStream(dspConsoleInputStream);
    }

}


