package com.weiwan.dsp.console.controller;

import com.weiwan.dsp.console.model.PageWrapper;
import com.weiwan.dsp.console.model.Result;
import com.weiwan.dsp.console.model.query.PluginQuery;
import com.weiwan.dsp.console.model.vo.PluginJarFileVo;
import com.weiwan.dsp.console.model.vo.PluginJarVo;
import com.weiwan.dsp.console.model.vo.PluginListVo;
import com.weiwan.dsp.console.service.PluginService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author: xiaozhennan
 * @description:
 */
@RestController
@RequestMapping("plugin")
public class PluginController {


    @Autowired
    private PluginService pluginService;


    @PostMapping("list")
    @RequiresPermissions("plugin:view")
    public Result<PageWrapper<PluginJarVo>> list(@RequestBody PluginQuery query) {
        PageWrapper<PluginJarVo> pluginJarVoPageWrapper = pluginService.searchPlugin(query);
        return Result.success(pluginJarVoPageWrapper);
    }

    @RequestMapping(value = "disable",method = RequestMethod.PUT)
    @RequiresPermissions("plugin:disable")
    public Result disable(@RequestBody PluginJarVo pluginJarVo) {
        pluginService.disablePlugin(pluginJarVo);
        return Result.success();
    }

    @RequestMapping(value = "create",method = RequestMethod.POST)
    @RequiresPermissions("plugin:create")
    public Result create(@RequestBody PluginJarVo pluginJarVo) {
        pluginService.createPlugin(pluginJarVo);
        return Result.success();
    }

    @RequestMapping(value = "update",method = RequestMethod.PUT)
    @RequiresPermissions("plugin:update")
    public Result update(@RequestBody PluginJarVo pluginJarVo) {
        pluginService.updatePlugin(pluginJarVo);
        return Result.success();
    }

    @RequestMapping(value = "delete",method = RequestMethod.DELETE)
    @RequiresPermissions("plugin:delete")
    public Result delete(@RequestBody PluginJarVo pluginJarVo) {
        pluginService.deletePlugin(pluginJarVo);
        return Result.success();
    }

    @RequestMapping(value = "upload", method = RequestMethod.POST)
    @RequiresPermissions("plugin:upload")
    public Result<PluginJarFileVo> upload(@RequestParam("file") MultipartFile file, @RequestParam("pluginJarId") String pluginJarId) throws Exception{
        PluginJarFileVo fileInfo = pluginService.uploadPlugin(file, pluginJarId);
        return Result.success(fileInfo);
    }

    @RequestMapping(value = "verify", method = RequestMethod.POST)
    @RequiresPermissions("plugin:upload")
    public Result<PluginJarVo> verify(@RequestBody PluginJarFileVo fileVo) {
        PluginJarVo pluginJarVo = pluginService.verifyPlugin(fileVo);
        return Result.success(pluginJarVo);
    }

    @RequestMapping(value = "updatePart", method = RequestMethod.PUT)
    @RequiresPermissions("plugin:update")
    public Result updatePart(@RequestBody PluginJarVo pluginJarVo) {
        pluginService.updatePart(pluginJarVo);
        return Result.success();
    }

    @GetMapping("plugins")
    @RequiresPermissions("plugin:view")
    public Result<PluginListVo> plugins() {
        PluginListVo pluginListVo = pluginService.searchAll();
        return Result.success(pluginListVo);
    }
}
