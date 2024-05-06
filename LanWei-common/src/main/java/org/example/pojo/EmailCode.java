package org.example.pojo;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

//import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.sql.Time;
/**
 * @author 张根勇
 */
@Component
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@TableName("email_code")
public class EmailCode  implements Serializable {

    String email;

    String code;

    Time create_time;

    int status;

}