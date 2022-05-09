package com.weiwan.dsp.console.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.weiwan.dsp.api.config.core.*;
import com.weiwan.dsp.api.config.flow.FlowConfig;
import com.weiwan.dsp.api.config.flow.NodeConfig;
import com.weiwan.dsp.api.constants.CoreConstants;
import com.weiwan.dsp.api.enums.ApplicationState;
import com.weiwan.dsp.client.deploy.DeployerFactory;
import com.weiwan.dsp.common.utils.*;
import com.weiwan.dsp.core.deploy.JobDeployExecutor;
import com.weiwan.dsp.common.enums.DspResultStatus;
import com.weiwan.dsp.common.exception.DspConsoleException;
import com.weiwan.dsp.console.mapper.FlowPluginRefMapper;
import com.weiwan.dsp.console.model.dto.FlowDTO;
import com.weiwan.dsp.console.model.dto.PluginDTO;
import com.weiwan.dsp.console.model.entity.PluginJar;
import com.weiwan.dsp.console.service.*;
import com.weiwan.dsp.console.service.impl.deploy.ApplicationEvent;
import com.weiwan.dsp.console.mapper.ApplicationDeployMapper;
import com.weiwan.dsp.console.mapper.ApplicationMapper;
import com.weiwan.dsp.console.model.dto.ApplicationDTO;
import com.weiwan.dsp.console.model.dto.ApplicationDeployDTO;
import com.weiwan.dsp.console.model.entity.Application;
import com.weiwan.dsp.console.model.entity.ApplicationDeploy;
import com.weiwan.dsp.console.model.query.ApplicationDeployQuery;
import com.weiwan.dsp.console.service.impl.deploy.*;
import com.weiwan.dsp.core.pub.SystemEnvManager;
import com.weiwan.dsp.core.utils.DspConfigFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author: xiaozhennan
 * @description:
 */
