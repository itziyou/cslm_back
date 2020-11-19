package com.cpiaoju.cslmback.common.properties;

import lombok.Data;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author ziyou
 */
@Data
@SpringBootConfiguration
@ConfigurationProperties(prefix = "cslm.shiro")
public class ShiroProperties {

    private String anonUrl;

    /**
     * token默认有效时间
     */
    private Long jwtTimeOut;
}
