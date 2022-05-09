package com.weiwan.dsp.console.model.vo;

import com.weiwan.dsp.api.config.core.ReferenceConfigs;
import com.weiwan.dsp.api.enums.ConfigCategory;
import com.weiwan.dsp.api.enums.ConfigType;
import lombok.Data;

import java.util.Date;

@Data
public class ConfigVo {
    private Integer id;
    private String configId;
    private String configName;
    private ConfigCategory configCategory;
    private ConfigType configType;
    private ReferenceConfigs configMap;
    private Integer disableMark;
    private Integer usingMark;
    private String remarkMsg;
    private String createUser;
    private Date createTime;
    private Date updateTime;
}
