package org.example.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
@TableName("sys_setting")
public class SysSetting  implements Serializable {

    String code;

    String json_content;


}
