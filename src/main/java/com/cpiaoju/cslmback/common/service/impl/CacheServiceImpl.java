package com.cpiaoju.cslmback.common.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.cpiaoju.cslmback.common.entity.CslmConstant;
import com.cpiaoju.cslmback.common.exception.CslmException;
import com.cpiaoju.cslmback.common.service.CacheService;
import com.cpiaoju.cslmback.common.service.RedisService;
import com.cpiaoju.cslmback.system.entity.Menu;
import com.cpiaoju.cslmback.system.entity.Role;
import com.cpiaoju.cslmback.system.entity.User;
import com.cpiaoju.cslmback.system.mapper.UserMapper;
import com.cpiaoju.cslmback.system.service.MenuService;
import com.cpiaoju.cslmback.system.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ziyou
 */
@Service
@RequiredArgsConstructor
public class CacheServiceImpl implements CacheService {

    private final RedisService redisService;

    private final RoleService roleService;

    private final MenuService menuService;

    private final UserMapper userMapper;

    @Override
    public User getUser(String username) {
        Object usernameObj = this.redisService.get(CslmConstant.USER_CACHE_PREFIX + username);
        if (usernameObj != null) {
            User user = JSONUtil.toBean(usernameObj.toString(), User.class);
            return user;
        } else {
            throw new CslmException("");
        }

    }

    @Override
    public List<Role> getRoles(String username) {
        Object roleListObj = this.redisService.get(CslmConstant.USER_ROLE_CACHE_PREFIX + username);
        if (roleListObj != null) {
            JSONArray jsonArray = JSONUtil.parseArray(roleListObj);
            List<Role> roles = JSONUtil.toList(jsonArray, Role.class);
            return roles;
        } else {
            throw new CslmException("");
        }
    }

    @Override
    public List<Menu> getPermissions(String username) {
        Object permissionListObj = this.redisService.get(CslmConstant.USER_PERMISSION_CACHE_PREFIX + username);
        if (permissionListObj != null) {
            JSONArray jsonArray = JSONUtil.parseArray(permissionListObj);
            List<Menu> menus = JSONUtil.toList(jsonArray, Menu.class);
            return menus;
        } else {
            throw new CslmException("");
        }
    }

    @Override
    public void saveUser(User user) {
        String username = user.getUsername();
        this.deleteUser(username);
        redisService.set(CslmConstant.USER_CACHE_PREFIX + username, JSONUtil.toJsonStr(user));
    }

    @Override
    public void saveUser(String username) {
        User user = userMapper.findDetail(username);
        this.deleteUser(username);
        redisService.set(CslmConstant.USER_CACHE_PREFIX + username, JSONUtil.toJsonStr(user));
    }

    @Override
    public void saveRoles(String username) {
        List<Role> roleList = this.roleService.findUserRole(username);
        if (!roleList.isEmpty()) {
            this.deleteRoles(username);
            redisService.set(CslmConstant.USER_ROLE_CACHE_PREFIX + username, JSONUtil.toJsonStr(roleList));
        }

    }

    @Override
    public void savePermissions(String username) {
        List<Menu> permissionList = this.menuService.findUserPermissions(username);
        if (!permissionList.isEmpty()) {
            this.deletePermissions(username);
            redisService.set(CslmConstant.USER_PERMISSION_CACHE_PREFIX + username, JSONUtil.toJsonStr(permissionList));
        }
    }

    @Override
    public void deleteUser(String username) {
        username = username.toLowerCase();
        redisService.del(CslmConstant.USER_CACHE_PREFIX + username);
    }

    @Override
    public void deleteRoles(String username) {
        username = username.toLowerCase();
        redisService.del(CslmConstant.USER_ROLE_CACHE_PREFIX + username);
    }

    @Override
    public void deletePermissions(String username) {
        username = username.toLowerCase();
        redisService.del(CslmConstant.USER_PERMISSION_CACHE_PREFIX + username);
    }

}
