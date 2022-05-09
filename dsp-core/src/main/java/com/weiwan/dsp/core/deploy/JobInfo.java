package com.weiwan.dsp.core.deploy;

import com.weiwan.dsp.api.config.core.JobConfig;
import com.weiwan.dsp.api.enums.ApplicationType;

import java.net.URL;
import java.util.List;

/**
 * @Author: xiaozhennan
 * @Date: 2021/10/20 21:05
 * @ClassName: JobInfo
 * @Description: Dsp作业封装
 **/
public class JobInfo {


    private String jobId;
    private String jobName;
    private ApplicationType jobType;
    private URL runtimeJar;
    private List<URL> jobPluginJars;
    private URL jobDefFile;
    private long startTime;
    private long endTime;
    private long lastRefreshTime;
    private JobConfig jobConfig;


    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public ApplicationType getJobType() {
        return jobType;
    }

    public void setJobType(ApplicationType jobType) {
        this.jobType = jobType;
    }

    public URL getRuntimeJar() {
        return runtimeJar;
    }

    public void setRuntimeJar(URL runtimeJar) {
        this.runtimeJar = runtimeJar;
    }

    public List<URL> getJobPluginJars() {
        return jobPluginJars;
    }

    public void setJobPluginJars(List<URL> jobPluginJars) {
        this.jobPluginJars = jobPluginJars;
    }

    public URL getJobDefFile() {
        return jobDefFile;
    }

    public void setJobDefFile(URL jobDefFile) {
        this.jobDefFile = jobDefFile;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getLastRefreshTime() {
        return lastRefreshTime;
    }

    public void setLastRefreshTime(long lastRefreshTime) {
        this.lastRefreshTime = lastRefreshTime;
    }

    public JobConfig getJobConfig() {
        return jobConfig;
    }

    public void setJobConfig(JobConfig jobConfig) {
        this.jobConfig = jobConfig;
    }

    public static final class DspJobBuilder {
        private String jobId;
        private String jobName;
        private ApplicationType jobType;
        private URL runtimeJar;
        private List<URL> jobPluginJars;
        private URL jobDefFile;
        private long startTime;
        private long endTime;
        private long lastRefreshTime;
        private JobConfig jobConfig;

        private DspJobBuilder() {
        }

        public static DspJobBuilder aDspJob() {
            return new DspJobBuilder();
        }

        public DspJobBuilder jobId(String jobId) {
            this.jobId = jobId;
            return this;
        }

        public DspJobBuilder jobName(String jobName) {
            this.jobName = jobName;
            return this;
        }

        public DspJobBuilder jobType(ApplicationType jobType) {
            this.jobType = jobType;
            return this;
        }

        public DspJobBuilder runtimeJar(URL runtimeJar) {
            this.runtimeJar = runtimeJar;
            return this;
        }

        public DspJobBuilder jobPluginJars(List<URL> jobPluginJars) {
            this.jobPluginJars = jobPluginJars;
            return this;
        }

        public DspJobBuilder jobDefFile(URL jobDefFile) {
            this.jobDefFile = jobDefFile;
            return this;
        }

        public DspJobBuilder startTime(long startTime) {
            this.startTime = startTime;
            return this;
        }

        public DspJobBuilder endTime(long endTime) {
            this.endTime = endTime;
            return this;
        }

        public DspJobBuilder lastRefreshTime(long lastRefreshTime) {
            this.lastRefreshTime = lastRefreshTime;
            return this;
        }

        public DspJobBuilder jobConfig(JobConfig jobConfig) {
            this.jobConfig = jobConfig;
            return this;
        }

        public JobInfo build() {
            JobInfo jobInfo = new JobInfo();
            jobInfo.jobName = this.jobName;
            jobInfo.startTime = this.startTime;
            jobInfo.jobId = this.jobId;
            jobInfo.jobDefFile = this.jobDefFile;
            jobInfo.jobConfig = this.jobConfig;
            jobInfo.endTime = this.endTime;
            jobInfo.jobPluginJars = this.jobPluginJars;
            jobInfo.jobType = this.jobType;
            jobInfo.runtimeJar = this.runtimeJar;
            jobInfo.lastRefreshTime = this.lastRefreshTime;
            return jobInfo;
        }
    }
}
