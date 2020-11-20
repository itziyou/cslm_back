package com.cpiaoju.cslmback;

import com.cpiaoju.cslmback.common.authentication.jwt.JWTUtil;
import com.cpiaoju.cslmback.common.util.Md5Util;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CslmBackApplicationTests {

    @Test
    void contextLoads() {
        //System.out.println(Md5Util.encrypt("mrbird", "qwer1234"));
        System.out.println(JWTUtil.sign("username", "password"));
        String token = "ayJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2MDU4NDI4MTksInVzZXJuYW1lIjoidXNlcm5hbWUifQ.ygkvGLUtXKDQvUCSW4818ewJzRpiRAmvTcUrgic04a4";
        //String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2MDU4NDI4NjgsInVzZXJuYW1lIjoidXNlcm5hbWUifQ.2shYiDKZXiE7d9WzJnxuN8RDEKZmVwcM-TCUaIO0CRU";
        System.out.println(JWTUtil.verify(token,"username","password"));
    }

}
