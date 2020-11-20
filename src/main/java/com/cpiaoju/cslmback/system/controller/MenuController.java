package com.cpiaoju.cslmback.system.controller;

import com.cpiaoju.cslmback.common.controller.BaseController;
import com.cpiaoju.cslmback.common.entity.router.VueRouter;
import com.cpiaoju.cslmback.system.entity.Menu;
import com.cpiaoju.cslmback.system.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping("/menu")
public class MenuController extends BaseController {

    private String message;

    @Autowired
    private MenuService menuService;

    @GetMapping("/{username}")
    public ArrayList<VueRouter<Menu>> getUserRouters(@NotBlank(message = "{required}") @PathVariable String username) {
        return this.menuService.findUserMenus(username);
    }

    @GetMapping
    @RequiresPermissions("menu:view")
    public Map<String, Object> menuList(Menu menu) {
        return this.menuService.findMenus(menu);
    }

/*    @PostMapping
    @RequiresPermissions("menu:add")
    public CslmResponse addMenu(@RequestBody @Valid Menu menu){
        try {
            this.menuService.createMenu(menu);
            return new CslmResponse().code("200").message("新增菜单/按钮成功").status("success");
        } catch (Exception e) {
            message = "新增菜单/按钮失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    @DeleteMapping("/{menuIds}")
    @RequiresPermissions("menu:delete")
    public FebsResponse deleteMenus(@NotBlank(message = "{required}") @PathVariable String menuIds) throws FebsException {
        try {
            String[] ids = menuIds.split(StringPool.COMMA);
            this.menuService.deleteMeuns(ids);
            return new FebsResponse().code("200").message("删除菜单/按钮成功").status("success");
        } catch (Exception e) {
            message = "删除菜单/按钮失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    @PutMapping
    @RequiresPermissions("menu:update")
    public FebsResponse updateMenu(@RequestBody @Valid Menu menu) throws FebsException {
        try {
            this.menuService.updateMenu(menu);
            return new FebsResponse().code("200").message("修改菜单/按钮成功").status("success");
        } catch (Exception e) {
            message = "修改菜单/按钮失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }*/


}
