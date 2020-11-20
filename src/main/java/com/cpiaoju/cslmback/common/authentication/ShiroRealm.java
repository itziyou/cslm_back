package com.cpiaoju.cslmback.common.authentication;

import cn.hutool.core.util.StrUtil;
import com.cpiaoju.cslmback.common.authentication.jwt.JWTToken;
import com.cpiaoju.cslmback.common.authentication.jwt.JWTUtil;
import com.cpiaoju.cslmback.system.entity.Menu;
import com.cpiaoju.cslmback.system.entity.Role;
import com.cpiaoju.cslmback.system.entity.User;
import com.cpiaoju.cslmback.system.service.MenuService;
import com.cpiaoju.cslmback.system.service.RoleService;
import com.cpiaoju.cslmback.system.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 自定义实现 ShiroRealm，包含认证和授权两大模块
 *
 * @author ziyou
 */
@Component
public class ShiroRealm extends AuthorizingRealm {

    private UserService userService;
    private RoleService roleService;
    private MenuService menuService;

    @Lazy
    @Autowired
    public void setMenuService(MenuService menuService) {
        this.menuService = menuService;
    }

    @Lazy
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Lazy
    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }

    /**
     * 授权模块，获取用户角色和权限
     *
     * @param principal principal
     * @return AuthorizationInfo 权限信息
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principal) {
        String userName = JWTUtil.getUsername(principal.toString());


        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();

        // 获取用户角色集
        List<Role> roleList = this.roleService.findUserRole(userName);
        Set<String> roleSet = roleList.stream().map(Role::getRoleName).collect(Collectors.toSet());
        simpleAuthorizationInfo.setRoles(roleSet);

        // 获取用户权限集
        List<Menu> permissionList = this.menuService.findUserPermissions(userName);
        Set<String> permissionSet = permissionList.stream().map(Menu::getPerms).collect(Collectors.toSet());
        simpleAuthorizationInfo.setStringPermissions(permissionSet);
        return simpleAuthorizationInfo;
    }

    /**
     * 用户认证
     *
     * @param authenticationToken AuthenticationToken 身份认证 token
     * @return AuthenticationInfo 身份认证信息
     * @throws AuthenticationException 认证相关异常
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        // 这里的 token是从 JWTFilter 的 executeLogin 方法传递过来的，已经经过了解密
        String token = (String) authenticationToken.getCredentials();
        String username = JWTUtil.getUsername(token);

        if (StrUtil.isBlank(username)) {
            throw new AuthenticationException("token校验不通过");
        }
        // 通过用户名到数据库查询用户信息
        User user = this.userService.findByName(username);

        if (user == null) {
            throw new IncorrectCredentialsException("用户名或密码错误！");
        }
        if (!JWTUtil.verify(token, username, user.getPassword())) {
            throw new AuthenticationException("token校验不通过");
        }
        if (User.STATUS_LOCK.equals(user.getStatus())) {
            throw new LockedAccountException("账号已被锁定,请联系管理员！");
        }
        return new SimpleAuthenticationInfo(token, token, getName());
    }

}
