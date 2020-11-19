package com.cpiaoju.cslmback.common.configure;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;

import java.time.LocalDateTime;

/**
 * 自定义 p6spy sql输出格式
 *
 * @author ziyou
 */
public class P6spySqlFormatConfigure implements MessageFormattingStrategy {

    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {

        return StrUtil.isNotBlank(sql) ? DateUtil.format(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss")
                + " | 耗时 " + elapsed + " ms | SQL 语句：" + StrUtil.LF + sql.replaceAll("[\\s]+", StrUtil.SPACE) + ";" : StrUtil.EMPTY;
    }
}
