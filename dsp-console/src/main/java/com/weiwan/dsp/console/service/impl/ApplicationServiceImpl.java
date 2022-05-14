package com.weiwan.dsp.console.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.weiwan.dsp.api.config.core.*;
import com.weiwan.dsp.api.enums.ApplicationState;
import com.weiwan.dsp.api.enums.ApplicationType;
import com.weiwan.dsp.common.enums.DspResultStatus;
import com.weiwan.dsp.common.exception.DspConsoleException;
import com.weiwan.dsp.common.utils.CheckTool;
import com.weiwan.dsp.common.utils.ObjectUtil;
import com.weiwan.dsp.console.mapper.ApplicationMapper;
import com.weiwan.dsp.console.model.PageWrapper;
import com.weiwan.dsp.console.model.dto.ApplicationDTO;
import com.weiwan.dsp.console.model.dto.ApplicationDeployDTO;
import com.weiwan.dsp.console.model.dto.ApplicationTimer;
import com.weiwan.dsp.console.model.dto.FlowDTO;
import com.weiwan.dsp.console.model.entity.Application;
import com.weiwan.dsp.console.model.entity.ScheduleTask;
import com.weiwan.dsp.console.model.entity.User;
import com.weiwan.dsp.console.model.query.ApplicationQuery;
import com.weiwan.dsp.console.model.query.PageQuery;
import com.weiwan.dsp.console.model.vo.*;
import com.weiwan.dsp.console.service.*;
import com.weiwan.dsp.core.pub.JobID;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: xiaozhennan
 * @description:
 */
