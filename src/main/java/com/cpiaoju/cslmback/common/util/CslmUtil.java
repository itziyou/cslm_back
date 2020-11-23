package com.cpiaoju.cslmback.common.util;


import com.cpiaoju.cslmback.common.authentication.jwt.JWTUtil;
import com.cpiaoju.cslmback.system.entity.User;
import com.cpiaoju.cslmback.system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Cslm工具类
 *
 * @author ziyou
 */
@Slf4j
public class CslmUtil {

    /**
     * 获取当前操作用户
     *
     * @return 用户信息
     */
    public static User getCurrentUser() {
        String token = (String) SecurityUtils.getSubject().getPrincipal();
        String username = JWTUtil.getUsername(token);
        UserService userService = SpringContextUtil.getBean(UserService.class);
        return userService.findByName(username);
    }

    /**
     * 正则校验
     *
     * @param regex 正则表达式字符串
     * @param value 要匹配的字符串
     * @return 正则校验结果
     */
    public static boolean match(String regex, String value) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }


}
