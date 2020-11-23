package com.cpiaoju.cslmback.system.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.cpiaoju.cslmback.common.annotation.Limit;
import com.cpiaoju.cslmback.common.authentication.jwt.JWTToken;
import com.cpiaoju.cslmback.common.authentication.jwt.JWTUtil;
import com.cpiaoju.cslmback.common.entity.CslmConstant;
import com.cpiaoju.cslmback.common.entity.CslmResponse;
import com.cpiaoju.cslmback.common.exception.CslmException;
import com.cpiaoju.cslmback.common.properties.ShiroProperties;
import com.cpiaoju.cslmback.common.service.RedisService;
import com.cpiaoju.cslmback.common.util.Md5Util;
import com.cpiaoju.cslmback.system.entity.User;
import com.cpiaoju.cslmback.system.service.MenuService;
import com.cpiaoju.cslmback.system.service.RoleService;
import com.cpiaoju.cslmback.system.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * @author ziyou
 */
@Api(tags = "登录模块")
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final RedisService redisService;
    private final UserService userService;
    private final RoleService roleService;
    private final MenuService menuService;
    private final ShiroProperties shiroProperties;


    @ApiOperation(value = "登录接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true),
            @ApiImplicitParam(name = "password", value = "密码", required = true)
    })
    @PostMapping("/login")
    @Limit(key = "login", period = 60, count = 20, name = "登录接口", prefix = "limit")
    public CslmResponse login(
            @NotBlank(message = "{required}") String username,
            @NotBlank(message = "{required}") String password) {

        password = Md5Util.encrypt(username, password);

        final String errorMessage = "用户名或密码错误";
        User user = this.userService.findByName(username);

        if (user == null || password == null) {
            throw new CslmException(errorMessage);
        }
        if (!StrUtil.equals(user.getPassword(), password)) {
            throw new CslmException(errorMessage);
        }
        if (User.STATUS_LOCK.equals(user.getStatus())) {
            throw new CslmException("账号已被锁定,请联系管理员！");
        }

        // 更新用户登录时间
        this.userService.updateLoginTime(username);

        String token = JWTUtil.sign(username, password);
        LocalDateTime expireTime = LocalDateTime.now().plusSeconds(shiroProperties.getJwtTimeOut());

        String expireTimeStr = DateUtil.format(expireTime, "yyyyMMddHHmmss");
        JWTToken jwtToken = new JWTToken(token, expireTimeStr);

        // redis 中存储这个 token，key = 前缀 + userId
        this.redisService.set(CslmConstant.TOKEN_CACHE_PREFIX + user.getUserId(), token, shiroProperties.getJwtTimeOut());

        Map<String, Object> userInfo = this.generateUserInfo(jwtToken, user);
        return new CslmResponse().code(HttpStatus.OK).message("认证成功").data(userInfo);
    }


    @GetMapping("logout/{userId}")
    public CslmResponse logout(@PathVariable Long userId) throws Exception {
        try {
            redisService.del(CslmConstant.TOKEN_CACHE_PREFIX + userId);
            return new CslmResponse().message("退出系统成功").code(HttpStatus.OK);
        } catch (Exception e) {
            String message = "退出系统失败";
            log.error(message, e);
            throw new Exception(message);
        }

    }

    @PostMapping("regist")
    public void regist(
            @NotBlank(message = "{required}") String username,
            @NotBlank(message = "{required}") String password) {
        this.userService.regist(username, password);
    }

    /**
     * 生成前端需要的用户信息，包括：
     * 1. token
     * 2. Vue Router
     * 3. 用户角色
     * 4. 用户权限
     * 5. 前端系统个性化配置信息
     *
     * @param token token
     * @param user  用户信息
     * @return UserInfo
     */
    private Map<String, Object> generateUserInfo(JWTToken token, User user) {
        String username = user.getUsername();
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("token", token.getToken());
        userInfo.put("exipreTime", token.getExipreAt());

        Set<String> roles = this.roleService.findUserRole(username);
        userInfo.put("roles", roles);

        Set<String> permissions = this.menuService.findUserPermissions(username);
        userInfo.put("permissions", permissions);

        user.setPassword("it's a secret");
        userInfo.put("user", user);
        return userInfo;
    }
}
