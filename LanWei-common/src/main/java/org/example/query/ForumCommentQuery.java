package org.example.query;

import lombok.Data;
import org.example.pojo.ForumBoard;

import java.sql.Time;
import java.util.Date;
import java.util.List;

@Data
public class ForumCommentQuery {


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


   Boolean loadChildren;

       //  0 未点赞   1 已点赞
   Integer likeType;

   List<ForumCommentQuery> children;


}
