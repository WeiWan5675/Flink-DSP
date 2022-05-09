package com.weiwan.dsp.core.pub;

import com.sun.istack.NotNull;
import com.weiwan.dsp.api.constants.DspConstants;
import com.weiwan.dsp.common.exception.DspException;
import com.weiwan.dsp.common.utils.CheckTool;
import com.weiwan.dsp.common.utils.CommonUtil;
import com.weiwan.dsp.common.utils.FileUtil;
import com.weiwan.dsp.core.shell.options.DspCliOptions;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/5/17 17:38
 * @description:
 */
public class SystemEnvManager {
    private static final Logger logger = LoggerFactory.getLogger(SystemEnvManager.class);
    private static SystemEnvManager envManager;
    private Map<String, String> internalVariables = new ConcurrentHashMap<String, String>();
    private Map<String, String> systemVariables = new ConcurrentHashMap<>();

    public static SystemEnvManager getInstance(Map<String, String> systemVariables) {
        if (envManager == null) {
            synchronized (SystemEnvManager.class) {
                if (envManager == null) {
                    envManager = new SystemEnvManager();
                    if (systemVariables != null) {
                        envManager.initEnv(systemVariables);
                    } else {
                        envManager.initEnv(System.getenv());
                    }
                }
            }
        }
        return envManager;
    }

    public static SystemEnvManager getInstance() {
        if (envManager == null) {
            synchronized (SystemEnvManager.class) {
                if (envManager == null) {
                    logger.info("Initialize the Dsp environment manager");
                    envManager = new SystemEnvManager();
                    envManager.initEnv(System.getenv());
                }
            }
        }
        return envManager;
    }

    private SystemEnvManager() {
    }

    public String getDspSignKey() {
        return internalVariables.get("dsp.security.report.key");
    }


    private void initEnv(Map<String, String> systemVariables) {
        logger.info("Initialize the Dsp environment manager");
        initOsEnv(systemVariables);
        initFlinkEnv();
        initHadoopEnv();
        initSparkEnv();
        printSystemEnv();
        logger.info("Initialize the Dsp environment manager complete");
    }

    private DspCliOptions getUserCmdLine() {
        Context context = DspContext.getInstance().getContext();
        Object o = context.get(DspConstants.USER_CLIENT_OPTION_KEY);
        if (o != null) {
            return (DspCliOptions) o;
        }
        return null;
    }

    private static void printSystemEnv() {
        Map<String, String> systemVariables = System.getenv();
        for (String envKey : systemVariables.keySet()) {
            if (logger.isDebugEnabled()) {
                logger.debug("key: {} \tvalue: {}", envKey, systemVariables.get(envKey));
            }
        }
    }

    public String getDspBaseDir() {
        String baseDir = internalVariables.get(DspConstants.DSP_BASE_DIR);
        if (StringUtils.isBlank(baseDir)) {
            //dsp.base.dir是空的
            baseDir = System.getProperty(DspConstants.DSP_BASE_DIR);
            if (StringUtils.isBlank(baseDir)) {
                //获取class所在地址
                String classFilePath = CommonUtil.getAppPath(SystemEnvManager.class);
                File file = new File(classFilePath);
                baseDir = file.getParent();
            }
            internalVariables.put(DspConstants.DSP_BASE_DIR, baseDir);
        }
        return baseDir;
    }


    public String getDspConfDir() {
        String confDir = internalVariables.get(DspConstants.DSP_CONF_DIR);
        if (StringUtils.isBlank(confDir)) {
            confDir = System.getProperty(DspConstants.DSP_CONF_DIR);
            if (StringUtils.isBlank(confDir)) {
                confDir = getDspBaseDir() + File.separator + "conf";
            }
            internalVariables.put(DspConstants.DSP_CONF_DIR, confDir);
        }
        return confDir;
    }

    public String getDspTmpDir() {
        String tmpDir = internalVariables.get(DspConstants.DSP_TMP_DIR);
        if (StringUtils.isBlank(tmpDir)) {
            tmpDir = System.getProperty(DspConstants.DSP_TMP_DIR);
            if (StringUtils.isBlank(tmpDir)) {
                tmpDir = getDspBaseDir() + File.separator + "tmp";
            }
            internalVariables.put(DspConstants.DSP_TMP_DIR, tmpDir);
        }
        return tmpDir;
    }

    public String getDspLibDir() {
        String libDir = internalVariables.get(DspConstants.DSP_LIB_DIR);
        if (StringUtils.isBlank(libDir)) {
            libDir = System.getProperty(DspConstants.DSP_LIB_DIR);
            if (StringUtils.isBlank(libDir)) {
                libDir = getDspBaseDir() + File.separator + "lib";
            }
            internalVariables.put(DspConstants.DSP_LIB_DIR, libDir);
        }
        return libDir;
    }

