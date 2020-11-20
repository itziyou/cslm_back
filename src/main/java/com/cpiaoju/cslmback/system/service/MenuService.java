package com.cpiaoju.cslmback.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cpiaoju.cslmback.common.entity.router.VueRouter;
import com.cpiaoju.cslmback.system.entity.Menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface MenuService extends IService<Menu> {

    Set<String> findUserPermissions(String username);

    ArrayList<VueRouter<Menu>> findUserMenus(String username);

    Map<String, Object> findMenus(Menu menu);

    List<Menu> findMenuList(Menu menu);

    void createMenu(Menu menu);

    void updateMenu(Menu menu) throws Exception;

    /**
     * 递归删除菜单/按钮
     *
     * @param menuIds menuIds
     */
    void deleteMeuns(String[] menuIds) throws Exception;

}
