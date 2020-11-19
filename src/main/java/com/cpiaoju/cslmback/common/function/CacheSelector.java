package com.cpiaoju.cslmback.common.function;

/**
 * @author ziyou
 */
@FunctionalInterface
public interface CacheSelector<T> {
    T select() throws Exception;
}
