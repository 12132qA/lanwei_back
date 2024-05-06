package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.pojo.ForumArticleAttachment;
import org.example.pojo.ForumArticleAttachmentDownload;

public interface ForumArticleAttachmentDownloadService  extends IService<ForumArticleAttachmentDownload> {
    ForumArticleAttachmentDownload getForumArticleAttachmentDownLoadByFileIdAndUserId(String fileId, String userId);
}
