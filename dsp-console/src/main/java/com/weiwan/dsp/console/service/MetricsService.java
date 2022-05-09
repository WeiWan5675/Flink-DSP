package com.weiwan.dsp.console.service;

import com.weiwan.dsp.console.model.query.MetricQuery;
import com.weiwan.dsp.console.model.vo.ApplicationOverviewVo;

/**
 * @Author: xiaozhennan
 * @Date: 2021/11/14 17:19
 * @ClassName: MetricsService
 * @Description:
 **/
public interface MetricsService {

    ApplicationOverviewVo getApplicationOverview(MetricQuery metricQuery);
}
