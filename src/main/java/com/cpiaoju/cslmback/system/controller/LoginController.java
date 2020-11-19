package com.cpiaoju.cslmback.system.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.cpiaoju.cslmback.common.annotation.Limit;
import com.cpiaoju.cslmback.common.authentication.jwt.JWTToken;
import com.cpiaoju.cslmback.common.authentication.jwt.JWTUtil;
import com.cpiaoju.cslmback.common.entity.CslmConstant;
import com.cpiaoju.cslmback.common.entity.CslmResponse;
import com.cpiaoju.cslmback.common.exception.CslmException;
import com.cpiaoju.cslmback.common.properties.FebsProperties;
import com.cpiaoju.cslmback.common.properties.ShiroProperties;
import com.cpiaoju.cslmback.common.service.RedisService;
import com.cpiaoju.cslmback.common.util.CslmUtil;
import com.cpiaoju.cslmback.common.util.IpUtil;
import com.cpiaoju.cslmback.common.util.Md5Util;
import com.cpiaoju.cslmback.system.entity.User;
import com.cpiaoju.cslmback.system.manager.UserManager;
import com.cpiaoju.cslmback.system.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.*;


@Slf4j
@Validated
@RestController
public class LoginController {

    @Autowired
    private RedisService redisService;
    @Autowired
    private UserManager userManager;
    @Autowired
    private UserService userService;

    @Autowired
    private FebsProperties properties;
    @Autowired
    private ShiroProperties shiroProperties;
    @Autowired
    private ObjectMapper mapper;

    @PostMapping("/login")
    @Limit(key = "login", period = 60, count = 20, name = "登录接口", prefix = "limit")
    public CslmResponse login(
            @NotBlank(message = "{required}") String username,
            @NotBlank(message = "{required}") String password, HttpServletRequest request) throws Exception {

        password = Md5Util.encrypt(username, password);


        final String errorMessage = "用户名或密码错误";
        User user = this.userManager.getUser(username);

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

        String userId = this.saveTokenToRedis(jwtToken, request);
        user.setId(userId);

        Map<String, Object> userInfo = this.generateUserInfo(jwtToken, user);
        return new CslmResponse().code(HttpStatus.OK).message("认证成功").data(userInfo);
    }

/*    @GetMapping("index/{username}")
    public CslmResponse index(@NotBlank(message = "{required}") @PathVariable String username) {
        Map<String, Object> data = new HashMap<>();
        // 获取系统访问记录
        Long totalVisitCount = loginLogMapper.findTotalVisitCount();
        data.put("totalVisitCount", totalVisitCount);
        Long todayVisitCount = loginLogMapper.findTodayVisitCount();
        data.put("todayVisitCount", todayVisitCount);
        Long todayIp = loginLogMapper.findTodayIp();
        data.put("todayIp", todayIp);
        // 获取近期系统访问记录
        List<Map<String, Object>> lastSevenVisitCount = loginLogMapper.findLastSevenDaysVisitCount(null);
        data.put("lastSevenVisitCount", lastSevenVisitCount);
        User param = new User();
        param.setUsername(username);
        List<Map<String, Object>> lastSevenUserVisitCount = loginLogMapper.findLastSevenDaysVisitCount(param);
        data.put("lastSevenUserVisitCount", lastSevenUserVisitCount);
        return new CslmResponse().data(data);
    }*/


/*    @DeleteMapping("kickout/{id}")
    @RequiresPermissions("user:kickout")
    public void kickout(@NotBlank(message = "{required}") @PathVariable String id) throws Exception {
        String now = DateUtil.formatFullTime(LocalDateTime.now());
        Set<String> userOnlineStringSet = redisService.zrangeByScore(FebsConstant.ACTIVE_USERS_ZSET_PREFIX, now, "+inf");
        ActiveUser kickoutUser = null;
        String kickoutUserString = "";
        for (String userOnlineString : userOnlineStringSet) {
            ActiveUser activeUser = mapper.readValue(userOnlineString, ActiveUser.class);
            if (StringUtils.equals(activeUser.getId(), id)) {
                kickoutUser = activeUser;
                kickoutUserString = userOnlineString;
            }
        }
        if (kickoutUser != null && StringUtils.isNotBlank(kickoutUserString)) {
            // 删除 zset中的记录
            redisService.zrem(FebsConstant.ACTIVE_USERS_ZSET_PREFIX, kickoutUserString);
            // 删除对应的 token缓存
            redisService.del(FebsConstant.TOKEN_CACHE_PREFIX + kickoutUser.getToken() + "." + kickoutUser.getIp());
        }
    }*/

   /* @GetMapping("logout/{id}")
    public CslmResponse logout(@NotBlank(message = "{required}") @PathVariable String id) throws Exception {
        try {
            this.kickout(id);
            return new CslmResponse().message("退出系统成功").code(HttpStatus.OK);
        } catch (Exception e) {

            String message = "退出系统失败";
            log.error(message, e);
            throw new Exception(message);
        }

    }*/

    @PostMapping("regist")
    public void regist(
            @NotBlank(message = "{required}") String username,
            @NotBlank(message = "{required}") String password) throws Exception {
        this.userService.regist(username, password);
    }

    private String saveTokenToRedis(JWTToken token, HttpServletRequest request) {
        String ip = IpUtil.getIpAddr(request);

        // redis 中存储这个加密 token，key = 前缀 + 加密 token + .ip
        this.redisService.set(CslmConstant.TOKEN_CACHE_PREFIX + token.getToken() + StringPool.DOT + ip, token.getToken(), shiroProperties.getJwtTimeOut() * 1000);

        return RandomUtil.randomString(20);
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

        Set<String> roles = this.userManager.getUserRoles(username);
        userInfo.put("roles", roles);

        Set<String> permissions = this.userManager.getUserPermissions(username);
        userInfo.put("permissions", permissions);

        user.setPassword("it's a secret");
        userInfo.put("user", user);
        return userInfo;
    }
}
