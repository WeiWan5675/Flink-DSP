package com.weiwan.dsp.console.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author: xiaozhennan
 * @Date: 2022/3/26 22:31
 * @Package: com.weiwan.dsp.console.model.vo
 * @ClassName: PluginListVo
 * @Description:
 **/

@Data
public class PluginListVo {
    private List<PluginVo> inputPlugins;

    private List<PluginVo> processPlugins;

    private List<PluginVo> outputPlugins;

    private List<PluginVo> splitPlugins;

    private List<PluginVo> unionPlugins;

    private List<PluginVo> systemPlugins;

    private List<PluginVo> unknownPlugins;
}
