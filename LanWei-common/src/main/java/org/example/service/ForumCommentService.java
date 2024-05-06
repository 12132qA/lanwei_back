package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.pojo.ForumComment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


public interface ForumCommentService extends IService<ForumComment> {

    ForumComment getForumCommentByCommentId(String commentId);


    void changeTopType(String userId, String commentId, Integer topType);

    void updateTopTypeByArticleId(String userId);

    void postComment(ForumComment forumComment, MultipartFile image);
}
