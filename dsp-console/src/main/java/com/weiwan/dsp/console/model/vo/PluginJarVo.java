package com.weiwan.dsp.console.model.vo;

import com.weiwan.dsp.api.enums.PluginJarOrigin;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author: xiaozhennan
 * @Date: 2021/11/4 16:05
 * @ClassName: PluginVo
 * @Description:
 **/
@Data
public class PluginJarVo {
    private Integer id;
    private String pluginJarId;
    private String pluginJarName;
    private String pluginJarAlias;
    private String pluginJarMd5;
    private PluginJarOrigin pluginJarOrigin;
    private String pluginJarUrl;
    private String pluginDefFile;
    private String pluginDefFileContent;
    private Integer disableMark;
    private String remarkMsg;
    private Long fileSize;
    private Date uploadTime;
    private Date createTime;
    private Date updateTime;
    private List<PluginVo> plugins;
}
