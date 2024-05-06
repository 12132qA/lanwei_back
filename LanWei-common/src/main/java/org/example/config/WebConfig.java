package org.example.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
@Data
@Configuration
public class WebConfig extends AppConfig{

    @Value("${spring.mail.username:}")
    private String sendUsername;

    @Value("${admin.emails:}")
    private String adminEmails;

    /*
    * 内部app 公钥
    * */
    @Value("${inner.api.appKey:}")
    private String innerApiAppKey;

    // 密钥
    @Value("${inner.api.appSecret:}")
    private String innerApiAppSecret;

    public String getSendUsername() {
        return sendUsername;
    }

    public void setSendUsername(String sendUsername) {
        this.sendUsername = sendUsername;
    }

    public String getAdminEmails() {
        return adminEmails;
    }

    public void setAdminEmails(String adminEmails) {
        this.adminEmails = adminEmails;
    }
}
