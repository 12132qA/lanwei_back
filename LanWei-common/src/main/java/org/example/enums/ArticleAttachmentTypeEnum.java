package org.example.enums;

public enum ArticleAttachmentTypeEnum {

    NO_ATTACHMENT(0,"没有附件"),
    HAVE_ATTACHMENT(1,"由附件");
    private Integer type;
    private String desc;

    ArticleAttachmentTypeEnum(Integer type, String desc) {
        this.type = type;

        this.desc = desc;
    }
   // 获取 文件 附件 类型
    public static ArticleAttachmentTypeEnum getByType(Integer type){
        for(ArticleAttachmentTypeEnum item:
                ArticleAttachmentTypeEnum.values()){
            if(item.getType().equals(type)){
                return item;
            }
        }
        return null;
    }
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }


    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}