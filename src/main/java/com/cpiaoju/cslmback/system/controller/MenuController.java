package com.cpiaoju.cslmback.system.controller;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.cpiaoju.cslmback.common.controller.BaseController;
import com.cpiaoju.cslmback.common.entity.CslmResponse;
import com.cpiaoju.cslmback.common.entity.router.VueRouter;
import com.cpiaoju.cslmback.system.entity.Menu;
import com.cpiaoju.cslmback.system.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author ziyou
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController extends BaseController {


    private final MenuService menuService;

    @GetMapping("/{username}")
    public ArrayList<VueRouter<Menu>> getUserRouters(@NotBlank(message = "{required}") @PathVariable String username) {
        return this.menuService.findUserMenus(username);
    }

    @GetMapping
    @RequiresPermissions("menu:view")
    public Map<String, Object> menuList(Menu menu) {
        return this.menuService.findMenus(menu);
    }

    @PostMapping
    @RequiresPermissions("menu:add")
    public CslmResponse addMenu(@RequestBody @Valid Menu menu) {
        this.menuService.createMenu(menu);
        return new CslmResponse().code(HttpStatus.OK).message("新增菜单/按钮成功");
    }

    @DeleteMapping("/{menuIds}")
    @RequiresPermissions("menu:delete")
    public CslmResponse deleteMenus(@NotBlank(message = "{required}") @PathVariable String menuIds) {
        String[] ids = menuIds.split(StringPool.COMMA);
        this.menuService.deleteMeuns(ids);
        return new CslmResponse().code(HttpStatus.OK).message("删除菜单/按钮成功");
    }

    @PutMapping
    @RequiresPermissions("menu:update")
    public CslmResponse updateMenu(@RequestBody @Valid Menu menu) {
        this.menuService.updateMenu(menu);
        return new CslmResponse().code(HttpStatus.OK).message("修改菜单/按钮成功");
    }


}
