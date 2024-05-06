package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.pojo.ForumArticle;
import org.example.pojo.ForumArticleAttachment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


public interface ForumArticleService extends IService<ForumArticle> {

    ForumArticle readArticle(String articleId);
//    int updateArticleCount();

    int updateArticleCount(Integer updateType, Integer changeCount, String articleId);


    void postArticle(Boolean isAdmin,ForumArticle forumArticle, ForumArticleAttachment forumArticleAttachment,
                     MultipartFile cover,MultipartFile attachment);

    void updateArticle(Boolean isAdimin, ForumArticle article,
                       ForumArticleAttachment articleAttachment,
                       MultipartFile cover,
                       MultipartFile attachment);
}
