package com.weiwan.dsp.console.model.vo;

import com.alibaba.fastjson.JSONObject;
import com.weiwan.dsp.api.config.core.PluginConfigs;
import com.weiwan.dsp.api.enums.PluginType;
import com.weiwan.dsp.common.utils.ObjectUtil;
import com.weiwan.dsp.console.model.entity.FlowPluginRef;
import com.weiwan.dsp.console.model.entity.Plugin;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author: xiaozhennan
 * @Date: 2022/2/15 14:50
 * @Package: com.weiwan.dsp.console.model.vo
 * @ClassName: PluginVo
 * @Description:
 **/
@Data
public class PluginVo {
    //自增ID
    private Integer id;
    //所属jar包id
    private String pluginJarId;
    //插件id
    private String pluginId;
    //插件名称
    private String pluginName;
    //插件类型
    private PluginType pluginType;
    //插件别名
    private String pluginAlias;
    //插件Class
    private String pluginClass;
    //插件引用的流
    private List<FlowPluginRef> flowPluginRefs;
    //插件备注
    private String pluginDescription;
    //插件配置模板
    private PluginConfigs pluginConfigs;
    //插件Infos
    private JSONObject pluginInfos;
    //创建时间
    private Date createTime;
    //修改时间
    private Date updateTime;

    /**
     * Plugin转换为PluginVo, 此处未查询flowRef
     *
     * @param plugin
     * @return
     */
    public static PluginVo fromPlugin(Plugin plugin) {
        PluginVo pluginVo = new PluginVo();
        pluginVo.setId(plugin.getId());
        pluginVo.setPluginJarId(plugin.getPluginJarId());
        pluginVo.setPluginId(plugin.getPluginId());
        pluginVo.setPluginName(plugin.getPluginName());
        pluginVo.setPluginAlias(plugin.getPluginAlias());
        pluginVo.setPluginType(PluginType.getPluginType(plugin.getPluginType()));
        pluginVo.setPluginClass(plugin.getPluginClass());
        pluginVo.setFlowPluginRefs(new ArrayList<FlowPluginRef>());
        pluginVo.setPluginDescription(plugin.getPluginDescription());
        PluginConfigs pluginConfigs = ObjectUtil.deSerialize(plugin.getPluginConfigs(), PluginConfigs.class);
        pluginVo.setPluginConfigs(pluginConfigs);
        JSONObject pluginInfos = ObjectUtil.deSerialize(plugin.getPluginInfos(), JSONObject.class);
        pluginVo.setPluginInfos(pluginInfos);
        pluginVo.setCreateTime(plugin.getCreateTime());
        pluginVo.setUpdateTime(plugin.getUpdateTime());
        return pluginVo;
    }
}
