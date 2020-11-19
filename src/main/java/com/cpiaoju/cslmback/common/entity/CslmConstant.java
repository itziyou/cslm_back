package com.cpiaoju.cslmback.common.entity;

/**
 * 常量
 *
 * @author ziyou
 */
public interface CslmConstant {

    /**
     * 注册用户角色ID
     */
    Long REGISTER_ROLE_ID = 2L;

    /**
     * 排序规则：降序
     */
    String ORDER_DESC = "desc";

    /**
     * 排序规则：升序
     */
    String ORDER_ASC = "asc";

    /**
     * 前端页面路径前缀
     */
    String VIEW_PREFIX = "cslm/views/";

    /**
     * 验证码 Session Key
     */
    String CODE_PREFIX = "cslm_captcha_";

    /**
     * 允许下载的文件类型，根据需求自己添加（小写）
     */
    String[] VALID_FILE_TYPE = {"xlsx", "zip"};

    /**
     * 异步线程池名称
     */
    String ASYNC_POOL = "cslmAsyncThreadPool";

    /**
     * 异步线程名称前缀
     */
    String ASYNC_THREAD_NAME_PREFIX = "Cslm-Async-Thread";

    /**
     * 任务调度线程前缀
     */
    String QUARTZ_THREAD_NAME_PREFIX = "Cslm-Job-Thread";

    /**
     * 开发环境
     */
    String DEVELOP = "dev";

    /**
     * Windows 操作系统
     */
    String SYSTEM_WINDOWS = "windows";

    /**
     * user缓存前缀
     */
    String USER_CACHE_PREFIX = "cslm.back.cache.user.";

    /**
     * user角色缓存前缀
     */
    String USER_ROLE_CACHE_PREFIX = "cslm.back.cache.user.role.";

    /**
     * user权限缓存前缀
     */
    String USER_PERMISSION_CACHE_PREFIX = "cslm.back.cache.user.permission.";

    /**
     * token缓存前缀
     */
    String TOKEN_CACHE_PREFIX = "cslm.back.cache.token.";

    /**
     * 按钮
     */
    String TYPE_BUTTON = "1";

    /**
     * 菜单
     */
    String TYPE_MENU = "0";
}
