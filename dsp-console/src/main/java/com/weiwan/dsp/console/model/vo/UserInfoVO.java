package com.weiwan.dsp.console.model.vo;

import lombok.Data;

import java.util.Set;

/**
 * @author: xiaozhennan
 */
@Data
public class UserInfoVO {
    private String name;
    private String avatar;
    private Set<String> permissions;

}
