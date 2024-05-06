package org.example.service;

import org.example.dto.SessionWebUserDto;
import org.example.pojo.ForumArticleAttachment;
import org.springframework.stereotype.Service;

/**
 * 附件
 *
 */

public interface ForumArticleAttachmentService {

    ForumArticleAttachment downloadAttachment(String fileId, SessionWebUserDto sessionWebUserDto);


}