@Service
public class ApplicationServiceImpl extends ServiceImpl<ApplicationMapper, Application>
        implements ApplicationService {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationServiceImpl.class);
    @Autowired
    private ApplicationDeployService deployService;
    @Autowired
    private ApplicationMapper applicationMapper;
    @Autowired
    @Lazy
    private FlowService flowService;
    @Autowired
    private UserService userService;
    @Autowired
    private ApplicationResourceService resourceService;
    @Autowired
    private ApplicationFlowService applicationFlowService;
    @Autowired
    private ApplicationDeployService applicationDeployService;
    @Autowired
    private ScheduleTaskService scheduleTaskService;

    @Override
    @Transactional
    public Application createApp(ApplicationDTO appDto) {
        //1. 校验application是否存在以及是否完整
        CheckTool.checkNotNull(appDto, DspResultStatus.PARAM_IS_NULL);

        ApplicationDTO dto = appDto;
        //2. 校验应用名称
        String appName = appDto.getAppName();
        CheckTool.checkNotNull(appName, DspResultStatus.APPLICATION_NAME_NULL);

        //校验是否已经存在同名应用
        Application dbApplication = new LambdaQueryChainWrapper<>(applicationMapper)
                .eq(Application::getAppName, appDto.getAppName()).one();

        CheckTool.checkIsNotNull(dbApplication, DspResultStatus.APPLICATION_NAME_EXIST);
        //校验应用类型
        ApplicationType applicationType = appDto.getAppType();
        CheckTool.checkNotNull(applicationType, DspResultStatus.APPLICATION_TYPE_NULL);

        //校验流程ID
        Integer flowId = appDto.getFlowId();
        CheckTool.checkNotNull(flowId, DspResultStatus.APPLICATION_FLOW_ID_NULL);
        FlowDTO flowDTO = flowService.searchFlow(flowId);

        CheckTool.checkNotNull(flowDTO, DspResultStatus.APPLICATION_FLOW_NOT_EXIST);

        if (flowDTO.getDisableMark() == 1) {
            //流程处于被禁用状态
            throw new DspConsoleException(DspResultStatus.FLOW_DISABLED, "关联的流程处于被禁用状态");
        }


        CoreConfig coreConfig = dto.getCoreConfig();
        EngineConfig engineConfig = coreConfig.getEngineConfig();
        CheckTool.checkNotNull(coreConfig, DspResultStatus.APPLICATION_CORE_CONFIG_NOT_EXIST);
        CheckTool.checkNotNull(engineConfig, DspResultStatus.APPLICATION_ENGINE_CONFIG_NOT_EXIST);

        //设置创建用户
        User currentUser = userService.getCurrentUser();
        //设置更新和创建时间(数据库字段也会自动赋值)
        dto.setUserId(currentUser.getId().intValue());
        dto.setFlowId(flowDTO.getId());
        dto.setFlowName(flowDTO.getFlowName());
        dto.setFlowDTO(flowDTO);
        dto.setApplicationState(ApplicationState.INIT);
        //生成JOBID
        JobID jobID = resourceService.generateJobID(dto.getAppName());
        dto.setJobId(jobID.getJobId());

        Application application = ApplicationDTO.toDspApplication(dto);
        //保存
        save(application);

        application = new LambdaQueryChainWrapper<>(applicationMapper)
                .eq(Application::getJobId, application.getJobId()).one();
        ApplicationDTO applicationDTO = ApplicationDTO.fromDspApplication(application);
        applicationDTO.setFlowDTO(flowDTO);

        //保存应用和流程关系
        applicationFlowService.save(applicationDTO);

        ApplicationDeployDTO deployDTO = deployService.createDeploy(applicationDTO);

        //检查app定时任务数量
        ScheduleConfig scheduleConfig = appDto.getScheduleConfig();
        for (Object timerObj : scheduleConfig.values()) {
            ApplicationTimer timer = (ApplicationTimer) timerObj;
            if (timer.getEnableTimer()) {
                timer.setJobId(application.getJobId());
                ScheduleTask appTask = scheduleTaskService.createAppTask(timer, true);
                timer.setTaskId(appTask.getId());
            }
        }
        application.setScheduleConfig(ObjectUtil.serialize(scheduleConfig));
        application.setDeployId(deployDTO.getId());
        application.setJobConfig(ObjectUtil.serialize(deployDTO.getDspContext().getDsp().getJob()));
        boolean update = new LambdaUpdateChainWrapper<>(applicationMapper)
                .eq(Application::getId, application.getId())
                .update(application);

        //从数据库查询最新的application
        application = new LambdaQueryChainWrapper<>(applicationMapper)
                .eq(Application::getJobId, dto.getJobId()).one();
        return application;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteApp(Integer appId) {
        CheckTool.checkIsNull(appId, DspResultStatus.PARAM_IS_NULL);
        Application application = new LambdaQueryChainWrapper<>(applicationMapper)
                .eq(Application::getId, appId)
                .one();

        //1. 如果是运行状态,需要先停止运行然后删除
        Integer appState = application.getAppState();
        if (!ApplicationState.isFinalState(ApplicationState.getApplicationState(appState))) {
            throw DspConsoleException.generateIllegalStateException(DspResultStatus.APPLICATION_DEPLOY_RUNNING);
        }
        //2. 删除应用
        applicationMapper.deleteById(appId);
        //3. 删除应用和流的关联关系
        applicationFlowService.deleteByAppId(appId);
        //4. 查询应用的部署记录, 删除应用的部署记录
        applicationDeployService.deleteDeploy(application);
        //5. 查询应用的定时任务, 如果有定时任务, 根据taskId删除task表中相关记录
        List<Integer> taskIds = new ArrayList<>();
        ScheduleConfig scheduleConfig = ObjectUtil.deSerialize(application.getScheduleConfig(), ScheduleConfig.class);
        for (ApplicationTimer.ApplicationTimerType value : ApplicationTimer.ApplicationTimerType.values()) {
            ApplicationTimer timer = scheduleConfig.getVal(value.getType(), ApplicationTimer.class);
            if (timer.getEnableTimer()) {
                taskIds.add(timer.getTaskId());
            }
        }
        if (taskIds.size() > 0) {
            scheduleTaskService.deleteBatch(taskIds);
        }
        return true;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateApp(ApplicationDTO dto) {
        try {
            //校验是否已经存在同名应用
            Application dspApplication = new LambdaQueryChainWrapper<>(applicationMapper)
                    .eq(Application::getId, dto.getId()).one();
            if (dspApplication == null) {
                createApp(dto);
                return;
            }
            // 查询流程
            FlowVo flowVO = flowService.findFlowById(dto.getFlowId());
            if (flowVO == null || flowVO.getDisableMark() == 1) {
                throw DspConsoleException.generateIllegalStateException(DspResultStatus.FLOW_DISABLED);
            }
            FlowDTO flowDTO = FlowDTO.fromFlowVo(flowVO);
            dto.setFlowDTO(flowDTO);
            dto.setFlowId(flowDTO.getId());
            dto.setFlowName(flowDTO.getFlowName());

            // 更新部署表
            deployService.updateDeploy(dto);

            //更新流程引用关系表
            applicationFlowService.update(dto);

            //更新定时任务表
            scheduleTaskService.updateAppTask(dto);

            //保存更新应用
            Application application = ApplicationDTO.toDspApplication(dto);
            updateById(application);
        } catch (Exception e) {
            DspConsoleException.generateIllegalStateException(DspResultStatus.UPDATE_DEPLOY_CONFIG_FAIL);
        }
    }

    @Override
    public PageWrapper<ApplicationVO> searchApp(ApplicationQuery applicationQuery) {
        Page<Application> page = new Page<>(applicationQuery.getPageNo(), applicationQuery.getPageSize());
        LambdaQueryChainWrapper<Application> queryWrapper = getApplicationQueryWrapper(applicationQuery);
        Page<Application> appPage = queryWrapper.page(page);
        List<Application> records = appPage.getRecords();
        List<ApplicationVO> applicationVOS = new ArrayList<>();
        for (Application app : records) {
            ApplicationVO applicationVO = new ApplicationVO();
            ApplicationDTO appDto = ApplicationDTO.fromDspApplication(app);
            ApplicationDeployDTO deployDTO = deployService.searchById(appDto.getDeployId());
            ApplicationDeployVo deployVo = ApplicationDeployDTO.toApplicationDeployVo(deployDTO);
            //设置applicationConfigVo
            ApplicationConfigVO applicationConfigVO = new ApplicationConfigVO();
            CustomConfig customConfig = appDto.getCustomConfig();
            CoreConfig coreConfig = appDto.getCoreConfig();
            JobConfig jobConfig = appDto.getJobConfig();
            applicationConfigVO.setCore(coreConfig);
            applicationConfigVO.setCustom(customConfig);
            applicationConfigVO.setJob(jobConfig);
            applicationConfigVO.setFlowId(appDto.getFlowId());
            applicationConfigVO.setFlowName(appDto.getFlowName());
            applicationVO.setConfigVo(applicationConfigVO);

            //设置deployVO
            applicationVO.setDeployId(deployDTO.getId());
            applicationVO.setDeployVo(deployVo);
            ApplicationState appState = deployService.getAppState(deployDTO.getJobId());
            applicationVO.setApplicationState(appState);
            applicationVO.setEngineType(coreConfig.getEngineConfig().getEngineType());
            applicationVO.setEngineMode(coreConfig.getEngineConfig().getEngineMode());

            //设置TimerVo
            ApplicationTimerVo timerVo = new ApplicationTimerVo();
            ScheduleConfig scheduleConfig = appDto.getScheduleConfig();
            for (ApplicationTimer.ApplicationTimerType value : ApplicationTimer.ApplicationTimerType.values()) {
                ApplicationTimer timer = scheduleConfig.getVal(value.getType(), ApplicationTimer.class);
                switch (value.getType()) {
                    case "startTimer":
                        timerVo.setEnableStartTimer(timer.getEnableTimer());
                        timerVo.setStartTimerCron(timer.getTimerCron());
                        break;
                    case "stopTimer":
                        timerVo.setEnableStopTimer(timer.getEnableTimer());
                        timerVo.setStopTimerCron(timer.getTimerCron());
                        break;
                    case "restartTimer":
                        timerVo.setEnableRestartTimer(timer.getEnableTimer());
                        timerVo.setRestartTimerCron(timer.getTimerCron());
                        break;
                }
            }
            applicationVO.setTimerVo(timerVo);

            //设置其他
            applicationVO.setId(appDto.getId());
            applicationVO.setAppName(appDto.getAppName());
            applicationVO.setAppType(appDto.getAppType());
            applicationVO.setJobId(appDto.getJobId());
            applicationVO.setCreateTime(app.getCreateTime());
            applicationVO.setUpdateTime(app.getUpdateTime());
            applicationVO.setFlowId(new Integer[]{app.getFlowId()});
            applicationVO.setFlowName(app.getFlowName());
            applicationVO.setUserId(app.getUserId());
            applicationVO.setUserName(app.getUserName());
            applicationVO.setRemarkMsg(app.getRemarkMsg());
            applicationVOS.add(applicationVO);
        }
        return new PageWrapper<ApplicationVO>(applicationVOS, appPage.getTotal(), appPage.getCurrent(), appPage.getPages());
    }

    @Override
    public ApplicationDTO searchById(Integer appId) {
        Application application = applicationMapper.selectById(appId);
        ApplicationDTO applicationDTO = null;
        if (application != null) {
            applicationDTO = ApplicationDTO.fromDspApplication(application);
        }
        return applicationDTO;
    }

    @Override
    public List<Application> searchListByFlowId(Integer flowId) {
        ApplicationQuery applicationQuery = new ApplicationQuery();
        applicationQuery.setFlowId(flowId);
        List<Application> list = getApplicationQueryWrapper(applicationQuery).list();
        return list;
    }

    @Override
    public Application findOneByQuery(ApplicationQuery applicationQuery) {
        List<Application> list = getApplicationQueryOneWrapper(applicationQuery).list();
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public void updateFlowNameByAppIds(String newFlowName, List<Integer> appIds) {
        new LambdaUpdateChainWrapper<>(applicationMapper)
                .set(Application::getFlowName, newFlowName)
                .in(Application::getId, appIds)
                .update();
    }

    @Override
    public ApplicationDTO searchByJobId(String jobId) {
        Application app = new LambdaQueryChainWrapper<>(applicationMapper)
                .eq(Application::getJobId, jobId)
                .one();
        CheckTool.checkIsNull(app, DspResultStatus.APPLICATION_NOT_EXIST);
        ApplicationDTO appDTO = ApplicationDTO.fromDspApplication(app);
        return appDTO;
    }


    private LambdaQueryChainWrapper<Application> getApplicationQueryWrapper(ApplicationQuery query) {
        LambdaQueryChainWrapper<Application> wrapper = getAppQueryWrapper(query);
        if (StringUtils.isNotBlank(query.getAppName())) {
            wrapper.like(Application::getAppName, query.getAppName());
        }
        return wrapper;
    }

    private LambdaQueryChainWrapper<Application> getAppQueryWrapper(ApplicationQuery query) {
        LambdaQueryChainWrapper<Application> wrapper = new LambdaQueryChainWrapper<>(applicationMapper);
        if (query.getAppId() != null) {
            wrapper.eq(Application::getJobId, query.getAppId());
        }

        if (query.getAppType() != null && query.getAppType().length > 0) {
            wrapper.in(Application::getAppType, Arrays.stream(query.getAppType()).map(a -> a.getCode()).collect(Collectors.toList()));
        }

        if (StringUtils.isNotBlank(query.getFlowName())) {
            wrapper.eq(Application::getFlowName, query.getFlowName());
        }

        if (StringUtils.isNotBlank(query.getJobId())) {
            wrapper.eq(Application::getJobId, query.getJobId());
        }
        if (query.getApplicationState() != null && query.getApplicationState().length > 0) {
            wrapper.in(Application::getAppState, Arrays.stream(query.getApplicationState()).map(a -> a.getCode()).collect(Collectors.toList()));
        }

        String sortField = query.getSortField();
        PageQuery.SortOrder sortOrder = query.getSortOrder();
        if (StringUtils.isNotBlank(sortField)) {
            if (sortOrder == PageQuery.SortOrder.descend) {
                wrapper.orderByDesc(Application::getCreateTime);
            } else {
                wrapper.orderByAsc(Application::getCreateTime);
            }
        } else {
            wrapper.orderByDesc(Application::getCreateTime);
        }
        return wrapper;
    }


    private LambdaQueryChainWrapper<Application> getApplicationQueryOneWrapper(ApplicationQuery query) {
        LambdaQueryChainWrapper<Application> wrapper = getAppQueryWrapper(query);
        if (StringUtils.isNotBlank(query.getAppName())) {
            wrapper.eq(Application::getAppName, query.getAppName());
        }

        return wrapper;
    }

}
