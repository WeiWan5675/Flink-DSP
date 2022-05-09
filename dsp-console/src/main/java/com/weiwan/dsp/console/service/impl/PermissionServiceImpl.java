package com.weiwan.dsp.console.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.weiwan.dsp.console.mapper.PermissionMapper;
import com.weiwan.dsp.console.model.SystemContextHolder;
import com.weiwan.dsp.console.model.constant.ConsoleConstants;
import com.weiwan.dsp.console.model.dto.PermissionTreeDTO;
import com.weiwan.dsp.console.model.entity.Permission;
import com.weiwan.dsp.console.service.PermissionService;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    @Transactional
    public void createPermission(Permission permission) {
        if (permission.getParentId() == ConsoleConstants.PERMISSION_ROOT_ID) {
            permission.setParentIds("0/");
        } else {
            QueryWrapper<Permission> permissionQueryWrapper = new QueryWrapper<>();
            permissionQueryWrapper.eq(true, "id", permission.getParentId());
            Permission parent = permissionMapper.selectOne(permissionQueryWrapper);
            permission.setParentIds(parent.makeSelfAsParentIds());
        }
        permission.setStatus(1);
        permissionMapper.insert(permission);
    }

    @Override
    public Set<String> queryPermissionTree(Long... permissionIds) {
        Set<String> permissions = new HashSet<>();
        QueryWrapper<Permission> permissionQueryWrapper = new QueryWrapper<>();
        permissionQueryWrapper.in("id", permissionIds);
        List<Permission> permissionsList = permissionMapper.selectList(permissionQueryWrapper);
        for (Permission permission : permissionsList) {
            if (StringUtils.isEmpty(permission.getPermission())) {
                continue;
            }
            permissions.add(permission.getPermission());
        }
        return permissions;
    }

    @Override
    public List<PermissionTreeDTO> queryMenus(Set<String> permissions) {
        List<Permission> allPermissions = queryPermissionList();
        List<PermissionTreeDTO> menus = new ArrayList<>();
        for (Permission permission : allPermissions) {
            if (permission.getType() != 1) {
                continue;
            }
            if (!hasPermission(permissions, permission)) {
                continue;
            }
            menus.add(new PermissionTreeDTO(permission));
        }
        return menus;
    }

    @Override
    public List<Permission> queryPermissionsByOrder() {
        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort");
        return permissionMapper.selectList(queryWrapper);
    }

    @Override
    public List<PermissionTreeDTO> queryPermissionTree() {
        List<Permission> permissions = queryPermissionList();
        SystemContextHolder.putThreadCache("permissions", permissions);
        return getPermissionTree(permissions, ConsoleConstants.PERMISSION_ROOT_ID);
    }

    private List<Permission> queryPermissionList() {
        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort");
        List<Permission> permissions = permissionMapper.selectList(queryWrapper);
        return permissions;
    }

    @Override
    public void updateNotNull(Permission permission) {
        permissionMapper.updateById(permission);
    }

    @Override
    public void deleteById(Long id) {
        permissionMapper.deleteById(id);
    }

    private List<PermissionTreeDTO> getPermissionTree(List<Permission> list, Long parentId) {
        List<PermissionTreeDTO> permissionTree = list.stream()
                .filter(p -> p.getParentId().equals(parentId))
                .map(PermissionTreeDTO::new)
                .collect(Collectors.toList());
        if (permissionTree.isEmpty()) {
            return Collections.emptyList();
        }
        for (PermissionTreeDTO permission : permissionTree) {
            permission.setChildren(getPermissionTree(list, permission.getId()));
        }
        return permissionTree;
    }

    private boolean hasPermission(Set<String> permissions, Permission resource) {
        if (StringUtils.isEmpty(resource.getPermission())) {
            return true;
        }
        for (String permission : permissions) {
            WildcardPermission p1 = new WildcardPermission(permission);
            WildcardPermission p2 = new WildcardPermission(resource.getPermission());
            if (p1.implies(p2) || p2.implies(p1)) {
                return true;
            }
        }
        return false;
    }

}
