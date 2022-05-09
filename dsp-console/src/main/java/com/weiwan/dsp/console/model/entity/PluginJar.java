package com.weiwan.dsp.console.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("dsp_plugin_jar")
public class PluginJar {
    //自增ID
    private Integer id;
    //jar包id
    private String pluginJarId;
    //jar名称
    private String pluginJarName;
    //jar别名
    private String pluginJarAlias;
    //jar的MD5
    private String pluginJarMd5;
    //jar来源
    private Integer pluginJarOrigin;
    //jarUrl
    private String pluginJarUrl;
    //jar包定义文件名
    private String pluginDefFile;
    //jar包定义文件内容
    private String pluginDefFileContent;
    //禁用标识
    private Integer disableMark;
    //备注信息
    private String remarkMsg;
    //文件大小
    private Long fileSize;
    //上传时间
    private Date uploadTime;
    //创建用户
    private String createUser;
    //创建时间
    private Date createTime;
    //修改时间
    private Date updateTime;

}
