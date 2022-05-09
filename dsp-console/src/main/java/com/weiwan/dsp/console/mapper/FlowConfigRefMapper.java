package com.weiwan.dsp.console.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.weiwan.dsp.console.model.entity.FlowConfigRef;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

public interface FlowConfigRefMapper extends BaseMapper<FlowConfigRef> {
    List<Integer> checkReferenceExist(@Param("configPks") Collection<Integer> configPks);

    void updateBatchByConfigPk(@Param("configPk") Integer configPk, @Param("configName") String configName);
}
