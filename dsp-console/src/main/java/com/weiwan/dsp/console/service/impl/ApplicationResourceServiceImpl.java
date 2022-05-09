package com.weiwan.dsp.console.service.impl;

import com.weiwan.dsp.api.config.core.DspContextConfig;
import com.weiwan.dsp.api.constants.DspConstants;
import com.weiwan.dsp.common.utils.CheckTool;
import com.weiwan.dsp.console.model.dto.ApplicationDeployDTO;
import com.weiwan.dsp.console.service.ApplicationResourceService;
import com.weiwan.dsp.core.pub.JobID;
import com.weiwan.dsp.core.pub.SystemEnvManager;
import com.weiwan.dsp.core.utils.DspConfigFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @author: xiaozhennan
 * @description:
 */
@Service
public class ApplicationResourceServiceImpl implements ApplicationResourceService {


    @Override
    public URL generateJobFile(ApplicationDeployDTO dto) throws IOException {
        DspContextConfig jobJson = dto.getDspContext();
        String content = DspConfigFactory.dspConfigToContent(jobJson.getDsp());
        String jobId = dto.getJobId();
        return null;
    }

    @Override
    public JobID generateJobID(String appName) {
        if (StringUtils.isNotBlank(appName)) {
            return new JobID(appName);
        }
        return new JobID();
    }

    @Override
    public URL writeJobFileToDisk(String jobId, String content) throws IOException {
        CheckTool.checkNotNull(jobId, "必须指定一个作业ID");
        //1. 获取资源管理器
        SystemEnvManager instance = SystemEnvManager.getInstance();
        String dspTmpDir = instance.getDspTmpDir();
        String jobFileDir = dspTmpDir + File.separator + "jobs";
        File jobDir = new File(jobFileDir);
        if (!jobDir.exists()) {
            jobDir.mkdirs();
        }

        String jobFileName = String.format(DspConstants.DSP_JOB_FILE_NAME_FORMAT, jobId);
        File file = DspConfigFactory.writeJobFile(jobFileDir, jobFileName, content);
        return file != null ? file.toURI().toURL() : null;
    }
}
