package org.example.config;

import io.minio.MinioClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * .
 *
 * @belongsProject: LanWei-java
 * @belongsPackage: org.example.config
 * @author: ZGY
 * @createTime: 2024-04-04  01:46
 * @version: 1.0
 */

@Configuration
public class MiniConfig {
    @Autowired
    private MinioProperties minioProperties;
    @Bean
    public MinioClient minioClient(){
        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint(minioProperties.getEndpoint())
                        .credentials(minioProperties.getAccessKey(),
                                minioProperties.getSecretKey())
                        .build();
        return minioClient;
    }
}