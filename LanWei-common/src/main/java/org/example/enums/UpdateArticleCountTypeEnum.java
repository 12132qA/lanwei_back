package org.example.enums;

import lombok.Data;

public enum UpdateArticleCountTypeEnum {

    READ_COUNT(0,"阅读数"),
    GOOD_COUNT(1,"点赞数"),
    COMMENT_COUNT(2,"评论数");

    private Integer type;
    private String desc;

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

    UpdateArticleCountTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static  UpdateArticleCountTypeEnum getByType(Integer type){
         for(UpdateArticleCountTypeEnum item: UpdateArticleCountTypeEnum.values()){
             if(item.getType().equals(type)){
                 return item;
             }
         }

         return null;
    }
}
