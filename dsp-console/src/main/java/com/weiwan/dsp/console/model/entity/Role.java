package com.weiwan.dsp.console.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


/**
 * @author: xiaozhennan
 */
@Data
@TableName("sys_role")
public class Role {
    private Long id;
    private String role;
    /**
     * 角色名称
     */
    private String name;
    /**
     * 角色描述,UI界面显示使用
     */
    private String description;
    /**
     * 状态,如果不可用将不会添加给用户。1.正常 0.禁用
     */
    private Integer status;
    /**
     * 拥有的资源
     */
    private String permissionIds;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getPermissionIds() {
        return permissionIds;
    }

    public void setPermissionIds(String permissionIds) {
        this.permissionIds = permissionIds;
    }
}
