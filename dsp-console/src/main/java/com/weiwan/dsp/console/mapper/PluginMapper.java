package com.weiwan.dsp.console.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.weiwan.dsp.console.model.entity.Plugin;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author: xiaozhennan
 * @Date: 2022/2/15 15:32
 * @Package: com.weiwan.dsp.console.mapper
 * @ClassName: DspPluginMapper
 * @Description:
 **/
public interface PluginMapper extends BaseMapper<Plugin> {
    void insertBatch(@Param("plugins") Collection<Plugin> plugins);

    void updateBatchByClass(@Param("plugins") Collection<Plugin> updateClassMap);

    @MapKey("pluginId")
    Map<String, Plugin> selectMapByIds(@Param("pluginIds") Set<String> pluginIds);

    @MapKey("pluginClass")
    Map<String, Plugin> selectMapByJarId(@Param("pluginJarId") String pluginJarId);

    void deleteBatchByClass(@Param("deleteClasses") List<String> deleteClasses);

    @MapKey("pluginClass")
    Map<String, Plugin> selectPluginClassMap();
}
