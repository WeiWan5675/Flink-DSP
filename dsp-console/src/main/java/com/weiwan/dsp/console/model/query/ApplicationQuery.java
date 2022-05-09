package com.weiwan.dsp.console.model.query;

import com.weiwan.dsp.api.enums.ApplicationState;
import com.weiwan.dsp.api.enums.ApplicationType;
import lombok.Data;

import java.util.Date;

/**
 * @author: xiaozhennan
 * @description:
 */
@Data
public class ApplicationQuery extends PageQuery {
    private Integer appId;
    private String jobId;
    private String appName;
    private Integer flowId;
    private ApplicationType[] appType;
    private ApplicationState[] applicationState;
    private String flowName;
}
