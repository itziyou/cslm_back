package com.cpiaoju.cslmback;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * @author ziyou
 */
@EnableAsync
@SpringBootApplication
@EnableTransactionManagement
public class CslmBackApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(CslmBackApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }

}
