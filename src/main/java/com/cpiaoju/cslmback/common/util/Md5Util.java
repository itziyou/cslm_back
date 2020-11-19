package com.cpiaoju.cslmback.common.util;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

/**
 * @author ziyou
 */
public class Md5Util {

    private static final String ALGORITHM_NAME = "md5";

    private static final int HASH_ITERATIONS = 5;

    public static String encrypt(String username, String password) {
        return new SimpleHash(ALGORITHM_NAME, password, ByteSource.Util.bytes(username), HASH_ITERATIONS).toHex();
    }
}
