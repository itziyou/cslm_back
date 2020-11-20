package com.cpiaoju.cslmback.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cpiaoju.cslmback.common.entity.QueryRequest;
import com.cpiaoju.cslmback.common.service.CacheService;
import com.cpiaoju.cslmback.common.util.Md5Util;
import com.cpiaoju.cslmback.system.entity.User;
import com.cpiaoju.cslmback.system.entity.UserRole;
import com.cpiaoju.cslmback.system.mapper.UserMapper;
import com.cpiaoju.cslmback.system.service.UserRoleService;
import com.cpiaoju.cslmback.system.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author ziyou
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final CacheService cacheService;
    private final UserRoleService userRoleService;


    @Override
    public User findByName(String username) {
        return baseMapper.findDetail(username);
    }


    @Override
    public IPage<User> findUserDetail(User user, QueryRequest request) {
        try {
            Page<User> page = new Page<>();
            //SortUtil.handlePageSort(request, page, "userId", FebsConstant.ORDER_ASC, false);
            return this.baseMapper.findUserDetail(page, user);
        } catch (Exception e) {
            log.error("查询用户异常", e);
            return null;
        }
    }

    @Override
    @Transactional
    public void updateLoginTime(String username) {
        User user = new User();
        user.setLastLoginTime(new Date());

        this.baseMapper.update(user, new LambdaQueryWrapper<User>().eq(User::getUsername, username));

        // 重新将用户信息加载到 redis中
        cacheService.saveUser(username);
    }

    @Override
    @Transactional
    public void createUser(User user) {
        // 创建用户
        user.setCreateTime(new Date());
        user.setAvatar(User.DEFAULT_AVATAR);
        user.setPassword(Md5Util.encrypt(user.getUsername(), User.DEFAULT_PASSWORD));
        save(user);

        // 保存用户角色
        String[] roles = user.getRoleId().split(StringPool.COMMA);
        setUserRoles(user, roles);
        //查询用户角色

        // 将用户相关信息保存到 Redis中

    }

    @Override
    @Transactional
    public void updateUser(User user) {
        // 更新用户
        user.setPassword(null);
        user.setModifyTime(new Date());
        updateById(user);

        userRoleService.getBaseMapper().delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, user.getUserId()));

        String[] roles = user.getRoleId().split(StringPool.COMMA);
        setUserRoles(user, roles);

        // 重新将用户信息，用户角色信息，用户权限信息 加载到 redis中
        cacheService.saveUser(user.getUsername());
        cacheService.saveRoles(user.getUsername());
        cacheService.savePermissions(user.getUsername());
    }

    @Override
    @Transactional
    public void deleteUsers(String[] userIds) {
        // 先删除相应的缓存
        //this.userManager.deleteUserRedisCache(userIds);

        List<String> list = Arrays.asList(userIds);

        removeByIds(list);

        // 删除用户角色
        this.userRoleService.deleteUserRolesByUserId(userIds);
    }

    @Override
    @Transactional
    public void updateProfile(User user) {
        updateById(user);
        // 重新缓存用户信息
        cacheService.saveUser(user.getUsername());
    }

    @Override
    @Transactional
    public void updateAvatar(String username, String avatar) {
        User user = new User();
        user.setAvatar(avatar);

        this.baseMapper.update(user, new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        // 重新缓存用户信息
        cacheService.saveUser(username);
    }

    @Override
    @Transactional
    public void updatePassword(String username, String password) {
        User user = new User();
        user.setPassword(Md5Util.encrypt(username, password));

        this.baseMapper.update(user, new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        // 重新缓存用户信息
        cacheService.saveUser(username);
    }

    @Override
    @Transactional
    public void regist(String username, String password) {
        User user = new User();
        user.setPassword(Md5Util.encrypt(username, password));
        user.setUsername(username);
        user.setCreateTime(new Date());
        user.setStatus(User.STATUS_LOCK);
        user.setSsex(User.SEX_UNKNOW);
        user.setAvatar(User.DEFAULT_AVATAR);
        user.setDescription("注册用户");
        this.save(user);

        UserRole ur = new UserRole();
        ur.setUserId(user.getUserId());
        ur.setRoleId(2L); // 注册用户角色 ID
        this.userRoleService.getBaseMapper().insert(ur);

        //获取用户部门员工
        // 将用户相关信息保存到 Redis中
        //userManager.loadUserRedisCache(user);
    }

    @Override
    @Transactional
    public void resetPassword(String[] usernames) {
        for (String username : usernames) {

            User user = new User();
            user.setPassword(Md5Util.encrypt(username, User.DEFAULT_PASSWORD));

            this.baseMapper.update(user, new LambdaQueryWrapper<User>().eq(User::getUsername, username));
            // 重新将用户信息加载到 redis中
            cacheService.saveUser(username);
        }

    }

    private void setUserRoles(User user, String[] roles) {
        Arrays.stream(roles).forEach(roleId -> {
            UserRole ur = new UserRole();
            ur.setUserId(user.getUserId());
            ur.setRoleId(Long.valueOf(roleId));
            this.userRoleService.getBaseMapper().insert(ur);
        });
    }

    private void loadUserRedisCache(User user) {
        // 缓存用户
        cacheService.saveUser(user);
        // 缓存用户角色
        cacheService.saveRoles(user.getUsername());
        // 缓存用户权限
        cacheService.savePermissions(user.getUsername());
    }

}
