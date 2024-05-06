package org.example.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @author 张根勇
 */
@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@TableName("forum_article_attachment_download")
public class ForumArticleAttachmentDownload  implements Serializable {

    String fileId;

    String userId;

    String articleId;

    Integer downloadCount;

}