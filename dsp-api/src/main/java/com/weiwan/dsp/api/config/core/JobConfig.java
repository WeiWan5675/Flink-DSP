package com.weiwan.dsp.api.config.core;

import com.weiwan.dsp.api.enums.ApplicationType;
import com.weiwan.dsp.common.config.ConfigOption;
import com.weiwan.dsp.common.config.ConfigOptions;

import java.io.Serializable;
import java.net.URL;
import java.util.List;

/**
 * @Author: xiaozhennan
 * @Date: 2021/10/15 0:34
 * @Package: com.weiwan.dsp.api.config.core
 * @ClassName: JobConfig
 * @Description:
 **/
public class JobConfig implements Serializable {
    public static final ConfigOption<Long> JOB_DEPLOY_TIMEOUT = ConfigOptions.key("jobDeployTimeout")
            .fullKey("dsp.core.jobConfig.jobDeployTimeout")
            .defaultValue(90L)
            .description("作业部署超时时间")
            .ok(Long.class);

    public static final ConfigOption<Long> JOB_NAME = ConfigOptions.key("jobName")
            .fullKey("dsp.core.jobConfig.jobName")
            .defaultValue("DspJob")
            .description("作业名称")
            .ok(Long.class);

    public static final ConfigOption<Long> JOB_ID = ConfigOptions.key("jobId")
            .fullKey("dsp.core.jobConfig.jobId")
            .defaultValue("00000000000000000000000000000000")
            .description("作业ID")
            .ok(Long.class);

    public static final ConfigOption<Long> JOB_FILE = ConfigOptions.key("jobFile")
            .fullKey("dsp.core.jobConfig.jobFile")
            .defaultValue("conf/example/job-flow.json")
            .description("作业文件")
            .ok(Long.class);


    private String jobName;
    private String jobId;
    private ApplicationType jobType;
    private List<String> jobPluginJars;
    private long jobDeployTimeout = 90;


    public List<String> getJobPluginJars() {
        return jobPluginJars;
    }

    public void setJobPluginJars(List<String> jobPluginJars) {
        this.jobPluginJars = jobPluginJars;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobId() {
        return jobId;
    }

    public long getJobDeployTimeout() {
        return jobDeployTimeout;
    }

    public void setJobDeployTimeout(long jobDeployTimeout) {
        this.jobDeployTimeout = jobDeployTimeout;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public ApplicationType getJobType() {
        return jobType;
    }

    public void setJobType(ApplicationType jobType) {
        this.jobType = jobType;
    }
}
