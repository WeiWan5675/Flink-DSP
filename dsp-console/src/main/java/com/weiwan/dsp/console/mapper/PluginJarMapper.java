package com.weiwan.dsp.console.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.weiwan.dsp.console.model.entity.PluginJar;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @Author: xiaozhennan
 * @Date: 2021/9/10 23:24
 * @Package: com.weiwan.dsp.console.mapper
 * @ClassName: DspPluginJarMapper
 * @Description:
 **/
public interface PluginJarMapper extends BaseMapper<PluginJar> {

    @MapKey("pluginJarId")
    Map<String, PluginJar> selectJarMapByJarIds(@Param("pluginJarIds") Set<String> pluginJarIds);

    @MapKey("plugin_jar_id")
    Map<String, Object> selectEnableJarMap();

    @MapKey("pluginJarName")
    Map<String, PluginJar> selectPluginJarNameMap();

    void insertBatch(@Param("newPluginJars") Collection<PluginJar> newPluginJars);
}
