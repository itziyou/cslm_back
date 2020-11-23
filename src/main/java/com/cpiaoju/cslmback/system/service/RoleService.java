package com.cpiaoju.cslmback.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cpiaoju.cslmback.common.entity.QueryRequest;
import com.cpiaoju.cslmback.system.entity.Role;

import java.util.Set;

public interface RoleService extends IService<Role> {

    IPage<Role> findRoles(Role role, QueryRequest request);

    Set<String> findUserRole(String userName);

    Role findByName(String roleName);

    void createRole(Role role);

    void deleteRoles(String[] roleIds);

    void updateRole(Role role);
}
