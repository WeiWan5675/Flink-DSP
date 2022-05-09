package com.weiwan.dsp.console.controller;

import com.weiwan.dsp.console.model.PageWrapper;
import com.weiwan.dsp.console.model.Result;
import com.weiwan.dsp.console.model.query.ConfigQuery;
import com.weiwan.dsp.console.model.vo.ConfigVo;
import com.weiwan.dsp.console.service.ConfigService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author: xiaozhennan
 * @description:
 */
@RestController
@RequestMapping("config")
public class ConfigController {

    @Autowired
    private ConfigService configService;

    @PostMapping("list")
    @RequiresPermissions("config:view")
    public Result<PageWrapper<ConfigVo>> list(@RequestBody ConfigQuery query) {
        PageWrapper<ConfigVo> page = configService.searchConfig(query);
        return Result.success(page);
    }

    @PostMapping("create")
    @RequiresPermissions("config:create")
    public Result create(@RequestBody ConfigVo configVo) {
        configService.createConfig(configVo);
        return Result.success();
    }

    @PostMapping("update")
    @RequiresPermissions("config:update")
    public Result update(@RequestBody ConfigVo configVo) {
        configService.updateConfig(configVo);
        return Result.success();
    }

    @DeleteMapping("delete")
    @RequiresPermissions("config:delete")
    public Result delete(@RequestParam Integer configPk) {
        boolean delete = configService.deleteConfig(configPk);
        return Result.success(delete);
    }
}
