package com.weiwan.dsp.console.controller;

import com.weiwan.dsp.console.model.Result;
import com.weiwan.dsp.console.model.constant.ConsoleConstants;
import com.weiwan.dsp.console.model.dto.PermissionTreeDTO;
import com.weiwan.dsp.console.model.vo.UserInfoVO;
import com.weiwan.dsp.console.service.PermissionService;
import com.weiwan.dsp.console.service.UserService;
import com.weiwan.dsp.console.util.JSONUtil;
import com.weiwan.dsp.console.util.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author cjbi
 */
@Slf4j
@RestController
public class IndexController {

    @Autowired
    private UserService userService;

    @Autowired
    private PermissionService permissionService;

    @GetMapping("user/nav")
    public Result<List<Map<String, Object>>> getUserNav() {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        Set<String> permissions = userService.queryPermissions(username);
        List<PermissionTreeDTO> permissionTreeDTOS = permissionService.queryMenus(permissions);
        List<Map<String, Object>> list = new ArrayList<>();
        for (PermissionTreeDTO permission : permissionTreeDTOS) {
            try {
                Map<String, Object> userNav = JSONUtil.toObject(permission.getConfig(), Map.class);
                userNav.put("id", permission.getId());
                userNav.put("parentId", permission.getParentId());
                list.add(userNav);
            } catch (Exception e) {
                log.warn("菜单【{}】路由配置有误，不展示此菜单", permission.getName());
            }
        }
        return Result.success(list);
    }

    @GetMapping("user/info")
    public Result<UserInfoVO> getUserInfo() {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setName(username);
        userInfoVO.setAvatar(ConsoleConstants.DEFAULT_AVATAR);
        userInfoVO.setPermissions(userService.queryPermissions(username));
        return Result.success(userInfoVO);
    }

    @GetMapping("datasource/initialize")
    public Result initializeDatasource() {
        DataSource dataSource = SpringUtils.getBean(DataSource.class);
        ResourceLoader loader = new DefaultResourceLoader();
        Resource schema = loader.getResource("classpath:schema.sql");
        Resource data = loader.getResource("classpath:data.sql");
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(schema, data);
        populator.execute(dataSource);
        return Result.success();
    }

}
