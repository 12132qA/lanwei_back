package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.dto.UserMessageCountDto;
import org.example.pojo.UserMessage;

import java.util.List;
import java.util.Map;


public interface UserMessageService extends IService<UserMessage> {

   List<Map<String, Object>> selectUserMessageCount(String userId);

    UserMessageCountDto getUserMessageCount(String userId);

    UserMessage selectByArticleIdAndCommentIdAndSendUserIdAndMessageType(String articleId, String commentId, String sendUserId, Integer type);

    void readMessageByType(String receivedUserId,Integer messageType);
}
