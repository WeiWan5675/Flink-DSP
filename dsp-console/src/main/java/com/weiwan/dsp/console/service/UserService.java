package com.weiwan.dsp.console.service;

import com.weiwan.dsp.common.exception.DspConsoleException;
import com.weiwan.dsp.console.model.PageWrapper;
import com.weiwan.dsp.console.model.dto.LoginDTO;
import com.weiwan.dsp.console.model.dto.UserPageDTO;
import com.weiwan.dsp.console.model.dto.UserTokenDTO;
import com.weiwan.dsp.console.model.entity.User;
import com.weiwan.dsp.console.model.query.UserQuery;

import java.util.Set;

public interface UserService {

    /**
     * 创建用户
     *
     * @param user
     */
    void createUser(User user) throws DspConsoleException;

    /**
     * 修改密码
     *
     * @param userId
     * @param newPassword
     */
    User changePassword(Long userId, String newPassword);

    /**
     * 根据用户名查找其角色
     *
     * @param username
     * @return
     */
    Set<String> queryRoles(String username);

    /**
     * 根据用户名查找其权限
     *
     * @param username
     * @return
     */
    Set<String> queryPermissions(String username);

    User queryByUsername(String username);

    UserTokenDTO login(LoginDTO loginDTO);

    PageWrapper<UserPageDTO> queryUserPage(UserQuery userQuery);

    void updateNotNull(User user);

    void deleteById(Long id);

    User getCurrentUser();
}
