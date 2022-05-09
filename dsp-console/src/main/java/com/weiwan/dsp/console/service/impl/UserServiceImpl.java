package com.weiwan.dsp.console.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.weiwan.dsp.console.util.PasswordHelper;
import com.weiwan.dsp.common.exception.DspConsoleException;
import com.weiwan.dsp.console.mapper.UserMapper;
import com.weiwan.dsp.console.model.PageWrapper;
import com.weiwan.dsp.console.model.constant.ConsoleConstants;
import com.weiwan.dsp.console.model.dto.LoginDTO;
import com.weiwan.dsp.console.model.dto.UserPageDTO;
import com.weiwan.dsp.console.model.dto.UserTokenDTO;
import com.weiwan.dsp.console.model.entity.User;
import com.weiwan.dsp.common.enums.DspResultStatus;
import com.weiwan.dsp.console.model.query.PageQuery;
import com.weiwan.dsp.console.model.query.UserQuery;
import com.weiwan.dsp.console.service.RoleService;
import com.weiwan.dsp.console.service.UserService;
import com.weiwan.dsp.console.shiro.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleService roleService;

    private PasswordHelper passwordHelper = new PasswordHelper();

    @Override
    @Transactional
    public void createUser(User user) {
        User u = userMapper.selectByUsername(user.getUsername());
        if (u != null) {
            throw new DspConsoleException(DspResultStatus.FAILED_USER_ALREADY_EXIST);
        }
        //设置默认密码
        if(StringUtils.isBlank(user.getPassword())){
            user.setPassword(ConsoleConstants.DEFAULT_PASSWORD);
        }
        // 加密密码
        passwordHelper.encryptPassword(user);
        userMapper.insert(user);
    }




    @Override
    @Transactional
    public User changePassword(Long userId, String newPassword) {
        User user = userMapper.selectById(userId);
        user.setPassword(newPassword);
        passwordHelper.encryptPassword(user);
        userMapper.updateById(user);
        return user;
    }

    @Override
    public Set<String> queryRoles(String username) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            return Collections.EMPTY_SET;
        }
        return roleService.queryRoles(getRoleIds(user));
    }

    @Override
    public Set<String> queryPermissions(String username) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            return Collections.EMPTY_SET;
        }
        return roleService.queryPermissions(getRoleIds(user));
    }

    @Override
    public User queryByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public UserTokenDTO login(LoginDTO loginDTO) {
        User user = userMapper.selectByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new DspConsoleException(DspResultStatus.LOGIN_ERROR, "用户不存在");
        }
        if (!passwordHelper.verifyPassword(user, loginDTO.getPassword())) {
            throw new DspConsoleException(DspResultStatus.LOGIN_ERROR, "密码错误");
        }
        UserTokenDTO userInfoDTO = new UserTokenDTO();
        userInfoDTO.setUsername(user.getUsername());
        userInfoDTO.setToken(JwtUtil.sign(user.getUsername(), String.valueOf(System.currentTimeMillis())));
        return userInfoDTO;
    }

    @Override
    public PageWrapper<UserPageDTO> queryUserPage(UserQuery userQuery) {
        Page<User> page = new Page<>(userQuery.getPageNo(), userQuery.getPageSize());
        QueryWrapper<User> userQueryWrapper = getUserQueryWrapper(userQuery);

        Page<User> userPage = userMapper.selectPage(page, userQueryWrapper);
        List<User> users = userPage.getRecords();
        List<UserPageDTO> list = new ArrayList<>();
        for (User user : users) {
            UserPageDTO userDTO = new UserPageDTO();
            userDTO.setId(user.getId());
            userDTO.setUsername(user.getUsername());
            userDTO.setRoleIds(Arrays.asList(getRoleIds(user)));
            userDTO.setRoleNames(getRoleNames(user));
            userDTO.setLocked(user.getLocked());
            list.add(userDTO);
        }
        return new PageWrapper<>(list, userPage.getTotal(), userPage.getCurrent(), userPage.getPages());
    }

    private QueryWrapper<User> getUserQueryWrapper(UserQuery userQuery) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<User>();
        if (userQuery.getId() != null) {
            userQueryWrapper.eq(true, "id", userQuery.getId());
        }
        if (userQuery.getUsername() != null) {
            userQueryWrapper.eq(true, "username", userQuery.getUsername());
        }
        if (userQuery.getLocked() != null) {
            userQueryWrapper.eq(true, "locked", userQuery.getLocked());
        }

        if (userQuery.getSortOrder() != null && userQuery.getSortField() != null) {
            if (userQuery.getSortOrder() == PageQuery.SortOrder.ascend) {
                userQueryWrapper.orderByAsc(userQuery.getSortField());
            }
            if (userQuery.getSortOrder() == PageQuery.SortOrder.descend) {
                userQueryWrapper.orderByDesc(userQuery.getSortField());
            }
        } else {
            userQueryWrapper.orderByDesc("id");
        }
        return userQueryWrapper;
    }

    @Override
    public void updateNotNull(User user) {
        User user1 = changePassword(user.getId(), user.getPassword());
        user1.setRoleIds(user.getRoleIds());
        userMapper.updateById(user1);
    }

    @Override
    public void deleteById(Long id) {
        userMapper.deleteById(id);
    }

    @Override
    public User getCurrentUser() {
        String principal = (String) SecurityUtils.getSubject().getPrincipal();
        return userMapper.selectByUsername(principal);
    }


    private List<String> getRoleNames(User user) {
        Map<String, String> roleMap = roleService.queryRoleNames(getRoleIds(user));
        return roleMap.entrySet().stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    private Long[] getRoleIds(User user) {
        return Stream.of(user.getRoleIds().split(","))
                .map(Long::valueOf)
                .collect(Collectors.toList()).toArray(new Long[0]);
    }

}
