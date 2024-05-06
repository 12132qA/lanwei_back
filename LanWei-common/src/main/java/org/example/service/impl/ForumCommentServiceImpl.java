package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.Mappers.ForumArticleMapper;
import org.example.Mappers.ForumCommentMapper;
import org.example.Mappers.UserInfoMapper;
import org.example.Mappers.UserMessageMapper;
import org.example.Utils.CopyTools;
import org.example.Utils.FileUtils;
import org.example.Utils.StringTools;
import org.example.Utils.SysCacheUtils;
import org.example.dto.FileUploadDto;
import org.example.enums.*;
import org.example.exception.BusinessException;
import org.example.pojo.ForumArticle;
import org.example.pojo.ForumComment;
import org.example.pojo.UserInfo;
import org.example.pojo.UserMessage;
import org.example.pojo.contants.Constants;
import org.example.query.ForumCommentQuery;
import org.example.service.ForumArticleService;
import org.example.service.ForumCommentService;
import org.example.service.UserMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Service
public class ForumCommentServiceImpl extends ServiceImpl<ForumCommentMapper, ForumComment> implements ForumCommentService {


    @Autowired
    private ForumCommentMapper forumCommentMapper;

    @Autowired
    private ForumArticleMapper forumArticleMapper;
    @Autowired
    private ForumArticleService forumArticleService;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private UserInfoServiceImpl userInfoService;

    @Autowired
    private UserMessageService userMessageService;

    @Autowired
    private UserMessageMapper userMessageMapper;

    @Autowired
    private FileUtils fileUtils;


//    @Autowired
//    private ForumCommentServiceImpl forumCommentService;
    public List<ForumCommentQuery> findListByParam(QueryWrapper<ForumComment> wq){

        List<ForumComment> list = forumCommentMapper.selectList(wq);
        List<ForumCommentQuery> forumCommentQueries
                = CopyTools.copyList(list, ForumCommentQuery.class);
        // 获取二级评论
        for(ForumCommentQuery v: forumCommentQueries){
            QueryWrapper<ForumComment> ch = new QueryWrapper<>();
            ch = ch.eq("p_comment_id", v.getCommentId());

            List<ForumComment> forumComments = forumCommentMapper.selectList(ch);
            List<ForumCommentQuery> fqueies = CopyTools.copyList(forumComments, ForumCommentQuery.class);

            v.setChildren( fqueies );
        }

        // @TODO
        return forumCommentQueries;
    }

