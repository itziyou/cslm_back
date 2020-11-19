package com.cpiaoju.cslmback.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cpiaoju.cslmback.system.entity.RoleMenu;

import java.util.List;

public interface RoleMenuServie extends IService<RoleMenu> {

    void deleteRoleMenusByRoleId(String[] roleIds);

    void deleteRoleMenusByMenuId(String[] menuIds);

    List<RoleMenu> getRoleMenusByRoleId(String roleId);
}
