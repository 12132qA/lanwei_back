package org.example.pojo;

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

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Component
@TableName("forum_article")
public class ForumArticle implements Serializable {
    @TableId
    String articleId;

    Integer boardId;

    String boardName;

    int pBoardId;

    String userId;

    String nickName;

    String userIpAddress;

    String title;

    String cover;

    String content;

    String markdownContent;

    int  editorType;

    String summary;

    Date postTime;

    Date lastUpdateTime;

    int readCount;

    int goodCount;

    int commentCount;

    int topType;

    int attachmentType;

    int status;


}
