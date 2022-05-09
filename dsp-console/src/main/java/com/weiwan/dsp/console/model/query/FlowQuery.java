package com.weiwan.dsp.console.model.query;

import lombok.Data;

/**
 * @Author: xiaozhennan
 * @Date: 2021/10/31 18:48
 * @ClassName: FlowQuery
 * @Description:
 **/
@Data
public class FlowQuery extends PageQuery{
    private String flowName;
    private String flowId;
    private Integer id;
    private Integer disableMark;
}
