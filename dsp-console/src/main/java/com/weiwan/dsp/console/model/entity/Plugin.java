package com.weiwan.dsp.console.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("dsp_plugin")
public class Plugin {
    //自增ID
    private Integer id;
    //所属jar包id
    private String pluginJarId;
    //插件id
    private String pluginId;
    //插件名称
    private String pluginName;
    //插件类型
    private Integer pluginType;
    //插件别名
    private String pluginAlias;
    //插件Class
    private String pluginClass;
    //插件备注
    private String pluginDescription;
    //插件配置模板
    private String pluginConfigs;
    //插件UIInfos
    private String pluginInfos;
    //创建时间
    private Date createTime;
    //修改时间
    private Date updateTime;
}
