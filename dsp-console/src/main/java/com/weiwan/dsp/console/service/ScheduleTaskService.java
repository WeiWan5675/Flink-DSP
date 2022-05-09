package com.weiwan.dsp.console.service;

import com.weiwan.dsp.api.config.core.ScheduleConfig;
import com.weiwan.dsp.console.model.PageWrapper;
import com.weiwan.dsp.console.model.dto.ApplicationDTO;
import com.weiwan.dsp.console.model.dto.ApplicationTimer;
import com.weiwan.dsp.console.model.entity.ScheduleTask;
import com.weiwan.dsp.console.model.query.ScheduleTaskQuery;
import com.weiwan.dsp.console.model.vo.ScheduleTaskVo;

import java.util.List;

/**
 * @author Zhu Shaoqin
 * @email zsqmia@163.com
 * @date 2022/3/21 15:32
 * @description
 */
public interface ScheduleTaskService {


    /**
     * 程序启动开始加载定时任务
     */
    void start();

    /**
     * 停止任务
     *
     * @param scheduleTaskVo
     */
    void stop(ScheduleTaskVo scheduleTaskVo);

    /**
     * 恢复任务
     *
     * @param scheduleTaskVo
     */
    void resume(ScheduleTaskVo scheduleTaskVo);

    /**
     * 添加新任务
     *
     * @param scheduleTaskVo
     */
    void load(ScheduleTaskVo scheduleTaskVo);

    /**
     * 卸载任务
     *
     * @param scheduleTaskVo
     */
    void unload(ScheduleTaskVo scheduleTaskVo);

    /**
     * 重新加载任务
     *
     * @param scheduleTaskVo
     */
    void reload(ScheduleTaskVo scheduleTaskVo);

    Integer create(ScheduleTaskVo scheduleTaskVo, boolean isLoad);

    ScheduleTask createAppTask(ApplicationTimer appTimer, boolean isLoad);

    PageWrapper<ScheduleTaskVo> search(ScheduleTaskQuery jobQuery);

    void delete(ScheduleTaskVo scheduleTaskVo);

    void update(ScheduleTaskVo scheduleTaskVo);

    void deleteBatch(List<Integer> taskIds);

    void updateAppTask(ApplicationDTO applicationDTO);
}
