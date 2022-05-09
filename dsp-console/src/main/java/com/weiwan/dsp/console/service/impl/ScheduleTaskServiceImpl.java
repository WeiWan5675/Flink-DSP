package com.weiwan.dsp.console.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.weiwan.dsp.api.config.console.TaskConfigs;
import com.weiwan.dsp.api.config.core.ScheduleConfig;
import com.weiwan.dsp.common.enums.DspResultStatus;
import com.weiwan.dsp.common.exception.DspConsoleException;
import com.weiwan.dsp.common.utils.CheckTool;
import com.weiwan.dsp.common.utils.MD5Utils;
import com.weiwan.dsp.common.utils.ObjectUtil;
import com.weiwan.dsp.console.mapper.ScheduleTaskMapper;
import com.weiwan.dsp.console.model.PageWrapper;
import com.weiwan.dsp.console.model.constant.ConsoleConstants;
import com.weiwan.dsp.console.model.dto.ApplicationDTO;
import com.weiwan.dsp.console.model.dto.ApplicationTimer;
import com.weiwan.dsp.console.model.entity.ScheduleTask;
import com.weiwan.dsp.console.model.query.PageQuery;
import com.weiwan.dsp.console.model.query.ScheduleTaskQuery;
import com.weiwan.dsp.console.model.vo.ScheduleTaskVo;
import com.weiwan.dsp.console.schedule.ConsoleTaskContext;
import com.weiwan.dsp.console.schedule.TaskType;
import com.weiwan.dsp.console.service.ScheduleTaskService;
import com.weiwan.dsp.console.service.UserService;
import com.weiwan.dsp.schedule.tasks.UserTask;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Zhu Shaoqin
 * @email zsqmia@163.com
 * @date 2022/3/21 15:40
 * @description
 */
