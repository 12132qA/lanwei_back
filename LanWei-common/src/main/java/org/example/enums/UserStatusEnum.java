package org.example.enums;

public enum UserStatusEnum {
      DISABLE(0,"禁用"),
      ENABLE(1,"启用");

//      ENABLE(1,"启用");

      private Integer status;

      private String desc;

      UserStatusEnum(Integer staus, String desc) {
            this.status = staus;
            this.desc = desc;
      }

      public Integer getStaus() {
            return status;
      }

      public void setStaus(Integer staus) {
            this.status = staus;
      }

      public String getDesc() {
            return desc;
      }

      public void setDesc(String desc) {
            this.desc = desc;
      }
}
