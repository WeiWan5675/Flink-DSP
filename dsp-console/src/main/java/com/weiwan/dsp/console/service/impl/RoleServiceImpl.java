package com.weiwan.dsp.console.service.impl;

import com.weiwan.dsp.console.mapper.RoleMapper;
import com.weiwan.dsp.console.model.dto.RoleDTO;
import com.weiwan.dsp.console.model.entity.Role;
import com.weiwan.dsp.console.service.PermissionService;
import com.weiwan.dsp.console.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PermissionService permissionService;

    @Override
    public Set<String> queryRoles(Long... roleIds) {
        Set<String> roles = new HashSet<>();
        List<Role> roleList = roleMapper.selectBatchIds(Arrays.asList(roleIds));
        for (Role role : roleList) {
            roles.add(role.getRole());
        }
        return roles;
    }

    @Override
    public Map<String, String> queryRoleNames(Long... roleIds) {
        Map<String, String> roleMap = new HashMap<>();
        List<Role> roleList = roleMapper.selectBatchIds(Arrays.asList(roleIds));
        for (Role role : roleList) {
            roleMap.put(role.getRole(), role.getName());
        }
        return roleMap;
    }

    @Override
    public Set<String> queryPermissions(Long... roleIds) {
        List<Role> roles = roleMapper.selectBatchIds(Arrays.asList(roleIds));
        Long[] permissionsIds = roles.stream()
                .flatMap(r -> Stream.of(r.getPermissionIds().split(",")))
                .map(Long::valueOf)
                .collect(Collectors.toSet())
                .toArray(new Long[]{});
        return permissionService.queryPermissionTree(permissionsIds);
    }

    @Override
    public List<RoleDTO> queryAllRole() {
        List<RoleDTO> collect = roleMapper.selectList(null).stream()
                .map(RoleDTO::new)
                .collect(Collectors.toList());

        return collect;
    }

    @Override
    public void create(Role role) {
        roleMapper.insert(role);
    }

    @Override
    public void updateNotNull(Role role) {
        roleMapper.updateById(role);
    }

    @Override
    public void deleteById(Long id) {
        roleMapper.deleteById(id);
    }

}
