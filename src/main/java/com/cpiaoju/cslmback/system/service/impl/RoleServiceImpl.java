package com.cpiaoju.cslmback.system.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cpiaoju.cslmback.common.entity.CslmConstant;
import com.cpiaoju.cslmback.common.entity.QueryRequest;
import com.cpiaoju.cslmback.common.service.RedisService;
import com.cpiaoju.cslmback.system.entity.Role;
import com.cpiaoju.cslmback.system.entity.RoleMenu;
import com.cpiaoju.cslmback.system.mapper.RoleMapper;
import com.cpiaoju.cslmback.system.mapper.RoleMenuMapper;
import com.cpiaoju.cslmback.system.service.RoleMenuServie;
import com.cpiaoju.cslmback.system.service.RoleService;
import com.cpiaoju.cslmback.system.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author ziyou
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    private final RoleMenuMapper roleMenuMapper;
    private final UserRoleService userRoleService;
    private final RoleMenuServie roleMenuService;
    private final RedisService redisService;


    @Override
    public IPage<Role> findRoles(Role role, QueryRequest request) {
        try {
            LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();

            if (StrUtil.isNotBlank(role.getRoleName())) {
                queryWrapper.eq(Role::getRoleName, role.getRoleName());
            }
            if (StrUtil.isNotBlank(role.getCreateTimeFrom()) && StrUtil.isNotBlank(role.getCreateTimeTo())) {
                queryWrapper
                        .ge(Role::getCreateTime, role.getCreateTimeFrom())
                        .le(Role::getCreateTime, role.getCreateTimeTo());
            }
            Page<Role> page = new Page<>();
            //SortUtil.handlePageSort(request, page, true);
            return this.page(page, queryWrapper);
        } catch (Exception e) {
            log.error("获取角色信息失败", e);
            return null;
        }
    }

    @Override
    public Set<String> findUserRole(String userName) {
        Object roleRedis = redisService.get(CslmConstant.USET_ROLE + userName);
        if (roleRedis == null) {
            List<Role> roleList = baseMapper.findUserRole(userName);
            if (roleList == null || roleList.size() == 0) {
                return null;
            }
            Set<String> roleSet = roleList.stream().map(Role::getRoleName).collect(Collectors.toSet());
            redisService.set(CslmConstant.USET_ROLE + userName, JSONUtil.toJsonStr(roleList));
            return roleSet;
        } else {
            JSONArray roleJSONArray = JSONUtil.parseArray(roleRedis);
            List<Role> roleList = JSONUtil.toList(roleJSONArray, Role.class);
            Set<String> roleSet = roleList.stream().map(Role::getRoleName).collect(Collectors.toSet());
            return roleSet;
        }
    }

    @Override
    public Role findByName(String roleName) {
        return baseMapper.selectOne(new LambdaQueryWrapper<Role>().eq(Role::getRoleName, roleName));
    }

    @Override
    public void createRole(Role role) {
        role.setCreateTime(new Date());
        this.save(role);

        String[] menuIds = role.getMenuId().split(StringPool.COMMA);
        this.setRoleMenus(role, menuIds);
    }

    @Override
    public void deleteRoles(String[] roleIds) throws Exception {
        // 查找这些角色关联了那些用户
        List<String> userIds = this.userRoleService.findUserIdsByRoleId(roleIds);

        List<String> list = Arrays.asList(roleIds);

        baseMapper.deleteBatchIds(list);

        this.roleMenuService.deleteRoleMenusByRoleId(roleIds);
        this.userRoleService.deleteUserRolesByRoleId(roleIds);

        // 重新将这些用户的角色和权限缓存到 Redis中
        //this.userManager.loadUserPermissionRoleRedisCache(userIds);

    }

    @Override
    public void updateRole(Role role) throws Exception {
        // 查找这些角色关联了那些用户
        String[] roleId = {String.valueOf(role.getRoleId())};
        List<String> userIds = this.userRoleService.findUserIdsByRoleId(roleId);

        role.setModifyTime(new Date());
        baseMapper.updateById(role);

        roleMenuMapper.delete(new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getRoleId, role.getRoleId()));

        String[] menuIds = role.getMenuId().split(StringPool.COMMA);
        setRoleMenus(role, menuIds);

        // 重新将这些用户的角色和权限缓存到 Redis中
        //this.userManager.loadUserPermissionRoleRedisCache(userIds);
    }

    private void setRoleMenus(Role role, String[] menuIds) {
        Arrays.stream(menuIds).forEach(menuId -> {
            RoleMenu rm = new RoleMenu();
            rm.setMenuId(Long.valueOf(menuId));
            rm.setRoleId(role.getRoleId());
            this.roleMenuMapper.insert(rm);
        });
    }
}
