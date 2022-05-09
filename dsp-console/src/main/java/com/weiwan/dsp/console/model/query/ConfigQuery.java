package com.weiwan.dsp.console.model.query;

import com.weiwan.dsp.api.enums.ConfigCategory;
import com.weiwan.dsp.api.enums.ConfigType;
import lombok.Data;

@Data
public class ConfigQuery extends PageQuery{
    private Integer id;
    private String configId;
    private String configName;
    private ConfigCategory[] configCategory;
    private ConfigType[] configType;
    private Integer disableMark;
}
