package com.cpiaoju.cslmback.system.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cpiaoju.cslmback.common.entity.CslmConstant;
import com.cpiaoju.cslmback.common.entity.Tree;
import com.cpiaoju.cslmback.common.entity.router.RouterMeta;
import com.cpiaoju.cslmback.common.entity.router.VueRouter;
import com.cpiaoju.cslmback.common.service.RedisService;
import com.cpiaoju.cslmback.common.util.TreeUtil;
import com.cpiaoju.cslmback.system.entity.Menu;
import com.cpiaoju.cslmback.system.mapper.MenuMapper;
import com.cpiaoju.cslmback.system.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ziyou
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    private final RedisService redisService;

    @Override
    public Set<String> findUserPermissions(String username) {
        Object permRedis = redisService.get(CslmConstant.USET_PERM + username);
        if (permRedis == null) {
            List<Menu> menuList = this.baseMapper.findUserPermissions(username);
            if (menuList == null || menuList.size() == 0) {
                return null;
            }
            Set<String> permSet = menuList.stream().map(Menu::getPerms).collect(Collectors.toSet());
            redisService.set(CslmConstant.USET_PERM + username, JSONUtil.toJsonStr(menuList));
            return permSet;
        } else {
            JSONArray permJSONArray = JSONUtil.parseArray(permRedis);
            List<Menu> menuList = JSONUtil.toList(permJSONArray, Menu.class);
            Set<String> permSet = menuList.stream().map(Menu::getPerms).collect(Collectors.toSet());
            return permSet;
        }
    }

    @Override
    public ArrayList<VueRouter<Menu>> findUserMenus(String username) {
        List<VueRouter<Menu>> routes = new ArrayList<>();
        List<Menu> menus = this.baseMapper.findUserMenus(username);
        menus.forEach(menu -> {
            VueRouter<Menu> route = new VueRouter<>();
            route.setId(menu.getMenuId().toString());
            route.setParentId(menu.getParentId().toString());
            route.setIcon(menu.getIcon());
            route.setPath(menu.getPath());
            route.setComponent(menu.getComponent());
            route.setName(menu.getMenuName());
            route.setMeta(new RouterMeta(true, null));
            routes.add(route);
        });
        return TreeUtil.buildVueRouter(routes);

    }

    @Override
    public Map<String, Object> findMenus(Menu menu) {
        Map<String, Object> result = new HashMap<>();
        try {
            LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
            findMenuCondition(queryWrapper, menu);
            List<Menu> menus = baseMapper.selectList(queryWrapper);

            List<Tree<Menu>> trees = new ArrayList<>();
            List<String> ids = new ArrayList<>();
            buildTrees(trees, menus, ids);

            result.put("ids", ids);
            if (StrUtil.equals(menu.getType(), CslmConstant.TYPE_BUTTON)) {
                result.put("rows", trees);
            } else {
                Tree<Menu> menuTree = TreeUtil.build(trees);
                result.put("rows", menuTree);
            }

            result.put("total", menus.size());
        } catch (NumberFormatException e) {
            log.error("查询菜单失败", e);
            result.put("rows", null);
            result.put("total", 0);
        }
        return result;
    }


    @Override
    public List<Menu> findMenuList(Menu menu) {
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        findMenuCondition(queryWrapper, menu);
        queryWrapper.orderByAsc(Menu::getMenuId);
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional
    public void createMenu(Menu menu) {
        menu.setCreateTime(new Date());
        this.setMenu(menu);
        this.save(menu);
    }

    @Override
    @Transactional
    public void updateMenu(Menu menu) {
        menu.setModifyTime(new Date());
        this.setMenu(menu);
        baseMapper.updateById(menu);

        // 查找与这些菜单/按钮关联的用户
        List<String> userIds = this.baseMapper.findUserIdsByMenuId(String.valueOf(menu.getMenuId()));
        // 重新将这些用户的角色和权限缓存到 Redis中
        //this.userManager.loadUserPermissionRoleRedisCache(userIds);
    }

    @Override
    @Transactional
    public void deleteMeuns(String[] menuIds) {
        this.delete(Arrays.asList(menuIds));
        for (String menuId : menuIds) {
            // 查找与这些菜单/按钮关联的用户
            List<String> userIds = this.baseMapper.findUserIdsByMenuId(String.valueOf(menuId));
            // 重新将这些用户的角色和权限缓存到 Redis中
            //this.userManager.loadUserPermissionRoleRedisCache(userIds);
        }
    }

    private void buildTrees(List<Tree<Menu>> trees, List<Menu> menus, List<String> ids) {
        menus.forEach(menu -> {
            ids.add(menu.getMenuId().toString());
            Tree<Menu> tree = new Tree<>();
            tree.setId(menu.getMenuId().toString());
            tree.setKey(tree.getId());
            tree.setParentId(menu.getParentId().toString());
            tree.setText(menu.getMenuName());
            tree.setTitle(tree.getText());
            tree.setIcon(menu.getIcon());
            tree.setComponent(menu.getComponent());
            tree.setCreateTime(menu.getCreateTime());
            tree.setModifyTime(menu.getModifyTime());
            tree.setPath(menu.getPath());
            tree.setOrder(menu.getOrderNum());
            tree.setPermission(menu.getPerms());
            tree.setType(menu.getType());
            trees.add(tree);
        });
    }

    private void setMenu(Menu menu) {
        if (menu.getParentId() == null) {
            menu.setParentId(0L);
        }
        if (Menu.TYPE_BUTTON.equals(menu.getType())) {
            menu.setPath(null);
            menu.setIcon(null);
            menu.setComponent(null);
        }
    }

    private void findMenuCondition(LambdaQueryWrapper<Menu> queryWrapper, Menu menu) {
        if (StrUtil.isNotBlank(menu.getMenuName())) {
            queryWrapper.eq(Menu::getMenuName, menu.getMenuName());
        }
        if (StrUtil.isNotBlank(menu.getType())) {
            queryWrapper.eq(Menu::getType, menu.getType());
        }
        if (StrUtil.isNotBlank(menu.getCreateTimeFrom()) && StrUtil.isNotBlank(menu.getCreateTimeTo())) {
            queryWrapper
                    .ge(Menu::getCreateTime, menu.getCreateTimeFrom())
                    .le(Menu::getCreateTime, menu.getCreateTimeTo());
        }
    }


    private void delete(List<String> menuIds) {
        removeByIds(menuIds);

        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Menu::getParentId, menuIds);
        List<Menu> menus = baseMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(menus)) {
            List<String> menuIdList = new ArrayList<>();
            menus.forEach(m -> menuIdList.add(String.valueOf(m.getMenuId())));
            this.delete(menuIdList);
        }
    }

}
