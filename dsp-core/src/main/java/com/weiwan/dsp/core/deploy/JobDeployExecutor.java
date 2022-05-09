package com.weiwan.dsp.core.deploy;

import com.weiwan.dsp.api.config.core.*;
import com.weiwan.dsp.api.constants.DspConstants;
import com.weiwan.dsp.api.enums.ApplicationState;
import com.weiwan.dsp.api.enums.EngineMode;
import com.weiwan.dsp.api.enums.EngineType;
import com.weiwan.dsp.common.exception.DspException;
import com.weiwan.dsp.core.event.DspEventBus;
import com.weiwan.dsp.core.event.JobChangeEvent;
import com.weiwan.dsp.core.pub.SystemEnvManager;
import com.weiwan.dsp.core.utils.DspConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: xiaozhennan
 * @email: xiaozhennan1995@gmail.com
 * @date: 2021/4/30 14:51
 * @description:
 */
public abstract class JobDeployExecutor implements DeployExecutor {

    private static final Logger _LOGGER = LoggerFactory.getLogger(JobDeployExecutor.class);
    public static final String DSP_RUNTIME_MAIN_CLASS = "com.weiwan.dsp.runtime.app.DspRuntimeApp";
    protected DspConfig dspConfig;
    protected JobConfig jobConfig;
    protected CoreConfig coreConfig;
    protected EngineConfig engineConfig;
    protected MetricConfig metricConfig;
    protected String jobId;
    protected String jobName;
    protected EngineMode engineMode;
    protected EngineType engineType;
    private Configs engineConfigs;

    private URL jobFileUrl;
    private JobInfo jobInfo;
    private String jobWebUrl;
    private ApplicationState applicationState;
    private volatile boolean needRestart;
    private volatile boolean inited;

    public JobDeployExecutor(DspConfig dspConfig) {
        this.dspConfig = dspConfig;
        this.coreConfig = dspConfig.getCore();
        this.jobConfig = dspConfig.getJob();
        this.jobId = jobConfig.getJobId();
        this.jobName = jobConfig.getJobName();
        this.metricConfig = dspConfig.getCore().getMetricConfig();
        this.engineConfig = coreConfig.getEngineConfig();
        this.engineConfigs = engineConfig.getEngineConfigs();
        this.engineMode = engineConfig.getEngineMode();
        this.engineType = engineConfig.getEngineType();
    }

    @Override
    public synchronized void initDeploy() throws Exception {
        this.inited = false;
        // 生成最新的作业文件
        this.writeJobFileToDisk();
        // 校验作业是否正常
        this.verifyJob();
        // 打包作业信息
        this.assembleJobInfo();
        // 执行JobDeployExecutor初始化
        this.init();
        // 初始化完成
        this.inited = true;
    }

    public void writeJobFileToDisk() throws IOException {
        String jobFileContent = DspConfigFactory.dspConfigToContent(this.dspConfig);
        SystemEnvManager instance = SystemEnvManager.getInstance();
        String dspTmpDir = instance.getDspTmpDir();
        String jobFileDir = dspTmpDir + File.separator + "jobs";
        File jobDir = new File(jobFileDir);
        if (!jobDir.exists()) {
            jobDir.mkdirs();
        }
        String jobFileName = String.format(DspConstants.DSP_JOB_FILE_NAME_FORMAT, this.jobId);
        File file = DspConfigFactory.writeJobFile(jobFileDir, jobFileName, jobFileContent);
        this.jobFileUrl = file.toURI().toURL();
    }

    private void assembleJobInfo() throws Exception {
        this.jobInfo = new JobInfo();
        List<String> jobPluginJars = jobConfig.getJobPluginJars();
        List<URL> pluginUrlList = new ArrayList<URL>();
        for (String jobPluginJar : jobPluginJars) {
            pluginUrlList.add(new URL(jobPluginJar));
        }
        jobInfo.setJobPluginJars(pluginUrlList);
        jobInfo.setJobDefFile(this.jobFileUrl);
        jobInfo.setJobId(this.jobId);
        jobInfo.setJobName(this.jobName);
        jobInfo.setJobType(jobConfig.getJobType());
        jobInfo.setRuntimeJar(SystemEnvManager.getInstance().getDspRuntimeJar());
        jobInfo.setStartTime(new Date().getTime());
    }


    protected abstract void init() throws Exception;

    protected abstract void close() throws Exception;

    public void verifyJob() throws Exception {
        List<String> jobPluginJars = jobConfig.getJobPluginJars();
        //校验插件是否存在
        for (String jobPluginJar : jobPluginJars) {
            File file = new File(new URL(jobPluginJar).toURI());
            if (!file.exists()) {
                throw DspException.generateIllegalStateException("The job plugin does not exist, the job cannot be started");
            }
        }
        if (jobFileUrl == null) {
            throw DspException.generateIllegalStateException("No job definition file cannot be found, job cannot be started");
        }
        File jobDefFile = new File(jobFileUrl.toURI());
        if (!jobDefFile.exists()) {
            throw DspException.generateIllegalStateException("No job definition file cannot be found, job cannot be started");
        }

        URL dspRuntimeJar = SystemEnvManager.getInstance().getDspRuntimeJar();
        if (dspRuntimeJar == null) {
            throw DspException.generateIllegalStateException("Cannot find runtime jar file,job cannot be started");
        }
    }

    @Override
    public synchronized void closeDeploy() throws Exception {
        this.stop();
        this.close();
    }


    public JobConfig getJobConfig() {
        return jobConfig;
    }


    public boolean isNeedRestart() {
        return needRestart;
    }

    public void setNeedRestart(boolean needRestart) {
        this.needRestart = needRestart;
    }

    public synchronized ApplicationState modifyApplicationState(final ApplicationState applicationState, @Nullable final boolean notify) {
        _LOGGER.info("application state changed, jobID: {} , current state: {} >>> {}", jobId, this.applicationState, applicationState);
        this.applicationState = applicationState;
        if (notify) {
            //每次修改状态, 需要将修改的事件发送到后台,然后更新数据库
            JobChangeEvent jobChangeEvent = new JobChangeEvent(this);
            DspEventBus.pushEvent(new JobChangeEvent.JobChangeEventWrap(jobChangeEvent));
        }

        try {
            TimeUnit.MILLISECONDS.sleep(500L);
        } catch (InterruptedException e) {
        }
        return this.applicationState;
    }

    @Override
    public void upgradeDeploy(DspConfig dspConfig) {
        this.dspConfig = dspConfig;
        this.coreConfig = dspConfig.getCore();
        this.jobConfig = dspConfig.getJob();
        this.jobId = jobConfig.getJobId();
        this.jobName = jobConfig.getJobName();
        this.metricConfig = dspConfig.getCore().getMetricConfig();
        this.engineConfig = coreConfig.getEngineConfig();
        this.engineConfigs = engineConfig.getEngineConfigs();
        this.engineMode = engineConfig.getEngineMode();
        this.engineType = engineConfig.getEngineType();
    }

    @Override
    public ApplicationState getApplicationState() {
        return this.applicationState;
    }

    public boolean canWeReady() {
        return inited;
    }


    public JobInfo getDspJob() {
        return jobInfo;
    }

    public String getJobWebUrl() {
        return jobWebUrl;
    }

    protected void setJobWebUrl(String jobWebUrl) {
        this.jobWebUrl = jobWebUrl;
    }

    public Configs getEngineConfigs() {
        return engineConfigs;
    }

    public String getJobId() {
        return jobId;
    }
}
