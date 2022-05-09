package com.weiwan.dsp.console.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.weiwan.dsp.console.model.entity.ApplicationDeploy;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author: xiaozhennan
 * @description:
 */
public interface ApplicationDeployMapper extends BaseMapper<ApplicationDeploy> {
    @MapKey("appId")
    Map<Integer, ApplicationDeploy> selectDeployMapByAppIds(@Param("appIds") List<Integer> appIds);

}
