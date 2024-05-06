package org.example.enums;

public enum UserIntegralChangeTypeEnum {
   ADD(1,"增加"),

    REDUCE(-1,"减少");
    private Integer changeType;

    private String desc;

    UserIntegralChangeTypeEnum(Integer changeType, String desc) {
        this.changeType = changeType;
        this.desc = desc;
    }

    public Integer getChangeType() {
        return changeType;
    }

    public void setChangeType(Integer changeType) {
        this.changeType = changeType;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
