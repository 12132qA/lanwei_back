package org.example.pojo;

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
@NoArgsConstructor
@ToString
@Data
@AllArgsConstructor
@TableName("like_record")
public class LikeRecord  implements Serializable {

    int op_id;

    int op_type;

    String object_id;

    String user_id;

    Date create_time;

    String author_user_id;
}
