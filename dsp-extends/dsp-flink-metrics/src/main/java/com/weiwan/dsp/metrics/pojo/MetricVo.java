package com.weiwan.dsp.metrics.pojo;

import com.weiwan.dsp.api.enums.MetricKey;
import com.weiwan.dsp.api.enums.MetricType;
import com.weiwan.dsp.api.enums.NodeType;

/**
 * @Author: xiaozhennan
 * @Date: 2021/10/31 12:46
 * @ClassName: MetricVo
 * @Description:
 **/
public class MetricVo {
    private String jobId;
    private String jobName;
    private String nodeId;
    private String nodeName;
    private MetricType metricType;
    private NodeType nodeType;
    private String metricsKey;
    private String metricsValue;

}
