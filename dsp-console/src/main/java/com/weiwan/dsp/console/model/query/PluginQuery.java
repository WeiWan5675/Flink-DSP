package com.weiwan.dsp.console.model.query;

import com.weiwan.dsp.api.config.core.Configs;
import com.weiwan.dsp.api.enums.PluginType;
import lombok.Data;

import java.util.Map;

/**
 * @Author: xiaozhennan
 * @Date: 2021/11/4 16:05
 * @ClassName: PluginQuery
 * @Description:
 **/
@Data
public class PluginQuery extends PageQuery {
    private String id;
    private String pluginJarId;
    private String pluginJarName;
    private String pluginJarAlias;
    private Integer disableMark;
    private Integer pluginJarOrigin;
    
}
