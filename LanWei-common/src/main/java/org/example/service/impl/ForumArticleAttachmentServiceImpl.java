package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.io.MergedStream;
import org.example.Mappers.ForumArticleAttachmentDownloadMapper;
import org.example.Mappers.ForumArticleAttachmentMapper;
import org.example.Mappers.UserMessageMapper;
import org.example.dto.SessionWebUserDto;
import org.example.enums.MessageStatusEnum;
import org.example.enums.MessageTypeEnum;
import org.example.enums.UserIntegralChangeTypeEnum;
import org.example.enums.UserIntegralOperTypeEnum;
import org.example.exception.BusinessException;
import org.example.pojo.*;
import org.example.service.ForumArticleAttachmentDownloadService;
import org.example.service.ForumArticleAttachmentService;
import org.example.service.ForumArticleService;
import org.example.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class ForumArticleAttachmentServiceImpl extends ServiceImpl<ForumArticleAttachmentMapper, ForumArticleAttachment> implements ForumArticleAttachmentService {

    @Autowired
    private ForumArticleAttachmentMapper forumArticleAttachmentMapper;
    @Autowired
    private ForumArticleAttachmentDownloadServiceImpl forumArticleAttachmentDownloadService;
    @Autowired
    private ForumArticleAttachmentDownloadMapper forumArticleAttachmentDownloadMapper;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private ForumArticleServiceImpl forumArticleService;

    @Autowired
    private UserMessageMapper userMessageMapper;

    /**
     * 文件下载接口
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ForumArticleAttachment downloadAttachment(String fileId, SessionWebUserDto sessionWebUserDto) {
        LambdaQueryWrapper<ForumArticleAttachment> qw =
                new LambdaQueryWrapper<>();
        LambdaQueryWrapper<ForumArticleAttachment> eq =
                qw.eq(ForumArticleAttachment::getFile_id, fileId);
        ForumArticleAttachment fat =
                forumArticleAttachmentMapper.selectOne(eq);
        // 判断 文件 是否存在
        if(null == fat){
            throw new BusinessException("附件不存在");
        }
        ForumArticleAttachmentDownload download = null;
        if(fat.getIntegral()>0 && !sessionWebUserDto.getUserId().equals(fat.getUser_id()) ){
           download =
                   forumArticleAttachmentDownloadService.selectByFileIdAndUserId(fileId,sessionWebUserDto.getUserId());
           if(download == null){
               UserInfo userInfo =
                       userInfoService.getById(sessionWebUserDto.getUserId());
               if(userInfo.getCurrentIntegral() - fat.getIntegral() < 0){
                   throw new BusinessException("积分不够");
               }
           }

        }
        ForumArticleAttachmentDownload updateDownLoad = new ForumArticleAttachmentDownload();

        updateDownLoad.setArticleId(fat.getArticle_id());
        updateDownLoad.setFileId(fileId);
        updateDownLoad.setUserId(fat.getUser_id());

        updateDownLoad.setDownloadCount(1);

        forumArticleAttachmentDownloadService.insertOrUpdate(updateDownLoad);

        forumArticleAttachmentDownloadService.updateDownloadCount(fileId);

        if(sessionWebUserDto.getUserId().equals(fat.getUser_id()) ||download!=null){
            return fat;
        }

        // 扣除下载人积分
        userInfoService.updateUserIntegral(sessionWebUserDto.getUserId(), UserIntegralOperTypeEnum.USER_DOWNLOAD_ATTACHMENT,
                UserIntegralChangeTypeEnum.REDUCE.getChangeType(), fat.getIntegral());

       // 给附件提供者增加积分
        userInfoService.updateUserIntegral(fat.getUser_id(),UserIntegralOperTypeEnum.USER_DOWNLOAD_ATTACHMENT,
                UserIntegralChangeTypeEnum.REDUCE.getChangeType(), fat.getIntegral());


        // 记录消息
        ForumArticle forumArticle = forumArticleService.getById(fat.getArticle_id());

        UserMessage userMessage = new UserMessage();
        userMessage.setMessageType(MessageTypeEnum.DOWNLOAD_ATTACHMENT.getType());
        userMessage.setCreateTime(new Date());
        userMessage.setArticleId(forumArticle.getArticleId());
        userMessage.setCommentId(0);
        userMessage.setSendUserId(sessionWebUserDto.getUserId());
        userMessage.setSendNickName(sessionWebUserDto.getNickName());
        userMessage.setStatus(MessageStatusEnum.NO_READ.getStatus());

        userMessageMapper.insert(userMessage);

        return fat;
    }
}