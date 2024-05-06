package org.example.enums;

public enum ArticleStatusEnum {  // RtiXj832TFL4nhW

    DEL(-1,"已删除"),

    NO_AUDIT(0,"待审核"),

    AUDTI(1,"已审核");

    private Integer status;

    private String desc;

    ArticleStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public ArticleStatusEnum getByStatus(Integer status) {
     for(ArticleStatusEnum item: ArticleStatusEnum.values()){
         System.out.println(item);
         if(item.getStatus().equals(status)){
             return item;
         }
     }
     return null;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
