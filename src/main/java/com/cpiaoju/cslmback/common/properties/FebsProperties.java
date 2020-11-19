package com.cpiaoju.cslmback.common.properties;

import lombok.Data;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author ziyou
 */
@Data
@SpringBootConfiguration
@ConfigurationProperties(prefix = "cslm")
public class FebsProperties {

    private boolean autoOpenBrowser = false;

    private int maxBatchInsertNum = 1000;

}
