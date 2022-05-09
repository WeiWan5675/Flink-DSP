package com.weiwan.dsp.console.mapper;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.weiwan.dsp.console.model.entity.User;

public interface UserMapper extends BaseMapper<User> {
    /**
     * 获取单个用户
     *
     * @param username
     * @return
     */
    default User selectByUsername(String username) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq(true, "username", username);
        User user = this.selectOne(userQueryWrapper);
        return user;
    }

}
