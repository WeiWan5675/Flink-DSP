package com.weiwan.dsp.console.controller;

import com.weiwan.dsp.console.model.PageWrapper;
import com.weiwan.dsp.console.model.Result;
import com.weiwan.dsp.console.model.query.ScheduleTaskQuery;
import com.weiwan.dsp.console.model.vo.ScheduleTaskVo;
import com.weiwan.dsp.console.service.ScheduleTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @author Zhu Shaoqin
 * @email zsqmia@163.com
 * @date 2022/3/21 15:59
 * @description
 */
@RestController
@RequestMapping("schedule")
public class ScheduleTaskController {

    @Autowired
    private ScheduleTaskService scheduleTaskService;

    @PostMapping("list")
    public Result<PageWrapper<ScheduleTaskVo>> list(@RequestBody ScheduleTaskQuery jobQuery) {
        PageWrapper<ScheduleTaskVo> scheduleJobVoPageWrapper = scheduleTaskService.search(jobQuery);
        return Result.success(scheduleJobVoPageWrapper);
    }

    @PostMapping("create")
    public Result create(@RequestBody ScheduleTaskVo scheduleTaskVo) {
        scheduleTaskService.create(scheduleTaskVo, false);
        return Result.success("定时任务添加成功");
    }

    @PostMapping("delete")
    public Result delete(@RequestBody ScheduleTaskVo scheduleTaskVo) {
        scheduleTaskService.delete(scheduleTaskVo);
        return Result.success("定时任务删除成功");
    }

    @PostMapping("update")
    public Result update(@RequestBody ScheduleTaskVo scheduleTaskVo) {
        scheduleTaskService.update(scheduleTaskVo);
        return Result.success("定时任务修改成功");
    }

    @PostMapping("stop")
    public Result stop(@RequestBody ScheduleTaskVo scheduleTaskVo) {
        scheduleTaskService.stop(scheduleTaskVo);
        return Result.success("定时任务停止成功");
    }

    @PostMapping("resume")
    public Result resume(@RequestBody ScheduleTaskVo scheduleTaskVo) {
        scheduleTaskService.resume(scheduleTaskVo);
        return Result.success("定时任务恢复成功");
    }

    @PostMapping("load")
    public Result load(@RequestBody ScheduleTaskVo scheduleTaskVo) {
        scheduleTaskService.load(scheduleTaskVo);
        return Result.success("定时任务加载成功");
    }

    @PostMapping("unload")
    public Result unload(@RequestBody ScheduleTaskVo scheduleTaskVo) {
        scheduleTaskService.unload(scheduleTaskVo);
        return Result.success("定时任务卸载成功");
    }

    @PostMapping("reload")
    public Result reload(@RequestBody ScheduleTaskVo scheduleTaskVo) {
        scheduleTaskService.reload(scheduleTaskVo);
        return Result.success("定时任务重载成功");
    }

}
