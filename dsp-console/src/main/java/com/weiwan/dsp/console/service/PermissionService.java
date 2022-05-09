package com.weiwan.dsp.console.service;


import com.weiwan.dsp.console.model.dto.PermissionTreeDTO;
import com.weiwan.dsp.console.model.entity.Permission;

import java.util.List;
import java.util.Set;

public interface PermissionService {

    void createPermission(Permission permission);

    /**
     * 得到资源对应的权限字符串
     *
     * @param permissionIds
     * @return
     */
    Set<String> queryPermissionTree(Long... permissionIds);

    /**
     * 根据用户权限得到菜单
     *
     * @param permissions
     * @return
     */
    List<PermissionTreeDTO> queryMenus(Set<String> permissions);

    List<Permission> queryPermissionsByOrder();

    List<PermissionTreeDTO> queryPermissionTree();

    void updateNotNull(Permission permission);

    void deleteById(Long id);
}
