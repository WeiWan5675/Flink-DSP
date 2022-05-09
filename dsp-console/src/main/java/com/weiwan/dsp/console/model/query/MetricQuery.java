package com.weiwan.dsp.console.model.query;

import lombok.Data;

import java.util.Date;

/**
 * @Author: xiaozhennan
 * @Date: 2021/11/14 17:21
 * @ClassName: MetricQuery
 * @Description:
 **/
@Data
public class MetricQuery {
    private Date startTime;
    private Date endTime;
    //-1 全部应用
    private Integer appId = -1;
}