    /**
     * 文章评论
     * */
   public int updateCommentGoodCount(Integer changeCount,Integer commentId){

       QueryWrapper<ForumComment> qw = new QueryWrapper<>();
       UpdateWrapper<ForumComment> dw = new UpdateWrapper<>();
       qw.eq("comment_id", commentId);
       UpdateWrapper<ForumComment> articleId1 = dw.eq("comment_id", commentId);
       ForumComment forumComment = forumCommentMapper.selectById(commentId);

       articleId1.set("good_count",forumComment.getGoodCount()+changeCount);

       int update = forumCommentMapper.update(forumComment, dw);


       return update;
    }
    @Override
    public ForumComment getForumCommentByCommentId(String commentId) {

        return forumCommentMapper.selectById(commentId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeTopType(String userId, String commentId, Integer topType) {
        CommentTopTypeEnum typeEnum = CommentTopTypeEnum.getByType(topType);
        if(null == topType){
          throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        ForumComment forumComment = forumCommentMapper.selectById(commentId);
        if(null == forumComment){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        ForumArticle forumArticle = forumArticleMapper.selectById(forumComment.getArticleId());
        if(null == forumArticle){
           throw new BusinessException(ResponseCodeEnum.CODE_600);
         }
        if(!forumArticle.getUserId().equals(userId)|| forumComment.getPCommentId()!=0){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        if(forumComment.getTopType()== topType){
            return;
        }
        if(CommentTopTypeEnum.TOP.getType().equals(topType)){
         updateTopTypeByArticleId(forumArticle.getUserId());
        }
        ForumComment updateInfo = new ForumComment();
        updateInfo.setTopType(topType);


        forumCommentMapper.update(updateInfo,new QueryWrapper<ForumComment>().eq("comment_id",commentId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTopTypeByArticleId(String Id) {
       UpdateWrapper<ForumComment> updateWrapper = new UpdateWrapper<>();
        UpdateWrapper<ForumComment> set = updateWrapper.eq("article_id", Id).set("top_type", 0);

        forumCommentMapper.update(new ForumComment(),set);

    }

    @Override
    public void postComment(ForumComment comment, MultipartFile image) {
        ForumArticle forumArticle = forumArticleMapper.selectById(comment.getArticleId());
        if(forumArticle==null|| ArticleStatusEnum.AUDTI.getStatus().equals(forumArticle.getStatus())){
            throw new BusinessException("评论文章不存在");

        }

        ForumComment pComment = null;
        if(comment.getPCommentId()!=0){
            pComment = forumCommentMapper.selectById(comment.getPCommentId());
            if(null == pComment){
                throw new BusinessException("回答的评论不存在");
            }
        }

        // 判断回复的用户是否存在
        if(!StringTools.isEmpty(comment.getReplyNickName())){
            UserInfo userInfo = userInfoMapper.selectById(comment.getUserId());
            if(userInfo == null){
                throw new BusinessException("回复的用户不存在");
            }
            comment.setPostTime(new Date());

            // 图片上传
            if(image!=null){
                FileUploadDto fileUploadDto = fileUtils.uploadFile2Local
                        (image, Constants.FILE_FOLDER_FILE, FileUploadTypeEnum.COMMENT_IMAGE);
                comment.setImgPath(fileUploadDto.getLocalPath());

            }
        }
        Boolean needAudit = SysCacheUtils.getSysSetting().getAuditDto().getCommentAudit();

        comment.setStatus(needAudit? CommentStatusEnum.NO_AUDIT.getStatus():CommentStatusEnum.AUDIT.getStatus());
        forumCommentMapper.insert(comment);

        if(needAudit){
            return;
        }
        // 不需要审核
       updateCommentInfo(comment,forumArticle,pComment);
        return;
    }

    public void updateCommentInfo(ForumComment comment,ForumArticle forumArticle,ForumComment pComment){
       Integer commentIntegral = SysCacheUtils.getSysSetting().getCommentDto().getCommentIntegral();
       if(commentIntegral>0){
           userInfoService.updateUserIntegral(comment.getUserId(),
                   UserIntegralOperTypeEnum.POST_COMMENT,
                   UserIntegralChangeTypeEnum.ADD.getChangeType(),
                   commentIntegral);
       }
       if(comment.getPCommentId()==0){
           forumArticleService.updateArticleCount
                   (UpdateArticleCountTypeEnum.COMMENT_COUNT.getType()
                   , Constants.ONE,comment.getArticleId());
       }

       // 记录消息

        UserMessage userMessage = new UserMessage();
        userMessage.setMessageType(MessageTypeEnum.COMMENT_LIKE.getType());
        userMessage.setCreateTime(new Date());
        userMessage.setArticleId(comment.getArticleId());
        userMessage.setSendUserId(comment.getUserId());
        userMessage.setSendNickName(comment.getNickName());
        userMessage.setCommentId(comment.getCommentId());
        userMessage.setArticleTitle(forumArticle.getTitle());

        if(comment.getPCommentId() == 0){
           userMessage.setReceivedUserId(forumArticle.getUserId());
        }else if(comment.getPCommentId()!=0&& StringTools.isEmpty(comment.getReplyUserId())){
          userMessage.setReceivedUserId(pComment.getUserId());
        }else if(comment.getPCommentId()!=0&& !StringTools.isEmpty(comment.getReplyUserId())){
          userMessage.setReceivedUserId(comment.getReplyUserId());
        }
        if(!comment.getUserId().equals(userMessage.getReceivedUserId())){
            userMessageMapper.insert(userMessage);
        }

    }
}
