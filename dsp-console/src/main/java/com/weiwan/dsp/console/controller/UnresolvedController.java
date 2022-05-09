package com.weiwan.dsp.console.controller;

import com.alibaba.fastjson.JSONObject;
import com.weiwan.dsp.api.pojo.UnresolvedDataRecord;
import com.weiwan.dsp.console.service.UnresolvedLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @author Zhu Shaoqin
 * @email zsqmia@163.com
 * @date 2022/3/7 20:15
 * @description
 */
@RestController
@RequestMapping("unresolved")
public class UnresolvedController {

    @Autowired
    private UnresolvedLogService unresolvedLogService;

    @PostMapping("report")
    public void report(@RequestBody UnresolvedDataRecord record) {
        unresolvedLogService.insertLog(record);
    }
}
