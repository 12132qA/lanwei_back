package org.example.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.pojo.SysSetting;

import javax.swing.text.StringContent;


public enum SysSettingCodeEnum {

    AUDIT("audit","com.lanwei.dto.SysSetting4AuditDto","auditSetting","审核设置"),
    COMMENT("comment","com.lanwei.dto.SysSetting4CommentDto","CommentSetting","评论设置"),
    POST("post","com.lanwei.dto.SysSetting4PostDto","postSetting","帖子设置"),
    LIKE("like","com.lanwei.dto.SysSetting4likeDto","likeSetting","点赞设置"),
    REGISTER("register","com.lanwei.dto.SysSetting4RegisterDto","registerSetting","注册设置"),
    EMAIL("email","com.lanwei.dto.SysSetting4EmailDto","emailSetting","邮件设置");

    SysSettingCodeEnum(String code, String classz, String propName, String desc) {
        this.code = code;
        this.classz = classz;
        this.propName = propName;
        this.desc = desc;
    }

    private String code;

    public static SysSettingCodeEnum getByCode(String code){
        for(SysSettingCodeEnum item: SysSettingCodeEnum.values()){
            if(item.getCode().equals(code)){
                return item;

            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public String getClassz() {
        return classz;
    }

    public void setClassz(String classz) {
        this.classz = classz;
    }

    public String getPropName() {
        return propName;
    }

    public void setPropName(String propName) {
        this.propName = propName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    private String  classz;

    private String propName;

    private String desc;



}
