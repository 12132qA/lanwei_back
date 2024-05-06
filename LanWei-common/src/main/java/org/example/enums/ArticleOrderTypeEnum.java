package org.example.enums;

import lombok.Data;

public enum ArticleOrderTypeEnum {

    /**
     * top_type 表示是否置顶
     * */
// desc,comment_count desc,good_count desc,read_count desc
    HOT(0,"top_type","热榜"),
    SEND(1,"top_type post_time","发布"),
    NEW(2,"top_type last_update_time","最新");

    private Integer type;

    private String orderSql;

    private String desc;

    ArticleOrderTypeEnum(Integer type, String orderSql, String desc) {
        this.type = type;
        this.orderSql = orderSql;
        this.desc = desc;
    }

    public static ArticleOrderTypeEnum getByType(Integer type){
        for(ArticleOrderTypeEnum item: ArticleOrderTypeEnum.values()){
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

    public String getOrderSql() {
        return orderSql;
    }

    public void setOrderSql(String orderSql) {
        this.orderSql = orderSql;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }



}
