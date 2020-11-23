package com.cpiaoju.cslmback.common.properties;

import lombok.Data;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author ziyou
 */
@Data
@SpringBootConfiguration
@ConfigurationProperties(prefix = "cslm.shiro")
public class ShiroProperties {

    private List<String> anonUrl;

    /**
     * token默认有效时间
     */
    private Long jwtTimeOut;
}
