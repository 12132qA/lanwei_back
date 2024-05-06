package org.example.enums;

public enum UserIntegralOperTypeEnum {
    REGISTER(1,"账号注册"),

    USER_DOWNLOAD_ATTACHMENT(2,"下载附件"),

    DOWNLOAD_ATTACHMENT(3,"附件被下载"),

    POST_COMMENT(4,"发布评论"),

    POST_ARTICLE(5,"发布文章"),

    ADMIN(6,"管理员操作"),

    DEL_ARTICLE(7,"文章被删除"),
    DEL_COMMENT(8,"评论被删除");

    private Integer operType;

    private String desc;

    UserIntegralOperTypeEnum(Integer operType, String desc) {
        this.operType = operType;
        this.desc = desc;
    }

    public static UserIntegralOperTypeEnum getByType(Integer type){
        for(UserIntegralOperTypeEnum item: UserIntegralOperTypeEnum.values()){
            if(item.getOperType().equals(type)){
                return item;
            }
        }
        return null;
    }

    public Integer getOperType() {
        return operType;
    }

    public void setOperType(Integer operType) {
        this.operType = operType;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
