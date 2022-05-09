package com.weiwan.dsp.console.controller;

import com.weiwan.dsp.common.enums.DspResultStatus;
import com.weiwan.dsp.common.exception.DspConsoleException;
import com.weiwan.dsp.common.utils.ObjectUtil;
import com.weiwan.dsp.console.model.PageWrapper;
import com.weiwan.dsp.console.model.Result;
import com.weiwan.dsp.console.model.constant.ConsoleConstants;
import com.weiwan.dsp.console.model.dto.FlowDTO;
import com.weiwan.dsp.console.model.query.FlowQuery;
import com.weiwan.dsp.console.model.vo.FlowVo;
import com.weiwan.dsp.console.service.FlowService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author: xiaozhennan
 * @description:
 */
@RestController
@RequestMapping("flow")
public class FlowController {

    private static final Logger logger = LoggerFactory.getLogger(FlowController.class);

    @Autowired
    private FlowService flowService;

    @PostMapping("list")
    @RequiresPermissions("flow:view")
    public Result<PageWrapper<FlowVo>> list(@RequestBody FlowQuery query) {
        PageWrapper<FlowVo> flowVoPage = flowService.search(query);
        return Result.success(flowVoPage);
    }

    @PostMapping("create")
    @RequiresPermissions("flow:create")
    public Result create(@RequestBody FlowVo flow) {
        FlowDTO flowDTO = FlowDTO.fromFlowVo(flow);
        flowService.createFlow(flowDTO);
        return Result.success();
    }

    @PostMapping("update")
    @RequiresPermissions("flow:update")
    public Result update(@RequestBody FlowVo flow) {
        FlowDTO flowDTO = FlowDTO.fromFlowVo(flow);
        flowService.updateFlow(flowDTO);
        return Result.success();
    }


    @RequestMapping(value = "disable", method = RequestMethod.PUT)
    @RequiresPermissions("flow:disable")
    public Result disable(@RequestBody FlowVo flow) {
        FlowDTO flowDTO = FlowDTO.fromFlowVo(flow);
        flowService.disableFlow(flowDTO);
        return Result.success();
    }


    @RequestMapping(value = "delete", method = RequestMethod.DELETE)
    @RequiresPermissions("flow:delete")
    public Result delete(@RequestBody FlowVo flow) {
        FlowDTO flowDTO = FlowDTO.fromFlowVo(flow);
        flowService.deleteFlow(flowDTO.getId());
        return Result.success();
    }

    @RequestMapping(value = "checked", method = RequestMethod.PUT)
    @RequiresPermissions("flow:update")
    public Result checked(@RequestBody FlowVo flow) {
        flowService.checkedChange(flow);
        return Result.success();
    }

    @GetMapping("download")
    @RequiresPermissions("flow:download")
    public void download(@RequestParam Integer flowId, HttpServletRequest request, HttpServletResponse response) {
        FlowDTO flowDTO = flowService.searchFlow(flowId);
        String flowJson = ObjectUtil.serialize(flowDTO);
        String fileName = String.format(ConsoleConstants.FLOW_EXPORT_FILE_NAME_FORMAT, flowDTO.getFlowName());
        response.setHeader("content-type", "application/octet-stream");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        OutputStream os = null;
        try {
            os = response.getOutputStream();
            os.write(flowJson.getBytes());
            os.flush();
        } catch (IOException e) {
            logger.error("flow download error, flow id: {}", flowId, e);
            throw DspConsoleException.generateIllegalStateException(DspResultStatus.FLOW_DOWNLOAD_FAILED);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @PostMapping("upload")
    @RequiresPermissions("flow:upload")
    public Result upload(@RequestBody FlowDTO flowDTO) {
        flowService.uploadFlow(flowDTO);
        return Result.success();
    }
}
