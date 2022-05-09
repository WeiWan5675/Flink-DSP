package com.weiwan.dsp.console.model.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("dsp_unresolved_log")
public class UnresolvedLog {
    private Integer id;
    private String jobId;
    private String jobName;
    private String nodeId;
    private String nodeName;
    private Integer nodeType;
    private String unresolvedSource;
    private String unresolvedType;
    private String unresolvedMsg;
    private String unresolvedDate;
    private Date unresolvedTime;
    private Date createTime;
}
