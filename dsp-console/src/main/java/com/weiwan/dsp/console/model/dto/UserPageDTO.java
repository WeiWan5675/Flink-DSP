package com.weiwan.dsp.console.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @author: xiaozhennan
 */
@Data
public class UserPageDTO {
    private Long id; //编号
    private String username; //用户名
    private List<Long> roleIds; //拥有的角色列表
    private List<String> roleNames;//
    private Integer locked;
}
