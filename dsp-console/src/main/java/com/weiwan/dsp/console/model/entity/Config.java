package com.weiwan.dsp.console.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("dsp_config")
public class Config {
    private Integer id;
    private String configId;
    private String configName;
    private Integer configCategory;
    private Integer configType;
    private String configMap;
    private Integer disableMark;
    private String remarkMsg;
    private String createUser;
    private Date createTime;
    private Date updateTime;
}
