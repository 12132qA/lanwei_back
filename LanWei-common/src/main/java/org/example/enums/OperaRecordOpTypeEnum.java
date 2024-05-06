package org.example.enums;

public enum OperaRecordOpTypeEnum {
    ARTICLE_LIKE(0,"文章点赞"),
    COMMENT_LIKE(1,"评论点赞");

    private Integer type;

    private  String desc;

    OperaRecordOpTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
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
