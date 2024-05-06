package org.example.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

@Component
@ToString
@NoArgsConstructor
@Data
@AllArgsConstructor
@TableName("user_info")
public class UserInfo  implements Serializable {
    @TableId
    String userId;

    String nickName;

    String email;

    String password;

    Integer sex;

    String personDescription;

    Date joinTime;

   Date lastLoginTime;

   String lastLoginIp;

  String lastLoginIpAddress;

   Integer totalIntegral;

   Integer currentIntegral;

   Integer status; //

}
