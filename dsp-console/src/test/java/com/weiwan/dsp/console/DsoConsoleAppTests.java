package com.weiwan.dsp.console;

import com.weiwan.dsp.console.controller.PermissionController;
import com.weiwan.dsp.console.model.dto.PermissionTreeDTO;
import com.weiwan.dsp.console.service.PermissionService;
import com.weiwan.dsp.console.util.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class DsoConsoleAppTests {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private PermissionController permissionController;

    @Test
    public void contextLoads() {
    }

    @Test
    public void queryPermissionTreeWithService() {
        List<PermissionTreeDTO> permissionTreeDTOS = permissionService.queryPermissionTree();
        log.info("返回的json为：{}", JSONUtil.toJSONString(permissionTreeDTOS));
    }

    @Test
    public void queryPermissionTreeWithController() {
//        Result<List<PermissionDTO>> listResult = permissionController.queryPermissionTree();
//        log.info("返回的json为：{}", JSONUtil.toJSON(listResult));
    }

}
