package com.cpiaoju.cslmback.common.service;


import com.cpiaoju.cslmback.system.entity.Menu;
import com.cpiaoju.cslmback.system.entity.Role;
import com.cpiaoju.cslmback.system.entity.User;

import java.util.List;

/**
 * @author ziyou
 */
public interface CacheService {

    /**
     * 从缓存中获取用户
     *
     * @param username 用户名
     * @return User
     */
    User getUser(String username);

    /**
     * 从缓存中获取用户角色
     *
     * @param username 用户名
     * @return 角色集
     */
    List<Role> getRoles(String username);

    /**
     * 从缓存中获取用户权限
     *
     * @param username 用户名
     * @return 权限集
     */
    List<Menu> getPermissions(String username);

    /**
     * 缓存用户信息，只有当用户信息是查询出来的，完整的，才应该调用这个方法
     * 否则需要调用下面这个重载方法
     *
     * @param user 用户信息
     */
    void saveUser(User user);

    /**
     * 缓存用户信息
     *
     * @param username 用户名
     */
    void saveUser(String username);

    /**
     * 缓存用户角色信息
     *
     * @param username 用户名
     */
    void saveRoles(String username);

    /**
     * 缓存用户权限信息
     *
     * @param username 用户名
     */
    void savePermissions(String username);

    /**
     * 删除用户信息
     *
     * @param username 用户名
     */
    void deleteUser(String username);

    /**
     * 删除用户角色信息
     *
     * @param username 用户名
     */
    void deleteRoles(String username);

    /**
     * 删除用户权限信息
     *
     * @param username 用户名
     */
    void deletePermissions(String username);

}
