package com.weiwan.dsp.console.model.query;

import lombok.Data;

/**
 * @Author: xiaozhennan
 * @Date: 2021/9/10 23:13
 * @Package: com.weiwan.dsp.console.model.query
 * @ClassName: DspReferenceQuery
 * @Description:
 **/
@Data
public class ReferenceQuery extends PageQuery {
    private Integer id;
    private Integer flowId;
    private Integer appId;
    private Integer pluginId;
    private Integer pluginType;
    private Integer configId;
    private Integer configType;
    private Integer referenceType;
}
