package com.weiwan.dsp.console.model.query;

import lombok.Data;

/**
 * @author: xiaozhennan
 */
@Data
public class UserQuery extends PageQuery {
    private Long id;
    private String username;
    private Integer locked;

}
