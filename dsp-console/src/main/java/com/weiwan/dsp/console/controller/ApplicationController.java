package com.weiwan.dsp.console.controller;

import com.weiwan.dsp.api.enums.ApplicationState;
import com.weiwan.dsp.console.model.PageWrapper;
import com.weiwan.dsp.console.model.Result;
import com.weiwan.dsp.console.model.dto.ApplicationDTO;
import com.weiwan.dsp.console.model.entity.Application;
import com.weiwan.dsp.console.model.query.ApplicationQuery;
import com.weiwan.dsp.console.model.vo.ApplicationVO;
import com.weiwan.dsp.console.service.ApplicationDeployService;
import com.weiwan.dsp.console.service.ApplicationService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author: xiaozhennan
 * @description:
 */
@RestController
@RequestMapping("application")
public class ApplicationController {
    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationDeployService applicationDeployService;

    @PostMapping("query")
    @RequiresPermissions("application:view")
    public Result<Application> findByQuery(@RequestBody ApplicationQuery applicationQuery) {
        Application application = applicationService.findOneByQuery(applicationQuery);
        return Result.success(application);
    }

    @PostMapping("create")
    @RequiresPermissions("application:create")
    public Result create(@RequestBody ApplicationVO application) {
        ApplicationDTO applicationDTO = ApplicationDTO.fromDspApplicationVo(application);
        applicationService.createApp(applicationDTO);
        return Result.success();
    }

    @DeleteMapping("delete")
    @RequiresPermissions("application:delete")
    public Result delete(@RequestParam Integer appId) {
        boolean delete = applicationService.deleteApp(appId);
        return Result.success(delete);
    }


    @PostMapping("update")
    @RequiresPermissions("application:update")
    public Result update(@RequestBody ApplicationVO application) {
        ApplicationDTO applicationDTO = ApplicationDTO.fromDspApplicationVo(application);
        applicationService.updateApp(applicationDTO);
        return Result.success();
    }

    @PostMapping("list")
    @RequiresPermissions("application:view")
    public Result<PageWrapper<ApplicationVO>> list(@RequestBody ApplicationQuery query) {
        PageWrapper<ApplicationVO> page = applicationService.searchApp(query);
        return Result.success(page);
    }


    @PostMapping("start")
    @RequiresPermissions("application:start")
    public Result startApp(@RequestBody ApplicationVO applicationVO) {
        ApplicationDTO applicationDTO = ApplicationDTO.fromDspApplicationVo(applicationVO);
        boolean started = applicationDeployService.startApp(applicationDTO);
        return Result.success(started);
    }

    @PostMapping("stop")
    @RequiresPermissions("application:stop")
    public Result stopApp(@RequestBody ApplicationVO applicationVO) {
        ApplicationDTO applicationDTO = ApplicationDTO.fromDspApplicationVo(applicationVO);
        boolean stoped = applicationDeployService.stopApp(applicationDTO);
        return Result.success(stoped);
    }


    @GetMapping("cancel")
    @RequiresPermissions("application:cancel")
    public Result cancelApp(Application application) {
        boolean started = applicationDeployService.cancelApp(application.getJobId());
        return Result.success(started);
    }


    @GetMapping("restart")
    @RequiresPermissions("application:restart")
    public Result restartApp(Application application) {
        boolean started = applicationDeployService.restartApp(application.getJobId());
        return Result.success(started);
    }


    @GetMapping("state")
    @RequiresPermissions("application:state")
    public Result getAppState(Application application) {
        ApplicationState appState = applicationDeployService.getAppState(application.getJobId());
        return Result.success(appState);
    }


}
