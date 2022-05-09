package com.weiwan.dsp.console.model.dto;

import com.alibaba.fastjson.JSONObject;
import com.weiwan.dsp.api.config.core.PluginConfigs;
import com.weiwan.dsp.api.enums.PluginType;
import com.weiwan.dsp.common.config.ConfigOption;
import com.weiwan.dsp.common.utils.ObjectUtil;
import com.weiwan.dsp.console.model.entity.Plugin;
import com.weiwan.dsp.console.model.entity.User;
import lombok.Data;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author: xiaozhennan
 * @Date: 2021/11/3 0:09
 * @ClassName: PluginDTO
 * @Description:
 **/
@Data
public class PluginDTO {
    private Integer id;
    private String pluginJarId;
    private String pluginId;
    private String pluginName;
    private PluginType pluginType;
    private String pluginAlias;
    private String pluginClass;
    private String pluginDescription;
    private PluginConfigs pluginConfigs;
    private JSONObject pluginInfos;
    private Date createTime;
    private Date updateTime;


    public static PluginDTO fromPlugin(Plugin plugin) {
        PluginDTO pluginDTO = new PluginDTO();
        pluginDTO.setId(plugin.getId());
        pluginDTO.setPluginJarId(plugin.getPluginJarId());
        pluginDTO.setPluginId(plugin.getPluginId());
        pluginDTO.setPluginName(plugin.getPluginName());
        pluginDTO.setPluginType(PluginType.getPluginType(plugin.getPluginType()));
        pluginDTO.setPluginAlias(plugin.getPluginAlias());
        pluginDTO.setPluginClass(plugin.getPluginClass());
        pluginDTO.setPluginDescription(plugin.getPluginDescription());
        pluginDTO.setPluginConfigs(ObjectUtil.deSerialize(plugin.getPluginConfigs(), PluginConfigs.class));
        pluginDTO.setPluginInfos(ObjectUtil.deSerialize(plugin.getPluginInfos(), JSONObject.class));
        pluginDTO.setCreateTime(plugin.getCreateTime());
        pluginDTO.setUpdateTime(plugin.getUpdateTime());
        return pluginDTO;
    }
}
