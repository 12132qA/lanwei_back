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
import java.util.List;

@Component
@Data
@AllArgsConstructor
@ToString
@NoArgsConstructor
@TableName("forum_comment")
public class ForumComment  implements Serializable {

    int commentId;

    int pCommentId;

    String articleId;

    String content;

    String imgPath;

    String userId;

    String replyNickName;

    String nickName;

    String userIpAddress;

    String replyUserId;

    int topType;

    Date postTime;

    int goodCount;

     int status;

//   //  0 未点赞   1 已点赞
//   Integer likeType;

//   List<ForumComment> children;


}
