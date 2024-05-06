package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.Mappers.UserMessageMapper;
import org.example.dto.UserMessageCountDto;
import org.example.enums.MessageStatusEnum;
import org.example.enums.MessageTypeEnum;
import org.example.pojo.UserMessage;
import org.example.service.UserMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.LongConsumer;

@Slf4j
@Service
public class UserMessageServiceImpl extends ServiceImpl<UserMessageMapper, UserMessage> implements UserMessageService {


    @Autowired
    private UserMessageMapper userMessageMapper;
//    @Autowired
//    private UserMessageService userMessageService;

    @Override
    public List<Map<String, Object>> selectUserMessageCount(String userId) {

        Map<String,String> ans = new HashMap<>();

        QueryWrapper<UserMessage> uq = new QueryWrapper<UserMessage>().eq("received_user_id", userId)
                .eq("status", 1).groupBy("message_type");
        UserMessageService userMessageService = new UserMessageServiceImpl();
        List<Map<String, Object>> maps = userMessageService.listMaps(uq);

        return maps;
    }

    @Override
    public UserMessageCountDto getUserMessageCount(String userId){
        List<Map<String, Object>> maps = selectUserMessageCount(userId);
        UserMessageCountDto messageCountDto = new UserMessageCountDto();
        Long total = 0L;

        for(Map item: maps){
            Integer type = (Integer) item.get("messageType");
            Long count = (Long) item.get("count");
            MessageTypeEnum messageTypeEnum = MessageTypeEnum.getByType(type);

            switch (messageTypeEnum){
                case SYS:
                    messageCountDto.setSys(count);
                    break;
                case COMMENT:
                    messageCountDto.setReply(count);
                    break;
                case ARTICLE_LIKE:
                    messageCountDto.setLikePost(count);
                    break;
                case COMMENT_LIKE:
                    messageCountDto.setLikeComment(count);
                case DOWNLOAD_ATTACHMENT:
                    messageCountDto.setDownloadAttachment(count);
                    break;
            }
            messageCountDto.setTotal(total);

        }

        return  messageCountDto;

    }

    @Override
    public UserMessage selectByArticleIdAndCommentIdAndSendUserIdAndMessageType(String articleId, String commentId,String sendUserId, Integer type){

        log.info("获取操作");
        LambdaQueryWrapper<UserMessage> qw = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<UserMessage> and
                = qw.eq(UserMessage::getArticleId, articleId)
                .and(w -> w.eq(UserMessage::getCommentId, commentId))
                .and(ww -> ww.eq(UserMessage::getMessageType, type))
                .and(qq-> qq.eq(UserMessage::getSendUserId,sendUserId));
        return   userMessageMapper.selectOne(and);
    }

    @Override
    public void readMessageByType(String receivedUserId, Integer messageType) {
        updateMessageStatusBatch(receivedUserId,messageType, MessageStatusEnum.READ.getStatus());
    }

    void updateMessageStatusBatch(String receivedUserId,Integer messageType,
                                  Integer status){

        UpdateWrapper<UserMessage> uq = new UpdateWrapper<>();

        uq.eq("received_user_id",receivedUserId)
                .eq("message_type",messageType).set("status",status);

        userMessageMapper.update(new UserMessage(),uq);
    }

}
