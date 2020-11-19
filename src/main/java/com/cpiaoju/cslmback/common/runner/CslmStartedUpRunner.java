package com.cpiaoju.cslmback.common.runner;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.cpiaoju.cslmback.common.entity.CslmConstant;
import com.cpiaoju.cslmback.common.properties.FebsProperties;
import com.cpiaoju.cslmback.common.properties.ShiroProperties;
import com.cpiaoju.cslmback.common.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author ziyou
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CslmStartedUpRunner implements ApplicationRunner {

    public static final String FULL_TIME_SPLIT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private final ConfigurableApplicationContext context;
    private final FebsProperties febsProperties;
    private final ShiroProperties shiroProperties;
    private final RedisService redisService;

    @Value("${server.port:8080}")
    private String port;
    @Value("${server.servlet.context-path:}")
    private String contextPath;
    @Value("${spring.profiles.active}")
    private String active;
    @Value("${spring.application.package-time:'1970-01-01T00:00:00Z'}")
    private String packageTime;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            // 测试 Redis连接是否正常
            redisService.hasKey("cslm_test");
        } catch (Exception e) {
            log.error(" ____   __    _   _ ");
            log.error("| |_   / /\\  | | | |");
            log.error("|_|   /_/--\\ |_| |_|__");
            log.error("                        ");
            log.error("CSLM_BACK启动失败，{}", e.getMessage());
            log.error("Redis连接异常，请检查Redis连接配置并确保Redis服务已启动");
            // 关闭 FEBS
            context.close();
        }
        if (context.isActive()) {
            InetAddress address = InetAddress.getLocalHost();
            String url = String.format("http://%s:%s", address.getHostAddress(), port);
            //String loginUrl = shiroProperties.getLoginUrl();
            String loginUrl =" shiroProperties.getLoginUrl()";
            if (StrUtil.isNotBlank(contextPath)) {
                url += contextPath;
            }
            if (StrUtil.isNotBlank(loginUrl)) {
                url += loginUrl;
            }
            log.info(" __    ___   _      ___   _     ____ _____  ____ ");
            log.info("/ /`  / / \\ | |\\/| | |_) | |   | |_   | |  | |_  ");
            log.info("\\_\\_, \\_\\_/ |_|  | |_|   |_|__ |_|__  |_|  |_|__ ");
            log.info("                                                      ");
            log.info("CSLM_BACK系统启动完毕，系统编译打包时间：{}，地址：{}", this.formatUtcTime(packageTime), url);

           /* boolean auto = febsProperties.isAutoOpenBrowser();
            if (auto && StrUtil.equalsIgnoreCase(active, CslmConstant.DEVELOP)) {
                String os = System.getProperty("os.name");
                // 默认为 windows时才自动打开页面
                if (StrUtil.containsIgnoreCase(os, CslmConstant.SYSTEM_WINDOWS)) {
                    //使用默认浏览器打开系统登录页
                    Runtime.getRuntime().exec("cmd  /c  start " + url);
                }
            }*/
        }
    }

    private String formatUtcTime(String utcTime) {
        try {
            String t = StrUtil.replace(utcTime, "T", StringPool.SPACE);
            String z = StrUtil.replace(t, "Z", StringPool.EMPTY);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FULL_TIME_SPLIT_PATTERN);
            LocalDateTime localDateTime = LocalDateTime.parse(z, formatter);
            LocalDateTime now = localDateTime.plusHours(8);
            return DateUtil.format(now, FULL_TIME_SPLIT_PATTERN);
        } catch (Exception ignore) {
            return StringPool.EMPTY;
        }
    }
}
