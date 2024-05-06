package org.example.vo.web;

import lombok.Data;
import org.example.pojo.ForumArticle;
import org.example.pojo.ForumArticleAttachment;
/**
 * 详情页ForumArticleDetailVO
 *
 * */
@Data
public class ForumArticleDetailVO {

    private ForumArticleVO forumArticle;

    private ForumArticleAttachmentVo attachment;
    private Boolean haveLike = false;

}
