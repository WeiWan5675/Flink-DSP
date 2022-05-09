package com.weiwan.dsp.console.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.weiwan.dsp.console.model.entity.ScheduleTask;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author Zhu Shaoqin
 * @email zsqmia@163.com
 * @date 2022/3/21 14:37
 * @description
 */
@Mapper
public interface ScheduleTaskMapper extends BaseMapper<ScheduleTask> {
    void deleteBatchByTaskIds(@Param("taskIds") List<Integer> taskIds);

    @MapKey("taskClass")
    Map<String, ScheduleTask> selectAppTaskMapByTaskNames(@Param("taskNames") List<String> taskNames);
}
