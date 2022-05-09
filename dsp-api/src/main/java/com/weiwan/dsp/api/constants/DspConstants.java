package com.weiwan.dsp.api.constants;

import java.io.File;
import java.io.Serializable;
import java.util.regex.Matcher;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/23 16:36
 * @description:
 */
public class DspConstants implements Serializable {


    //path
    public static final String DSP_BASE_DIR = "dsp.base.dir";
    public static final String DSP_CONF_DIR = "dsp.conf.dir";
    public static final String DSP_TMP_DIR = "dsp.tmp.dir";
    public static final String DSP_LIB_DIR = "dsp.lib.dir";
    public static final String DSP_LOG_DIR = "dsp.log.dir";
    public static final String DSP_PLUGIN_DIR = "dsp.plugin.dir";
    public static final String DSP_SYSTEM_PLUGIN_DIR = "dsp.system.plugin.dir";
    public static final String DSP_EXT_LIB_DIR = "dsp.ext-lib.dir";
    public static final String DSP_JOB_FILE_DIR = "dsp.tmp.job.file.dir";

    //env
    public static final String FLINK_HOME = "FLINK_HOME";
    public static final String FLINK_CONF_DIR = "FLINK_CONF_DIR";
    public static final String FLINK_LIB_DIR = "FLINK_LIB_DIR";
    public static final String FLINK_PLUGIN_DIR = "FLINK_PLUGIN_DIR";
    public static final String FLINK_BIN_DIR = "FLINK_BIN_DIR";
    public static final String HADOOP_HOME = "HADOOP_HOME";
    public static final String SPARK_HOME = "SPARK_HOME";

    public static final String FLINK_LOCAL_VERSION = "FLINK_LOCAL_VERSION";
    public static final String FLINK_LOCAL_DIST_JAR = "FLINK_LOCAL_DIST_JAR";

    //dsp
    public static final String DSP_NAME = "dsp.name";
    public static final String DSP_VERSION = "dsp.version";
    public static final String DSP_BUILD_VERSION = "dsp.build.version";
    public static final String DSP_BUILD_FLINK_VERSION = "dsp.build.flinkVersion";
    public static final String DSP_BUILD_HADOOP_VERSION = "dsp.build.hadoopVersion";
    public static final String DSP_BUILD_JDK_VERSION = "dsp.build.jdkVersion";
    public static final String DSP_BUILD_MAVEN_VERSION = "dsp.build.mavenVersion";
    public static final String DSP_JOB_FILE_NAME_FORMAT = "job_%s.json";


    //console
    public static final String DSP_CONSOLE_DEFAULT_CONFIG_FILE = "dsp-console-default.yaml";
    public static final String DSP_CONSOLE_DB_CONFIG_FILE = "dsp-console-db.yaml";
    public static final String DSP_CONSOLE_LOG_CONFIG_FILE = "log4j-console.properties";

    public static final String DSP_CONSOLE_LOG_PATH = "dsp.console.logging.console.dir";
    public static final String DSP_JOB_LOG_PATH = "dsp.console.logging.job.dir";
    public static final String DSP_BACKUP_LOG_PATH = "dsp.console.logging.backup";
    public static final String DSP_CONSOLE_LOG_CONFIG = "dsp.console.logging.config";

    public static final String SPRING_LOGGING_CONFIG = "logging.config";
    public static final String SPRING_CONFIG_LOCATION = "spring.config.additional-location";
    public static final String SPRING_SERVER_PORT = "server.port";

    //core
    public static final String DSP_CORE_CONF_FILE = "dsp-core.yaml";


    public static final Object USER_CLIENT_OPTION_KEY = "USER_CLIENT_OPTION";
    public static final String SPRING_UPLOAD_MAX_FILE_SIZE = "spring.servlet.multipart.max-file-size";
    public static final String SPRING_UPLOAD_MAX_REQUEST_SIZE = "spring.servlet.multipart.max-request-size";
    public static final String SPRING_UPLOAD_ENABLED = "spring.servlet.multipart.enabled";

}