    public String getDspExtLibDir() {
        String extlibDir = internalVariables.get(DspConstants.DSP_EXT_LIB_DIR);
        if (StringUtils.isBlank(extlibDir)) {
            extlibDir = System.getProperty(DspConstants.DSP_EXT_LIB_DIR);
            if (StringUtils.isBlank(extlibDir)) {
                extlibDir = getDspBaseDir() + File.separator + "ext-lib";
            }
            internalVariables.put(DspConstants.DSP_EXT_LIB_DIR, extlibDir);
        }
        return extlibDir;
    }

    public String getDspPluginDir() {
        String pluginDir = internalVariables.get(DspConstants.DSP_PLUGIN_DIR);
        if (StringUtils.isBlank(pluginDir)) {
            pluginDir = System.getProperty(DspConstants.DSP_PLUGIN_DIR);
            if (StringUtils.isBlank(pluginDir)) {
                pluginDir = getDspBaseDir() + File.separator + "plugin";
            }
            internalVariables.put(DspConstants.DSP_PLUGIN_DIR, pluginDir);
        }
        return pluginDir;
    }

    public String getSystemPluginDir() {
        String systemPluginDir = internalVariables.get(DspConstants.DSP_SYSTEM_PLUGIN_DIR);
        if (StringUtils.isBlank(systemPluginDir)) {
            systemPluginDir = System.getProperty(DspConstants.DSP_SYSTEM_PLUGIN_DIR);
            if (StringUtils.isBlank(systemPluginDir)) {
                systemPluginDir = getDspLibDir() + File.separator + "system-plugin";
            }
            internalVariables.put(DspConstants.DSP_SYSTEM_PLUGIN_DIR, systemPluginDir);
        }
        return systemPluginDir;
    }

    public String getJobFileDir() {
        String jobFileDir = internalVariables.get(DspConstants.DSP_JOB_FILE_DIR);
        if (StringUtils.isBlank(jobFileDir)) {
            jobFileDir = System.getProperty(DspConstants.DSP_JOB_FILE_DIR);
            if (StringUtils.isBlank(jobFileDir)) {
                jobFileDir = getDspTmpDir() + File.separator + "jobs";
            }
            internalVariables.put(DspConstants.DSP_JOB_FILE_DIR, jobFileDir);
        }
        if (!FileUtil.existsDir(jobFileDir)) {
            FileUtil.createDir(jobFileDir);
        }
        return jobFileDir;
    }


    private void initOsEnv(Map<String, String> initVariables) {
        if (initVariables != null) {
            for (String key : initVariables.keySet()) {
                systemVariables.put(key, initVariables.get(key));
                internalVariables.put(key, initVariables.get(key));
            }
        }
    }

    private void initSparkEnv() {
        String sparkHome = System.getenv(DspConstants.SPARK_HOME);
        if (sparkHome != null) {
            internalVariables.put(DspConstants.HADOOP_HOME, sparkHome);
        }
    }

    private void initHadoopEnv() {
        DspCliOptions userCmdLine = getUserCmdLine();
        String hadoopHome = null;
        if (userCmdLine != null) {
            hadoopHome = userCmdLine.getHadoopHome();
        }
        if (hadoopHome == null) {
            hadoopHome = systemVariables.get(DspConstants.HADOOP_HOME);
        }
        if (hadoopHome != null) {
            internalVariables.put(DspConstants.HADOOP_HOME, hadoopHome);
        }
    }

    private void initFlinkEnv() {
        //命令行最高优先级
        String flinkHome = null;
        DspCliOptions userCmdLine = getUserCmdLine();
        if (userCmdLine != null) {
            flinkHome = userCmdLine.getFlinkHome();
        }
        if (flinkHome == null && systemVariables != null) {
            flinkHome = systemVariables.get(DspConstants.FLINK_HOME);
        }

        //从环境变量获取flinkConfDir
        String flinkConfDir = systemVariables.get(DspConstants.FLINK_CONF_DIR);
        //如果没有就拼接
        if (flinkHome != null && flinkConfDir == null) {
            flinkConfDir = flinkHome + File.separator + "conf";
        }

        if (StringUtils.isNotBlank(flinkHome)) internalVariables.put(DspConstants.FLINK_HOME, flinkHome);
        if (StringUtils.isNotBlank(flinkConfDir)) internalVariables.put(DspConstants.FLINK_CONF_DIR, flinkConfDir);

        //设置其它目录
        if (StringUtils.isNotBlank(flinkHome)) {
            internalVariables.put(DspConstants.FLINK_LIB_DIR, flinkHome + File.separator + "lib");
            internalVariables.put(DspConstants.FLINK_PLUGIN_DIR, flinkHome + File.separator + "plugins");
            internalVariables.put(DspConstants.FLINK_BIN_DIR, flinkHome + File.separator + "bin");
        }

        if (StringUtils.isNotBlank(internalVariables.get(DspConstants.FLINK_LIB_DIR))) {
            File distFile = getFlinkDistJar();
            String flinkVersion = getFlinkVersion(distFile);
            if (distFile != null) {
                internalVariables.put(DspConstants.FLINK_LOCAL_DIST_JAR, distFile.getAbsolutePath());
            }
            if (flinkVersion != null) {
                internalVariables.put(DspConstants.FLINK_LOCAL_VERSION, flinkVersion);
            }
        }


    }

