package com.weiwan.dsp.console.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.weiwan.dsp.console.model.dto.ApplicationDeployDTO;
import com.weiwan.dsp.core.pub.JobID;

import java.io.IOException;
import java.net.URL;

/**
 * @author: xiaozhennan
 * @description:
 */
public interface ApplicationResourceService {


    URL generateJobFile(ApplicationDeployDTO dto) throws JsonProcessingException, IOException;

    JobID generateJobID(String appName);

    URL writeJobFileToDisk(String jobId, String content) throws IOException;
}
