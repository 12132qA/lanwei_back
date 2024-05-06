package org.example.enums;

public enum UserOperFrequencyTyoeEnum {

    NO_CHECK(0,"不校验"),
    POST_ARTICLE(1,"发表文章"),
    POST_COMMENT(2,"评价"),
    DO_LIKE(3,"点赞"),
    IMAGE_UPLOAD(4,"图片上传");

    private Integer operType;

    private String desc;

    UserOperFrequencyTyoeEnum(Integer operType, String desc) {
        this.operType = operType;
        this.desc = desc;
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
