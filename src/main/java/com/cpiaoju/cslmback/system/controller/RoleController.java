package com.cpiaoju.cslmback.system.controller;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.cpiaoju.cslmback.common.controller.BaseController;
import com.cpiaoju.cslmback.common.entity.CslmResponse;
import com.cpiaoju.cslmback.common.entity.QueryRequest;
import com.cpiaoju.cslmback.system.entity.Role;
import com.cpiaoju.cslmback.system.entity.RoleMenu;
import com.cpiaoju.cslmback.system.service.RoleMenuServie;
import com.cpiaoju.cslmback.system.service.RoleService;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Validated
@RestController
@RequestMapping("role")
public class RoleController extends BaseController {

    @Autowired
    private RoleService roleService;
    @Autowired
    private RoleMenuServie roleMenuServie;

    private String message;

    @GetMapping
    @RequiresPermissions("role:view")
    public Map<String, Object> roleList(QueryRequest queryRequest, Role role) {
        return getDataTable(roleService.findRoles(role, queryRequest));
    }

    @GetMapping("check/{roleName}")
    public boolean checkRoleName(@NotBlank(message = "{required}") @PathVariable String roleName) {
        Role result = this.roleService.findByName(roleName);
        return result == null;
    }

    @GetMapping("menu/{roleId}")
    public List<String> getRoleMenus(@NotBlank(message = "{required}") @PathVariable String roleId) {
        List<RoleMenu> list = this.roleMenuServie.getRoleMenusByRoleId(roleId);
        return list.stream().map(roleMenu -> String.valueOf(roleMenu.getMenuId())).collect(Collectors.toList());
    }

    @PostMapping
    @RequiresPermissions("role:add")
    public CslmResponse addRole(@RequestBody @Valid Role role) {
        this.roleService.createRole(role);
        return new CslmResponse().code(HttpStatus.OK).message("新增角色成功");
    }
/*
    @DeleteMapping("/{roleIds}")
    @RequiresPermissions("role:delete")
    public CslmResponse deleteRoles(@NotBlank(message = "{required}") @PathVariable String roleIds) throws FebsException {
        try {
            String[] ids = roleIds.split(StringPool.COMMA);
            this.roleService.deleteRoles(ids);
            return new FebsResponse().code("200").message("删除角色成功").status("success");
        } catch (Exception e) {
            message = "删除角色失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }

    @PutMapping
    @RequiresPermissions("role:update")
    public FebsResponse updateRole(@RequestBody @Valid Role role) throws FebsException {
        try {
            this.roleService.updateRole(role);
            return new FebsResponse().code("200").message("修改角色成功").status("success");
        } catch (Exception e) {
            message = "修改角色失败";
            log.error(message, e);
            throw new FebsException(message);
        }
    }*/

}