@Service
public class ApplicationDeployServiceImpl extends ServiceImpl<ApplicationDeployMapper, ApplicationDeploy>
        implements ApplicationDeployService {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationDeployServiceImpl.class);

    @Autowired
    private ApplicationDeployMapper applicationDeployMapper;

    @Autowired
    private ApplicationMapper applicationMapper;

    @Autowired
    @Lazy
    private ApplicationService applicationService;

    @Autowired
    private ApplicationResourceService resourceService;

    @Autowired
    @Lazy
    private FlowService flowService;

    @Autowired
    @Lazy
    private ApplicationDeployService applicationDeployService;

    @Autowired
    private FlowPluginRefMapper flowPluginRefMapper;

    @Autowired
    private PluginService pluginService;


    //用来存放部署的实例
    private Map<String, JobDeployExecutor> deployExecutorMap = new ConcurrentHashMap<>();

    private final ExecutorService executorService = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors() * 2,
            200,
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1024),
            ThreadUtil.threadFactory("dsp-deploy-executor", false, ThreadUtil.getLogUncaughtExceptionHandler("deploy")),
            new ThreadPoolExecutor.AbortPolicy()
    );


    @Override
    public boolean startApp(ApplicationDTO applicationDTO) {
        //先查询应用部署
        ApplicationDeployDTO applicationDeploy = applicationDeployService.searchByAppId(applicationDTO.getId());
        //TODO 需要查询流程表，把最新的流程添加过来
        if (applicationDeploy == null) {
            throw DspConsoleException.generateIllegalStateException(DspResultStatus.APPLICATION_APP_DEPLOY_NOT_EXIST);
        }
        ApplicationState jobState = applicationDeploy.getJobState();
        if (!ApplicationState.isFinalState(jobState)) {
            throw DspConsoleException.generateIllegalStateException(DspResultStatus.APPLICATION_DEPLOY_RUNING);
        }

        DspContextConfig dspContext = applicationDeploy.getDspContext();
        JobDeployExecutor deployExecutor = deployExecutorMap.computeIfAbsent(applicationDeploy.getJobId(), e -> (JobDeployExecutor) DeployerFactory.createDeployer(dspContext.getDsp()));
        deployExecutor.modifyApplicationState(ApplicationState.STARTING, true);
        if (applicationDeploy.getRestartMark() == 1) {
            // 这里设置是否需要重启标志， 如果需要， 就需要重新生成JSON文件
            deployExecutor.upgradeDeploy(dspContext.getDsp());
            deployExecutor.setNeedRestart(applicationDeploy.getRestartMark() == 1 ? true : false);
        }
        ApplicationEvent startApplicationEvent = new StartApplicationEvent(deployExecutor, applicationDeploy);
        //提交事件对象, 事件对象内部更新deployExecutor的状态
        executorService.submit(startApplicationEvent);
        return true;
    }

    @Override
    public boolean stopApp(ApplicationDTO applicationDTO) {
        //先查询应用部署
        ApplicationDeployDTO applicationDeploy = applicationDeployService.searchByAppId(applicationDTO.getId());

        if (applicationDeploy == null) {
            throw DspConsoleException.generateIllegalStateException(DspResultStatus.APPLICATION_APP_DEPLOY_NOT_EXIST);
        }
        ApplicationState jobState = applicationDeploy.getJobState();
        if (!ApplicationState.isRunningState(jobState)) {
            throw DspConsoleException.generateIllegalStateException(DspResultStatus.APPLICATION_DEPLOY_NOT_RUNING);
        }
        DspContextConfig dspContext = applicationDeploy.getDspContext();
        JobDeployExecutor deployExecutor = deployExecutorMap.computeIfAbsent(applicationDeploy.getJobId(), e -> (JobDeployExecutor) DeployerFactory.createDeployer(dspContext.getDsp()));
        deployExecutor.modifyApplicationState(ApplicationState.STOPPING, true);
        if (applicationDeploy.getRestartMark() == 1) {
            // 这里设置是否需要重启标志， 如果需要， 就需要重新生成JSON文件
            deployExecutor.upgradeDeploy(dspContext.getDsp());
            deployExecutor.setNeedRestart(applicationDeploy.getRestartMark() == 1 ? true : false);
        }
        StopApplicationEvent stopApplicationEvent = new StopApplicationEvent(deployExecutor, applicationDeploy);
        //提交事件对象, 事件对象内部更新deployExecutor的状态
        executorService.submit(stopApplicationEvent);
        return true;
    }

    @Override
    public boolean cancelApp(String jobId) {
        return false;
    }

    @Override
    public ApplicationState getAppState(String jobId) {
        JobDeployExecutor deployExecutor = deployExecutorMap.get(jobId);
        if (deployExecutor != null && deployExecutor.getApplicationState() != null) {
            return deployExecutor.getApplicationState();
        }
        ApplicationDeploy deploy = searchDeployByJobId(jobId);
        return ApplicationState.getApplicationState(deploy.getJobState());
    }


    @Override
    public boolean restartApp(String jobId) {
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDeploy(Application application) {
        CheckTool.checkIsNull(application, DspResultStatus.PARAM_IS_NULL);
        ApplicationDeploy applicationDeploy = applicationDeployMapper.selectById(application.getDeployId());
        ApplicationDeployDTO applicationDeployDTO = ApplicationDeployDTO.fromApplicationDeploy(applicationDeploy);
        //5. 删除部署的json文件
        File jobFile = applicationDeployDTO.getJobFile();
        //5.1 检查url不为空
        if (jobFile == null) {
            throw DspConsoleException.generateIllegalStateException(DspResultStatus.PARAM_IS_NULL);
        }
        //5.2 检查url指向的文件存在, 如果存在, 删除文件, 不存在就直接返回
        try {
            applicationDeployMapper.deleteById(applicationDeploy.getId());
            FileUtil.delete(jobFile);
            logger.info("delete job successfully! job id: {}", application.getJobId());
        } catch (Exception e) {
            logger.error("failed to delete job deploy, job id: {}", application.getJobId(), e);
            throw DspConsoleException.generateIllegalStateException(DspResultStatus.APPLICATION_DELETE_FAILED);
        }
        return true;
    }

    @Override
    public Page<ApplicationDeploy> searchDeploy(ApplicationDeployQuery deployQuery) {
        return null;
    }


    @Override
    public ApplicationDeploy searchDeployByJobId(String jobId) {
        if (StringUtils.isBlank(jobId)) {
            throw DspConsoleException.generateIllegalStateException(DspResultStatus.PARAM_IS_NULL);
        }
        LambdaQueryChainWrapper<ApplicationDeploy> queryChainWrapper = new LambdaQueryChainWrapper<>(applicationDeployMapper)
                .eq(ApplicationDeploy::getJobId, jobId);
        return queryChainWrapper.one();
    }

    @Override
    public boolean isDeployed(String jobId) {
        return false;
    }

    @Override
    public void updateDeploy(ApplicationDTO dto) throws Exception {
        // 查询老的deploy
        ApplicationDeploy dspApplicationDeploy = searchDeployByJobId(dto.getJobId());
        ApplicationDeployDTO deployDTO = ApplicationDeployDTO.fromApplicationDeploy(dspApplicationDeploy);
        DspContextConfig dspContext = deployDTO.getDspContext();
        DspConfig dsp = dspContext.getDsp();
        CoreConfig coreConfig = dto.getCoreConfig();
        CustomConfig customConfig = dto.getCustomConfig();
        FlowDTO flowDTO = dto.getFlowDTO();
        FlowConfig flowConfig = flowDTO.getFlowConfig();
        JobConfig jobConfig = dto.getJobConfig();
        dsp.setCore(coreConfig);
        dsp.setCustom(customConfig);
        dsp.setFlow(flowConfig);
        dsp.setJob(jobConfig);
        // 这里可能会存在，配置没有变化， 但是md5发生改变的问题
        String content = DspConfigFactory.dspConfigToContent(dsp);
        String newMd5 = MD5Utils.md5(content);
        if (!newMd5.equals(deployDTO.getJobJsonMd5())) {
            deployDTO.setRestartMark(1);
        } else {
            deployDTO.setRestartMark(0);
        }

        deployDTO.setEngineType(coreConfig.getEngineConfig().getEngineType());
        deployDTO.setEngineMode(coreConfig.getEngineConfig().getEngineMode());
        ApplicationDeploy deploy = ApplicationDeployDTO.toApplicationDeploy(deployDTO);
        updateById(deploy);
    }

    @Override
    public ApplicationDeployDTO createDeploy(ApplicationDTO applicationDTO) {

        String jobId = applicationDTO.getJobId();
        ApplicationDeploy deploy = searchDeployByJobId(jobId);
        if (deploy != null) {
            throw DspConsoleException.generateIllegalStateException(DspResultStatus.APPLICATION_APP_DEPLOY_EXISIT);
        }

        ApplicationDeployDTO deployDTO = new ApplicationDeployDTO();

        DspConfig dspConfig = new DspConfig();

        // Flow
        FlowDTO flowDTO = applicationDTO.getFlowDTO();
        if (flowDTO == null) {
            flowDTO = flowService.searchFlow(applicationDTO.getFlowId());
            CheckTool.checkNotNull(flowDTO, DspResultStatus.APPLICATION_FLOW_NOT_EXIST);
        }
        FlowConfig flowConfig = flowDTO.getFlowConfig();
        dspConfig.setFlow(flowConfig);

        // core
        CoreConfig coreConfig = applicationDTO.getCoreConfig();
        EngineConfig engineConfig = coreConfig.getEngineConfig();
        dspConfig.setCore(coreConfig);
        dspConfig.setCustom(applicationDTO.getCustomConfig());

        //context
        DspContextConfig dspContextConfig = new DspContextConfig(dspConfig);
        deployDTO.setAppId(applicationDTO.getId());
        deployDTO.setJobId(applicationDTO.getJobId());
        deployDTO.setJobName(applicationDTO.getAppName());
        deployDTO.setEngineMode(engineConfig.getEngineMode());
        deployDTO.setEngineType(engineConfig.getEngineType());
        deployDTO.setDeployUser(applicationDTO.getUserId());
        deployDTO.setDspContext(dspContextConfig);
        try {
            JobConfig jobConfig = applicationDTO.getJobConfig();
            if (jobConfig == null) {
                jobConfig = new JobConfig();
                applicationDTO.setJobConfig(jobConfig);
            }
            jobConfig.setJobId(jobId);
            jobConfig.setJobName(applicationDTO.getAppName());
            jobConfig.setJobType(applicationDTO.getAppType());
            List<PluginDTO> plugins = flowDTO.getPlugins();
            Set<String> pluginJarIds = plugins.stream().map(r -> r.getPluginJarId()).collect(Collectors.toSet());
            Map<String, PluginJar> pluginJarMap = pluginService.findPluginJarMapByJarIds(pluginJarIds);
            Set<String> pluginJarUrls = new HashSet<>();
            pluginJarMap.values().forEach(p -> {
                pluginJarUrls.add(p.getPluginJarUrl());
            });
            jobConfig.setJobPluginJars(new ArrayList<>(pluginJarUrls));
            dspConfig.setJob(jobConfig);
            String content = DspConfigFactory.dspConfigToContent(dspContextConfig.getDsp());
            String md5 = MD5Utils.md5(content);
            deployDTO.setJobJsonMd5(md5);
            String dspTmpDir = SystemEnvManager.getInstance().getDspTmpDir();
            String jobFileName = String.format(CoreConstants.JOB_FILE_FORMAT, jobId);
            String jobFile = dspTmpDir + File.separator + "jobs" + File.separator + jobFileName;
            deployDTO.setJobFile(new File(jobFile));
        } catch (IOException e) {
            throw DspConsoleException.generateIllegalStateException(DspResultStatus.UPDATE_DEPLOY_CONFIG_FAIL);
        }
        deployDTO.setRestartMark(0);
        deployDTO.setJobState(applicationDTO.getApplicationState());
        try {
            deploy = ApplicationDeployDTO.toApplicationDeploy(deployDTO);
        } catch (JsonProcessingException e) {
            throw DspConsoleException.generateIllegalStateException(DspResultStatus.APPLICATION_DEPLOY_CREATE_ERROR);
        }
        save(deploy);
        ApplicationDeployDTO applicationDeployDTO = searchByAppId(deployDTO.getAppId());
        return applicationDeployDTO;
    }

    @Override
    public ApplicationDeployDTO searchById(Integer deployId) {
        CheckTool.checkNotNull(deployId, DspResultStatus.PARAM_IS_NULL);
        ApplicationDeploy deploy = applicationDeployMapper.selectById(deployId);
        CheckTool.checkNotNull(deploy, DspResultStatus.APPLICATION_APP_DEPLOY_NOT_EXIST);
        ApplicationDeployDTO deployDTO = ApplicationDeployDTO.fromApplicationDeploy(deploy);
        return deployDTO;
    }

    @Override
    public void markAppChange(List<Integer> appIds) {
        if (appIds != null && appIds.size() > 0) {
            new LambdaUpdateChainWrapper<>(applicationDeployMapper)
                    .set(ApplicationDeploy::getRestartMark, 1)
                    .in(ApplicationDeploy::getAppId, appIds)
                    .update();
        }
    }

    @Override
    public ApplicationDeployDTO searchByAppId(Integer id) {
        ApplicationDeploy applicationDeploy = new LambdaQueryChainWrapper<>(applicationDeployMapper)
                .eq(ApplicationDeploy::getAppId, id).one();
        ApplicationDeployDTO applicationDeployDTO = ApplicationDeployDTO.fromApplicationDeploy(applicationDeploy);
        return applicationDeployDTO;
    }

    @Override
    public synchronized Map<String, JobDeployExecutor> getDeployExecutorMap() {
        return this.deployExecutorMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateJobState(ApplicationDeployDTO deployDTO, ApplicationState state) {
        Integer deployId = deployDTO.getId();
        LambdaUpdateChainWrapper<ApplicationDeploy> updateDeploy = new LambdaUpdateChainWrapper<>(applicationDeployMapper)
                .eq(ApplicationDeploy::getId, deployId)
                .set(ApplicationDeploy::getJobState, state.getCode());
        updateDeploy.update();
        LambdaUpdateChainWrapper<Application> updateApplication = new LambdaUpdateChainWrapper<>(applicationMapper)
                .eq(Application::getId, deployDTO.getAppId())
                .set(Application::getAppState, state.getCode());
        updateApplication.update();
    }

    @Override
    public List<ApplicationDeployDTO> searchUnsettledDeploy() {
        List<ApplicationState> runningStates = ApplicationState.getRunningStates();
        List<Integer> stateCodes = runningStates.stream().map(s -> s.getCode()).collect(Collectors.toList());

        List<ApplicationDeploy> deploys = new LambdaQueryChainWrapper<>(applicationDeployMapper)
                .in(ApplicationDeploy::getJobState, stateCodes).list();
        List<ApplicationDeployDTO> deployDTOS = new ArrayList<>();
        deploys.forEach(d -> {
            deployDTOS.add(ApplicationDeployDTO.fromApplicationDeploy(d));
        });
        return deployDTOS;
    }

    @Override
    public void refreshDeploy(Integer deployId) {
        ApplicationDeployDTO applicationDeploy = searchById(deployId);
        DspContextConfig dspContext = applicationDeploy.getDspContext();
        JobDeployExecutor deployExecutor = deployExecutorMap.computeIfAbsent(applicationDeploy.getJobId(), e -> (JobDeployExecutor) DeployerFactory.createDeployer(dspContext.getDsp()));
        deployExecutor.modifyApplicationState(applicationDeploy.getJobState(), false);
        deployExecutor.setNeedRestart(applicationDeploy.getRestartMark() == 1 ? true : false);
        ApplicationEvent refreshApplicationEvent = new RefreshApplicationEvent(deployExecutor, applicationDeploy);
        executorService.submit(refreshApplicationEvent);
    }

    @Override
    public void updateJobStartTime(ApplicationDeployDTO deployDTO) {
        Integer deployId = deployDTO.getId();
        LambdaUpdateChainWrapper<ApplicationDeploy> updateDeploy = new LambdaUpdateChainWrapper<>(applicationDeployMapper)
                .eq(ApplicationDeploy::getId, deployId)
                .set(ApplicationDeploy::getStartTime, new Date());
        updateDeploy.update();
    }

    @Override
    public void updateJobStopTime(ApplicationDeployDTO deployDTO) {
        Integer deployId = deployDTO.getId();
        LambdaUpdateChainWrapper<ApplicationDeploy> updateDeploy = new LambdaUpdateChainWrapper<>(applicationDeployMapper)
                .eq(ApplicationDeploy::getId, deployId)
                .set(ApplicationDeploy::getEndTime, new Date());
        updateDeploy.update();
    }

    @Override
    public void updateJobRestartMark(ApplicationDeployDTO deployDTO) {
        Integer deployId = deployDTO.getId();
        LambdaUpdateChainWrapper<ApplicationDeploy> updateDeploy = new LambdaUpdateChainWrapper<>(applicationDeployMapper)
                .eq(ApplicationDeploy::getId, deployId)
                .set(ApplicationDeploy::getRestartMark, 0);
        updateDeploy.update();
    }

    @Override
    public void updateDeployFlowByAppId(Integer appId, FlowDTO flowDTO) throws JsonProcessingException {
        // 查询应用部署
        ApplicationDeployDTO deployDTO = searchByAppId(appId);
        DspContextConfig context = deployDTO.getDspContext();
        DspConfig dsp = context.getDsp();
        FlowConfig flowConfig = flowDTO.getFlowConfig();
        dsp.setFlow(flowConfig);
        deployDTO.setRestartMark(1);
        ApplicationDeploy applicationDeploy = ApplicationDeployDTO.toApplicationDeploy(deployDTO);
        updateById(applicationDeploy);
    }


}
