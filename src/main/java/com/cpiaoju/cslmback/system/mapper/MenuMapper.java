package com.cpiaoju.cslmback.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cpiaoju.cslmback.system.entity.Menu;

import java.util.List;

/**
 * @author ziyou
 */
public interface MenuMapper extends BaseMapper<Menu> {

    List<Menu> findUserPermissions(String userName);

    List<Menu> findUserMenus(String userName);

    /**
     * 查找当前菜单/按钮关联的用户 ID
     *
     * @param menuId menuId
     * @return 用户 ID集合
     */
    List<String> findUserIdsByMenuId(String menuId);
}