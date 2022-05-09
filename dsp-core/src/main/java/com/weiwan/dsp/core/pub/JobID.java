package com.weiwan.dsp.core.pub;

import com.weiwan.dsp.common.enums.DspExceptionEnum;
import com.weiwan.dsp.common.exception.DspException;
import com.weiwan.dsp.core.utils.DspJobUtil;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * @author: xiaozhennan
 * @description:
 */
public class JobID {
    private String jobId;
    private String jobName;
    private Integer id;
    public JobID(String jobName) {
        this.jobName = jobName;
        this.jobId = DspJobUtil.getJobID(jobName);
    }

    public JobID(String jobName, String jobId) {
        this.jobName = jobName;
        if (jobId != null) {
            if (jobId.length() != 32) {
                throw DspException.generateParameterIllegalException("the job id needs to be a 32 bit string");
            }
            this.jobId = jobId;
        }
    }

    public JobID() {
        this.jobName = RandomStringUtils.randomAlphanumeric(10);
        this.jobId = DspJobUtil.getJobID(jobName);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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
}
