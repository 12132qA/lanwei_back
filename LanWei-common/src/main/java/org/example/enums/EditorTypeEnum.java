package org.example.enums;

public enum EditorTypeEnum {
    RICH(0,"富文本"),
    MARKDOWN(1,"MarkDown");

    private Integer type;

    private String desc;

    EditorTypeEnum(Integer type, String desc) {
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

    public static EditorTypeEnum getByType(Integer type){
        for(EditorTypeEnum item: EditorTypeEnum.values()){
            if(item.getType().equals(type)){
                return item;
            }
        }
        return null;
    }


}
