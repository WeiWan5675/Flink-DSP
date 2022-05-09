package com.weiwan.dsp.console.model.query;

import lombok.Data;

/**
 * @author: xiaozhennan
 * @description:
 */
@Data
public class ApplicationDeployQuery extends PageQuery {
    private Integer deployId;
    private Integer jobId;
    private Integer jobState;
    private Integer engineType;
    private Integer engineMode;
    private Integer restartFlag;
    private Integer changeFlow;
    private Integer deployUser;
}
