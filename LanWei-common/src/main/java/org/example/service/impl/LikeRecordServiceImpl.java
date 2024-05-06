package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.Mappers.ForumArticleMapper;
import org.example.Mappers.LikeRecordMapper;
import org.example.Mappers.UserMessageMapper;
import org.example.enums.MessageStatusEnum;
import org.example.enums.MessageTypeEnum;
import org.example.enums.OperaRecordOpTypeEnum;
import org.example.enums.UpdateArticleCountTypeEnum;
import org.example.exception.BusinessException;
import org.example.pojo.ForumArticle;
import org.example.pojo.ForumComment;
import org.example.pojo.LikeRecord;
import org.example.pojo.UserMessage;
import org.example.pojo.contants.Constants;

import org.example.service.LikeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 *
 * LikeRecordService
 * */

@Slf4j
@Service
public class LikeRecordServiceImpl extends ServiceImpl<LikeRecordMapper, LikeRecord> implements LikeRecordService {
    @Autowired
    private LikeRecordMapper likeRecordMapper;

    @Autowired
    private UserMessageMapper userMessageMapper;

    @Autowired
    private ForumArticleMapper forumArticleMapper;

    @Autowired
    private ForumArticleServiceImpl forumArticleService;

    @Autowired
    private UserMessageServiceImpl userMessageService;

    @Autowired
    private ForumCommentServiceImpl forumCommentService;



    /**
     * 以下两个方法是相同方法
     * */
    public LikeRecord getLikeRecordByObjectIdAndUserIdAndOpType(String articleId, String userId,Integer type){
        log.info("获取操作");
        LambdaQueryWrapper<LikeRecord>qw = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<LikeRecord> and
                = qw.eq(LikeRecord::getUser_id, userId)
                .and(w -> w.eq(LikeRecord::getObject_id, articleId))
                .and(ww -> ww.eq(LikeRecord::getOp_type, type));
      return   likeRecordMapper.selectOne(and);
    }



    public LikeRecord selectByObjectIdAndUserIdAndOpType(String articleId, String userId,Integer type){
         log.info("获取操作");
        LambdaQueryWrapper<LikeRecord>qw =
                new LambdaQueryWrapper<>();
        LambdaQueryWrapper<LikeRecord> and
                = qw.eq(LikeRecord::getUser_id, userId)
                .and(w -> w.eq(LikeRecord::getObject_id, articleId))
                .and(ww -> ww.eq(LikeRecord::getOp_type, type));
        return   likeRecordMapper.selectOne(and);
    }

    public void deleteByObjectIdAndUserIdAndOpType(String articleId, String userId, Integer type){
        log.info("删除操作");
        LambdaQueryWrapper<LikeRecord> qw = new LambdaQueryWrapper<>();

        LambdaQueryWrapper<LikeRecord> and = qw.eq(LikeRecord::getObject_id, articleId)
                .and(q -> q.eq(LikeRecord::getUser_id, userId))
                .and(w -> w.eq(LikeRecord::getOp_type, type));

        likeRecordMapper.delete(and);
    }

    /**
     * 文章点赞功能
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void doLike(String objectId, String userId, String nickName, OperaRecordOpTypeEnum opTypeEnum) {
        UserMessage userMessage = new UserMessage();
        userMessage.setCreateTime(new Date());
        LikeRecord likeRecord = null;

        switch (opTypeEnum){
            case ARTICLE_LIKE:
              ForumArticle  forumArticle = forumArticleMapper.selectById(objectId);
              if(forumArticle==null){
                  throw new BusinessException("文章不存在");
              }

              articleLike(objectId,forumArticle,userId,opTypeEnum);

              userMessage.setArticleId(objectId);
              userMessage.setArticleTitle(forumArticle.getTitle());
              userMessage.setMessageType(MessageTypeEnum.ARTICLE_LIKE.getType());
              userMessage.setCommentId(Constants.ZERO); // 评论 ID
              userMessage.setReceivedUserId(forumArticle.getUserId());
                break;
            case COMMENT_LIKE: //
                ForumComment forumComment = forumCommentService.getById(Integer.parseInt(objectId));
                if(null == forumComment){
                    throw new BusinessException("评论不存在");
                }
                commentLike(objectId,userId,opTypeEnum);

                forumArticle = forumArticleMapper.selectById(forumComment.getArticleId());

                userMessage.setArticleId(objectId);
                userMessage.setArticleTitle(forumArticle.getTitle());
                userMessage.setMessageType(MessageTypeEnum.ARTICLE_LIKE.getType());
                userMessage.setCommentId(forumComment.getCommentId());
                userMessage.setReceivedUserId(forumArticle.getUserId());
                userMessage.setMessageContent(forumComment.getContent() );

                break;
        }


        if(! userId.equals(userMessage.getReceivedUserId())){
            UserMessage dbInfo =
                    userMessageService.selectByArticleIdAndCommentIdAndSendUserIdAndMessageType(
                            userMessage.getArticleId(), String.valueOf(userMessage.getCommentId()),
                    userMessage.getSendUserId(),userMessage.getMessageType());
            if(dbInfo == null){
                userMessageMapper.insert(userMessage);
            }
        }

    }

    /**
     * 文章点赞 或 取消
     * */

    public LikeRecord articleLike(String objId,ForumArticle forumArticle,String userId,OperaRecordOpTypeEnum opTypeEnum){
        LikeRecord record  = selectByObjectIdAndUserIdAndOpType(objId,userId, opTypeEnum.getType());
        int changeCount = 0;
        if(record!=null){
            changeCount = -1;
            deleteByObjectIdAndUserIdAndOpType(objId,userId,opTypeEnum.getType());
//            forumArticleService.updateArticleCount(UpdateArticleCountTypeEnum.GOOD_COUNT.getType(), -1,objId);
        }
        else{
//            ForumArticle forumArticle = forumArticleMapper.selectById(objId);
           changeCount = 1;
            LikeRecord likeRecord = new LikeRecord();
            likeRecord.setObject_id(userId);
            likeRecord.setUser_id(userId);
            likeRecord.setOp_type(opTypeEnum.getType());
            likeRecord.setCreate_time(new Date());
            likeRecord.setAuthor_user_id(forumArticle.getUserId());
             likeRecordMapper.insert(likeRecord);
            /**
             * 更新文章点赞数量
             * */
            forumArticleService.updateArticleCount(UpdateArticleCountTypeEnum.GOOD_COUNT.getType(),changeCount,objId);
        }
        return record;
    }
    /**
     * 文章评论
     * */
     public void commentLike(String objId,String userId,OperaRecordOpTypeEnum opTypeEnum){
        LikeRecord record = selectByObjectIdAndUserIdAndOpType(objId,userId,opTypeEnum.getType());
        Integer changeCount = 0;
         if(record!=null){
             deleteByObjectIdAndUserIdAndOpType(objId,userId,opTypeEnum.getType());

             changeCount =-1;
         }
         else{
            ForumComment forumComment = forumCommentService.getById(Integer.parseInt(objId));
            if(null == forumComment){
                throw new BusinessException("评论不存在");
            }
             LikeRecord likeRecord = new LikeRecord();
             likeRecord.setObject_id(userId);
             likeRecord.setUser_id(userId);
             likeRecord.setOp_type(opTypeEnum.getType());
             likeRecord.setCreate_time(new Date());
             likeRecord.setAuthor_user_id(forumComment.getUserId());
             likeRecordMapper.insert(likeRecord);
             changeCount =1;
             /**
              * 更新文章点赞数量
              * */
         }
         forumCommentService.updateCommentGoodCount(changeCount,Integer.parseInt(objId));

     }




}
