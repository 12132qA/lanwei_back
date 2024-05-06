package org.example.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * .
 *
 * @belongsProject: LanWei-java
 * @belongsPackage: org.example.config
 * @author: ZGY
 * @createTime: 2024-04-04  01:49
 * @version: 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {
    private String endpoint;
    private String accessKey;
    private String secretKey;
}