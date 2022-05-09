package com.weiwan.dsp.console.service;

import com.weiwan.dsp.api.plugin.PluginConfig;
import com.weiwan.dsp.console.model.PageWrapper;
import com.weiwan.dsp.console.model.dto.PluginDTO;
import com.weiwan.dsp.console.model.entity.Plugin;
import com.weiwan.dsp.console.model.entity.PluginJar;
import com.weiwan.dsp.console.model.query.PluginQuery;
import com.weiwan.dsp.console.model.vo.PluginJarFileVo;
import com.weiwan.dsp.console.model.vo.PluginJarVo;
import com.weiwan.dsp.console.model.vo.PluginListVo;
import com.weiwan.dsp.console.model.vo.PluginVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author: xiaozhennan
 * @Date: 2021/9/10 23:24
 * @Package: com.weiwan.dsp.console.service.impl
 * @ClassName: PluginService
 * @Description:
 **/
public interface PluginService {
    void createPlugin(PluginJarVo pluginJarVo);

    boolean checkIsLoaded(PluginConfig pluginConfig);

    List<PluginDTO> searchByClass(Collection<String> pluginIds);

    PageWrapper<PluginJarVo> searchPlugin(PluginQuery query);

    void disablePlugin(PluginJarVo pluginJarVo);

    void deletePlugin(PluginJarVo pluginJarVo);

    PluginJarFileVo uploadPlugin(MultipartFile file, String pluginJarId);

    PluginJarVo verifyPlugin(PluginJarFileVo fileVo);

    void updatePart(PluginJarVo pluginJarVo);

    void updatePlugin(PluginJarVo pluginJarVo);

    PluginListVo searchAll();

    List<Plugin> findPluginByPluginIds(Set<String> pluginIds);

    Map<String, PluginJar> findPluginJarMapByJarIds(Set<String> pluginJarIds);

    Map<String, PluginDTO> findPluginMapByIds(Set<String> pluginIds);

    Map<String, PluginJar> findPluginJarNameMap();

    Map<String, Plugin> findPluginClassMap();

    void createJarsAndPlugins(List<PluginJar> newPluginJar, List<Plugin> newPlugin);
}
