package org.example.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.tomcat.util.descriptor.web.SecurityRoleRef;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@TableName("forum_article_attachment")
public class ForumArticleAttachment implements Serializable {

    String file_id;

    String article_id;

    String user_id;

    long file_size;

    String file_name;

    int download_count;

    String file_path;

    int file_type;

    int integral;

}
