package org.example.vo.web;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;
@Data
public class UserInfoVO {


    String user_id;

    String email;

    String nick_name;

    String password;

    int sex;

    String person_description;

    Date join_time;

    Date last_login_time;

    int current_integral;

    /**
     * 当前积分
     * */
    private Integer currentIntegral;


    private Integer postCount;

    private Integer likeCount;


}
