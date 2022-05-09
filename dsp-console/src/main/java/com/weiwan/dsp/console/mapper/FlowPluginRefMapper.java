package com.weiwan.dsp.console.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.weiwan.dsp.console.model.entity.FlowPluginRef;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface FlowPluginRefMapper extends BaseMapper<FlowPluginRef> {
    void insertBatch(@Param("flowPluginRefs") List<FlowPluginRef> flowPluginRefs);

    List<Integer> selectFlowPksByJarId(@Param("pluginJarId") String pluginJarId);

    @MapKey("pluginClass")
    Map<String, FlowPluginRef> selectPluginClassMapByJarId(@Param("pluginJarId") String pluginJarId);
}
