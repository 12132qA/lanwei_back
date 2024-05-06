package org.example.dto;

import org.example.annotation.VerifyParam;

/**
 * 发送邮件设置
 * */
public class SysSetting4EmailDto {

    // 邮件标题
    @VerifyParam(required = true)
   private String emailTitle;
   private String emailContent;

    public String getEmailTitle() {
        return emailTitle;
    }

    public void setEmailTitle(String emailTitle) {
        this.emailTitle = emailTitle;
    }

    public String getEmailContent() {
        return emailContent;
    }

    public void setEmailContent(String emailContent) {
        this.emailContent = emailContent;
    }
}