@Service
public class ScheduleTaskServiceImpl implements ScheduleTaskService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleTaskServiceImpl.class);

    @Autowired
    private ScheduleTaskMapper scheduleTaskMapper;
    @Autowired
    private Scheduler scheduler;
    @Autowired
    private UserService userService;

    /**
     * 程序启动开始加载定时任务
     * job状态: 0停止, 1启动
     */
    @Override
    public void start() {
        List<ScheduleTask> tasks = new LambdaQueryChainWrapper<ScheduleTask>(scheduleTaskMapper)
                .eq(ScheduleTask::getTaskStatus, 1)
                .list();
        if (tasks == null || tasks.size() == 0) {
            logger.info("user schedule task data is empty");
            return;
        }
        for (ScheduleTask task : tasks) {
            JobDetail jobDetail = null;
            CronTrigger cronTrigger = null;
            try {
                jobDetail = getJobDetail(task);
                cronTrigger = getCronTrigger(task);
                scheduler.scheduleJob(jobDetail, cronTrigger);
                logger.info("schedule task loaded successfully task id {}", task.getTaskId());
            } catch (Exception e) {
                logger.error("schedule task failed to load task id: {}", task.getTaskId(), e);
            }
            try {
                scheduler.start();
            } catch (SchedulerException e) {
                logger.error("failed to start the scheduled task", e);
                throw new DspConsoleException(DspResultStatus.TASK_START_FAILED);
            }
        }
    }

    /**
     * 停止/暂停任务, job依旧是装载在调度器中, 只需要恢复就可以继续运行
     *
     * @param scheduleTaskVo
     */
    @Override
    public void stop(ScheduleTaskVo scheduleTaskVo) {
        CheckTool.checkIsNull(scheduleTaskVo, DspResultStatus.PARAM_IS_NULL);
        String taskId = scheduleTaskVo.getTaskId();
        ScheduleTask task = checkScheduleTask(taskId);
        if (task.getTaskStatus() == 0) {
            throw new DspConsoleException(DspResultStatus.TASK_IS_NOT_LOADED, "Task can't be stopped for it is not loaded, task id: " + taskId);
        }
        try {
            scheduler.pauseJob(JobKey.jobKey(taskId));
            logger.info("Schedule task stop successfully, task id: {}", taskId);
        } catch (SchedulerException e) {
            logger.error("Schedule task failed to stop, task id: {}", taskId, e);
            throw new DspConsoleException(DspResultStatus.TASK_STOP_FAILED, "Schedule task failed to stop, please try again, task id: " + taskId);
        }
    }


    @Override
    public void resume(ScheduleTaskVo scheduleTaskVo) {
        CheckTool.checkIsNull(scheduleTaskVo, DspResultStatus.PARAM_IS_NULL);
        String taskId = scheduleTaskVo.getTaskId();
        ScheduleTask task = checkScheduleTask(taskId);
        if (task.getTaskStatus() == 0) {
            throw new DspConsoleException(DspResultStatus.TASK_IS_NOT_LOADED, "任务未加载, 任务Id: " + taskId);
        }
        try {
            scheduler.resumeJob(JobKey.jobKey(taskId));
            logger.info("Schedule task is resumed successfully, task id: {}", taskId);
        } catch (SchedulerException e) {
            logger.error("Schedule task failed to resumed, task id: {}", taskId, e);
            throw new DspConsoleException(DspResultStatus.TASK_RESUME_FAILED, "Schedule task failed to resumed, task id: " + taskId);
        }
    }

    /**
     * 加载/启动新的job
     *
     * @param scheduleTaskVo
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void load(ScheduleTaskVo scheduleTaskVo) {
        CheckTool.checkIsNull(scheduleTaskVo, DspResultStatus.PARAM_IS_NULL);
        loadTask(scheduleTaskVo.getTaskId());
    }

    private void loadTask(String taskId) {
        //查询task记录, 判断status是否为0, 如果是0, 则改为1, 并加载, 如果是1, 则不加载, 打印警示日志
        ScheduleTask task = checkScheduleTask(taskId);
        if (task.getTaskStatus() == 0) {
            new LambdaUpdateChainWrapper<>(scheduleTaskMapper)
                    .set(ScheduleTask::getTaskStatus, 1)
                    .eq(ScheduleTask::getTaskId, taskId)
                    .update();
            try {
                JobDetail jobDetail = getJobDetail(task);
                CronTrigger cronTrigger = getCronTrigger(task);
                scheduler.scheduleJob(jobDetail, cronTrigger);
                logger.info("Schedule task is loaded successfully, task id: {}", taskId);
            } catch (Exception e) {
                logger.error("Schedule task failed to be loaded, task id: {}", task.getTaskId(), e);
                throw new DspConsoleException(DspResultStatus.TASK_LOAD_FAILED);
            }
        } else {
            logger.warn("Schedule task has already been loaded, task id: {}", task.getTaskId());
        }
    }

    /**
     * 重新加载执行计划
     *
     * @param scheduleTaskVo
     */
    @Override
    public void reload(ScheduleTaskVo scheduleTaskVo) {
        CheckTool.checkIsNull(scheduleTaskVo, DspResultStatus.PARAM_IS_NULL);
        ScheduleTask task = checkScheduleTask(scheduleTaskVo.getTaskId());
        //如果状态为0, 则说明任务没有加载, 也就不需要重载
        if (task.getTaskStatus() == 0) {
            throw new DspConsoleException(DspResultStatus.TASK_IS_NOT_LOADED);
        }
        reloadTask(task);
    }

    private void reloadTask(ScheduleTask task) {
        try {
            unloadJobFromScheduler(task.getTaskId());
            JobDetail jobDetail = getJobDetail(task);
            CronTrigger cronTrigger = getCronTrigger(task);
            scheduler.scheduleJob(jobDetail, cronTrigger);
            logger.info("Schedule task is reloaded successfully, task id: {}", task.getTaskId());
        } catch (Exception e) {
            logger.error("Schedule task failed to be reloaded, task id: {}", task.getTaskId(), e);
            throw new DspConsoleException(DspResultStatus.TASK_RELOAD_FAILED);
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unload(ScheduleTaskVo scheduleTaskVo) {
        CheckTool.checkIsNull(scheduleTaskVo, DspResultStatus.PARAM_IS_NULL);
        String taskId = scheduleTaskVo.getTaskId();
        ScheduleTask task = checkScheduleTask(taskId);
        //如果状态为0, 则说明任务没有加载, 也就不需要卸载
        if (task.getTaskStatus() == 0) {
            throw new DspConsoleException(DspResultStatus.TASK_IS_NOT_LOADED);
        }
        //status的值从1改为0
        new LambdaUpdateChainWrapper<ScheduleTask>(scheduleTaskMapper)
                .set(ScheduleTask::getTaskStatus, 0)
                .eq(ScheduleTask::getTaskId, taskId)
                .update();
        unloadJobFromScheduler(taskId);
    }

    @Override
    public PageWrapper<ScheduleTaskVo> search(ScheduleTaskQuery taskQuery) {
        //分页查询列表
        Page<ScheduleTask> page = new Page<>(taskQuery.getPageNo(), taskQuery.getPageSize());
        LambdaQueryChainWrapper<ScheduleTask> queryWrapper = getScheduleJobQueryWrapper(taskQuery);
        Page<ScheduleTask> taskPage = queryWrapper.page(page);
        List<ScheduleTask> tasks = new ArrayList<>();
        if (taskPage.getRecords() != null) {
            tasks = taskPage.getRecords();
        }
        //job转vo
        List<ScheduleTaskVo> taskVos = new ArrayList<>();
        for (ScheduleTask task : tasks) {
            ScheduleTaskVo taskVo = new ScheduleTaskVo();
            taskVo.setId(task.getId());
            taskVo.setTaskId(task.getTaskId());
            taskVo.setTaskName(task.getTaskName());
            taskVo.setTaskType(TaskType.getTaskType(task.getTaskType()));
            taskVo.setTaskConfigs(ObjectUtil.deSerialize(task.getTaskConfigs(), TaskConfigs.class));
            taskVo.setTaskDesc(task.getTaskDesc());
            taskVo.setTaskCronExpr(task.getTaskCronExpr());
            taskVo.setTaskStatus(task.getTaskStatus());
            taskVo.setCreateUser(task.getCreateUser());
            taskVo.setCreateTime(task.getCreateTime());
            taskVo.setUpdateTime(task.getUpdateTime());
            taskVos.add(taskVo);
        }
        return new PageWrapper<ScheduleTaskVo>(taskVos, taskPage.getTotal(), taskPage.getCurrent(), taskPage.getPages());
    }

    @Override
    public Integer create(ScheduleTaskVo scheduleTaskVo, boolean isLoad) {
        //生成jobId md5(taskName + class + 时间戳)
        long time = System.currentTimeMillis();
        String taskId = MD5Utils.md5(String.format("%s-%s", scheduleTaskVo.getTaskName(), time));
        //生成ScheduleJob
        ScheduleTask scheduleTask = new ScheduleTask();
        scheduleTask.setTaskId(taskId);
        scheduleTask.setTaskName(scheduleTaskVo.getTaskName());
        scheduleTask.setTaskType(scheduleTaskVo.getTaskType().getCode());
        scheduleTask.setTaskClass(scheduleTaskVo.getTaskClass());
        scheduleTask.setTaskConfigs(ObjectUtil.serialize(scheduleTaskVo.getTaskConfigs()));
        scheduleTask.setTaskDesc(scheduleTaskVo.getTaskDesc());
        scheduleTask.setTaskCronExpr(scheduleTaskVo.getTaskCronExpr());
        scheduleTask.setTaskStatus(0);
        String username = userService.getCurrentUser().getUsername();
        scheduleTask.setCreateUser(username);
        scheduleTask.setCreateTime(new Date());
        ScheduleTask task = createScheduleTask(scheduleTask, isLoad);
        logger.info("Schedule task is created successfully, task id: {}", task.getTaskId());
        return task.getId();
    }

    private ScheduleTask createScheduleTask(ScheduleTask task, boolean isLoad) {
        //校验参数是否为空
        CheckTool.checkIsNull(task.getTaskType(), DspResultStatus.PARAM_IS_NULL);
        String taskName = task.getTaskName();
        String cronExpr = task.getTaskCronExpr();
        if (StringUtils.isBlank(taskName) || StringUtils.isBlank(cronExpr)) {
            throw new DspConsoleException(DspResultStatus.PARAM_IS_NULL);
        }
        //校验jobName是否已存在
        ScheduleTask checkTaskName = new LambdaQueryChainWrapper<>(scheduleTaskMapper)
                .eq(ScheduleTask::getTaskName, taskName)
                .one();
        CheckTool.checkIsNotNull(checkTaskName, DspResultStatus.TASK_EXISTS);
        //存入数据库
        scheduleTaskMapper.insert(task);
        if (isLoad) {
            loadTask(task.getTaskId());
        }
        return new LambdaQueryChainWrapper<>(scheduleTaskMapper)
                .eq(ScheduleTask::getTaskId, task.getTaskId())
                .one();
    }

    public ScheduleTask createAppTask(ApplicationTimer appTimer, boolean isLoad) {
        ScheduleTask task = new ScheduleTask();
        //生成TaskName = "jobId-timerType"
        String taskName = String.format("%s-%s", appTimer.getJobId(), appTimer.getTimerType().getType());
        //生成taskId md5(taskName)
        String taskId = MD5Utils.md5(taskName);
        task.setTaskId(taskId);
        task.setTaskName(taskName);
        task.setTaskType(TaskType.SYSTEM_TASK.getCode());
        task.setTaskClass(appTimer.getTimerType().getTimerClass().getName());
        task.setTaskDesc(String.format("App ID: %s, Timer Type: %s", appTimer.getJobId(), appTimer.getTimerType().getType()));
        task.setTaskCronExpr(appTimer.getTimerCron());
        task.setTaskStatus(0);
        String username = userService.getCurrentUser().getUsername();
        task.setCreateUser(username);
        task.setCreateTime(new Date());
        TaskConfigs taskConfigs = new TaskConfigs();
        taskConfigs.setStringVal("jobId", appTimer.getJobId());
        task.setTaskConfigs(ObjectUtil.serialize(taskConfigs));
        ScheduleTask scheduleTask = createScheduleTask(task, isLoad);
        logger.info("Application schedule task is created successfully, task id: {}", scheduleTask.getTaskId());
        return scheduleTask;
    }

    @Override
    public void delete(ScheduleTaskVo scheduleTaskVo) {
        //参数判空
        CheckTool.checkIsNull(scheduleTaskVo, DspResultStatus.PARAM_IS_NULL);
        //查询数据库中的task, 判断状态, 如果为1, 要先卸载任务, 才可以删除
        String taskId = scheduleTaskVo.getTaskId();
        ScheduleTask task = checkScheduleTask(taskId);
        if (task.getTaskStatus() == 1) {
            unloadJobFromScheduler(taskId);
        }
        //删除task
        new LambdaUpdateChainWrapper<>(scheduleTaskMapper)
                .eq(ScheduleTask::getTaskId, taskId)
                .remove();
        logger.info("Schedule task is deleted successfully, task id: {}", taskId);
    }

    @Override
    public void deleteBatch(List<Integer> taskIds) {
        CheckTool.checkIsNull(taskIds, DspResultStatus.PARAM_IS_NULL);
        //查询数据库中的task, 判断状态, 如果为1, 要先卸载任务, 才可以删除
        List<ScheduleTask> tasks = scheduleTaskMapper.selectBatchIds(taskIds);
        for (ScheduleTask task : tasks) {
            if (task.getTaskStatus() == 1) {
                unloadJobFromScheduler(task.getTaskId());
            }
        }
        //删除task记录
        scheduleTaskMapper.deleteBatchIds(taskIds);
        logger.info("Schedule tasks are deleted successfully, task ids: {}", taskIds);
    }

    /**
     * 根据jobId修改type,configs,desc,cron
     *
     * @param scheduleTaskVo
     */
    @Override
    public void update(ScheduleTaskVo scheduleTaskVo) {
        //参数和jobId判空
        CheckTool.checkIsNull(scheduleTaskVo, DspResultStatus.PARAM_IS_NULL);
        String taskId = scheduleTaskVo.getTaskId();
        ScheduleTask task = checkScheduleTask(taskId);
        task.setTaskConfigs(ObjectUtil.serialize(scheduleTaskVo.getTaskConfigs()));
        task.setTaskDesc(scheduleTaskVo.getTaskDesc());
        task.setTaskCronExpr(scheduleTaskVo.getTaskCronExpr());
        //更新数据库
        new LambdaUpdateChainWrapper<>(scheduleTaskMapper)
                .eq(ScheduleTask::getTaskId, taskId)
                .update(task);
        //检查task的状态, 如果为启用, 则更新完后需要reload
        if (task.getTaskStatus() == 1) {
            reloadTask(task);
        }
        logger.info("Schedule task is updated successfully, task id: {}", taskId);
    }

    @Override
    public void updateAppTask(ApplicationDTO applicationDTO) {
        CheckTool.checkIsNull(applicationDTO.getJobId(), DspResultStatus.PARAM_IS_NULL);
        CheckTool.checkIsNull(applicationDTO.getScheduleConfig(), DspResultStatus.PARAM_IS_NULL);

        //1. 用jobId查出task表中的记录(map)
        List<String> taskNames = new ArrayList<>();
        for (ApplicationTimer.ApplicationTimerType value : ApplicationTimer.ApplicationTimerType.values()) {
            String taskName = String.format("%s-%s", applicationDTO.getJobId(), value.getType());
            taskNames.add(taskName);
        }
        Map<String, ScheduleTask> appTaskMap = scheduleTaskMapper.selectAppTaskMapByTaskNames(taskNames);

        //2. 获取dto中的scheduleConfig, 并转换为ApplicationTimer
        ScheduleConfig scheduleConfig = applicationDTO.getScheduleConfig();
        for (ApplicationTimer.ApplicationTimerType value : ApplicationTimer.ApplicationTimerType.values()) {
            ApplicationTimer timer = scheduleConfig.getVal(value.getType(), ApplicationTimer.class);
            //从map中尝试获取task
            ScheduleTask appTask = appTaskMap.get(timer.getTimerType().getTimerClass().getName());
            //如果appTask为空, 则说明没有该任务, 于是需要检查timer的enable值, 如果为true, 则需要添加, 否则不添加
            if (appTask == null) {
                if (timer.getEnableTimer()) {
                    //添加task
                    createAppTask(timer, true);
                }
            } else {
                if (timer.getEnableTimer()) {
                    //对比cron
                    if (!timer.getTimerCron().equals(appTask.getTaskCronExpr())) {
                        //更新task
                        new LambdaUpdateChainWrapper<>(scheduleTaskMapper)
                                .set(ScheduleTask::getTaskCronExpr, timer.getTimerCron())
                                .eq(ScheduleTask::getId, appTask.getId())
                                .update();
                        //获取最新task, 进行reload
                        ScheduleTask task = checkScheduleTask(appTask.getTaskId());
                        reloadTask(task);
                    }
                } else {
                    //卸载task
                    unloadJobFromScheduler(appTask.getTaskId());
                    //删除task
                    scheduleTaskMapper.deleteById(appTask.getId());
                }
            }
        }
        logger.info("Application schedule task is updated successfully, job id: {}", applicationDTO.getJobId());
    }

    /**
     * 从调度器中卸载任务
     *
     * @param taskId
     */
    private void unloadJobFromScheduler(String taskId) {
        // 获取以前的触发器
        TriggerKey triggerKey = TriggerKey.triggerKey(taskId);
        try {
            // 停止触发器
            scheduler.pauseTrigger(triggerKey);
            // 删除触发器
            scheduler.unscheduleJob(triggerKey);
            // 删除原来的job
            scheduler.deleteJob(JobKey.jobKey(taskId));
            logger.info("Schedule task is unloaded successfully, task id: {}", taskId);
        } catch (Exception e) {
            logger.error("Schedule task failed to be unloaded, task id: {}", taskId, e);
            throw new DspConsoleException(DspResultStatus.TASK_UNLOAD_FAILED);
        }
    }

    //组装JobDetail
    private JobDetail getJobDetail(ScheduleTask task) throws ClassNotFoundException {
        Class jobClass = Class.forName(task.getTaskClass());
        if (UserTask.class.isAssignableFrom(jobClass)) {
            jobClass = Class.forName(ConsoleConstants.SCHEDULE_USER_CUSTOM_TASK_CLASS).asSubclass(Job.class);
        } else if (Job.class.isAssignableFrom(jobClass)) {
            jobClass = jobClass.asSubclass(Job.class);
        } else {
            logger.error("JobDetail生成失败, job class: {}", jobClass);
        }

        ConsoleTaskContext<ScheduleTask> consoleTaskContext = new ConsoleTaskContext<>();
        try {
            TaskConfigs taskConfigs = ObjectUtil.deSerialize(task.getTaskConfigs(), TaskConfigs.class);
            consoleTaskContext.setTaskConfigs(taskConfigs);
            consoleTaskContext.setScheduleJob(task);
        } catch (Exception e) {
            e.printStackTrace();
        }
        consoleTaskContext.setTaskType(TaskType.getTaskType(task.getTaskType()));

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(ConsoleConstants.JOB_KEY, consoleTaskContext);
        return JobBuilder.newJob()
                .withIdentity(JobKey.jobKey(task.getTaskId()))
                .setJobData(jobDataMap)
                .ofType(jobClass)
                .build();
    }

    //组装CronTrigger
    private CronTrigger getCronTrigger(ScheduleTask task) {
        CronTrigger cronTrigger = null;
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(task.getTaskCronExpr());
        cronTrigger = TriggerBuilder.newTrigger()
                .withIdentity(TriggerKey.triggerKey(task.getTaskId()))
                .withSchedule(cronScheduleBuilder)
                .build();
        return cronTrigger;
    }

    private LambdaQueryChainWrapper<ScheduleTask> getScheduleJobQueryWrapper(ScheduleTaskQuery taskQuery) {
        LambdaQueryChainWrapper<ScheduleTask> wrapper = new LambdaQueryChainWrapper<ScheduleTask>(scheduleTaskMapper);
        if (StringUtils.isNotBlank(taskQuery.getId())) {
            wrapper.eq(ScheduleTask::getId, taskQuery.getId());
        }
        if (StringUtils.isNotBlank(taskQuery.getTaskId())) {
            wrapper.eq(ScheduleTask::getTaskId, taskQuery.getTaskId());
        }
        if (StringUtils.isNotBlank(taskQuery.getTaskName())) {
            wrapper.eq(ScheduleTask::getTaskName, taskQuery.getTaskName());
        }
        if (taskQuery.getTaskType() != null) {
            wrapper.eq(ScheduleTask::getTaskType, taskQuery.getTaskType());
        }
        if (taskQuery.getTaskStatus() != null) {
            wrapper.eq(ScheduleTask::getTaskStatus, taskQuery.getTaskStatus());
        }

        String sortField = taskQuery.getSortField();
        PageQuery.SortOrder sortOrder = taskQuery.getSortOrder();
        if (StringUtils.isNotBlank(sortField)) {
            if (sortOrder == PageQuery.SortOrder.descend) {
                wrapper.orderByDesc(ScheduleTask::getCreateTime);
            } else {
                wrapper.orderByAsc(ScheduleTask::getCreateTime);
            }
        } else {
            wrapper.orderByDesc(ScheduleTask::getCreateTime);
        }
        return wrapper;
    }

    private ScheduleTask checkScheduleTask(String taskId) {
        if (StringUtils.isBlank(taskId)) {
            throw new DspConsoleException(DspResultStatus.PARAM_IS_NULL, "Task id is null");
        }
        ScheduleTask task = new LambdaQueryChainWrapper<>(scheduleTaskMapper)
                .eq(ScheduleTask::getTaskId, taskId)
                .one();
        CheckTool.checkIsNull(task, DspResultStatus.TASK_NOT_EXIST, "Task is not found, task id: " + taskId);
        return task;
    }
}