    public String getEnv(@NotNull String envKey) {
        if (StringUtils.isNotBlank(envKey)) {
            return internalVariables.get(envKey);
        }
        return null;
    }

    public String getEnv(@NotNull String envKey, String defaultVar) {
        String val = getEnv(envKey);
        return val != null ? val : defaultVar;
    }

    private File getFlinkDistJar() {
        File distFile = null;
        try {
            File file = new File(internalVariables.get(DspConstants.FLINK_LIB_DIR));
            File[] files = file.listFiles();
            CheckTool.checkNotNull(files, "Cannot find Flink's dependent files under Flink Hom eLib");
            List<File> collect = Arrays.stream(files).filter(f -> f.getName().contains("flink-dist_")).collect(Collectors.toList());
            distFile = null;
            if (collect != null && collect.size() == 1) {
                distFile = collect.get(0);
            }
        } catch (Exception e) {
            logger.warn("Error getting Flink dist jar path", e);
        }
        return distFile;
    }

    private String getFlinkVersion(File distFile) {
        String flinkVersion = null;
        try {
            String name = distFile.getName();
            int i = name.lastIndexOf("_");
            String substring = name.substring(i + 1, name.length());
            String[] split = substring.split("-");
            String scalaVersion = split[0];
            flinkVersion = split[1].replace(".jar", "");
        } catch (Exception e) {
            logger.warn("Error getting Flink version", e);
        }
        return flinkVersion;
    }


    public String getHadoopHome() {
        return internalVariables.get(DspConstants.HADOOP_HOME);
    }

    public String getFlinkConfDir() {
        return internalVariables.get(DspConstants.FLINK_CONF_DIR);
    }

    public String getFlinkHome() {
        return internalVariables.get(DspConstants.FLINK_HOME);
    }

    public List<URL> getDspRuntimeLibs() {
        String dspLibDir = getDspLibDir();
        File file = new File(dspLibDir);
        if (file.exists() && file.listFiles().length > 0) {
            File[] files = file.listFiles();
            return Arrays.stream(files).filter(f1 -> {
                        if (f1.getName().startsWith("dsp-") && f1.getName().endsWith(".jar")) {
                            return !f1.getName().startsWith("dsp-console-") ? true : false;
                        } else {
                            return false;
                        }
                    }
            ).map(f2 -> f2.toPath()).map(f3 -> {
                try {
                    return f3.toUri().toURL();
                } catch (MalformedURLException e) {
                    return null;
                }
            }).collect(Collectors.toList());
        }
        throw DspException.generateIllegalStateException("Unable to find the relevant dependencies for the program to run, please check the lib directory");
    }


    public URL getDspRuntimeJar(){
        String dspLibDir = getDspLibDir();
        File file = new File(dspLibDir);
        if (file.exists() && file.listFiles().length > 0) {
            File[] files = file.listFiles();
            Stream<File> fileStream = Arrays.stream(files).filter(f1 -> {
                        if (f1.getName().startsWith("dsp-runtime") && f1.getName().endsWith(".jar")) {
                            return true;
                        } else {
                            return false;
                        }
                    }
            );
            File file1 = fileStream.collect(Collectors.toList()).get(0);
            try {
                return file1.toURI().toURL();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getDspLogDir() {
        String logDir = internalVariables.get(DspConstants.DSP_LOG_DIR);
        if (StringUtils.isBlank(logDir)) {
            logDir = System.getProperty(DspConstants.DSP_LOG_DIR);
            if (StringUtils.isBlank(logDir)) {
                logDir = getDspBaseDir() + File.separator + "logs";
            }
            internalVariables.put(DspConstants.DSP_LOG_DIR, logDir);
        }
        return logDir;
    }
}
