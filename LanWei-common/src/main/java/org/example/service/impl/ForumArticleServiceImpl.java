package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.Mappers.ForumArticleAttachmentMapper;
import org.example.Mappers.ForumArticleMapper;
import org.example.Mappers.ForumBoardMapper;
import org.example.Utils.FileUtils;
import org.example.Utils.ImageUtils;
import org.example.Utils.StringTools;
import org.example.Utils.SysCacheUtils;
import org.example.config.AppConfig;
import org.example.dto.FileUploadDto;
import org.example.dto.SysSetting4AuditDto;
import org.example.enums.*;
import org.example.exception.BusinessException;
import org.example.pojo.ForumArticle;
import org.example.pojo.ForumArticleAttachment;
import org.example.pojo.ForumBoard;
import org.example.pojo.contants.Constants;
import org.example.service.ForumArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;
import java.util.List;

@Service
public class ForumArticleServiceImpl extends ServiceImpl<ForumArticleMapper, ForumArticle> implements ForumArticleService {


     @Autowired
    private ForumArticleMapper forumArticleMapper;

    @Autowired
    private ForumBoardMapper forumBoardMapper;

    @Autowired
    private ImageUtils imageUtils;

    @Autowired
    private AppConfig appConfig;

//     @Autowired
//     private ForumBoardServiceImpl forumBoardService;

     @Autowired
     private UserInfoServiceImpl userInfoService;

     @Autowired
     private FileUtils fileUtils;

    @Autowired
    private ForumArticleAttachmentMapper forumArticleAttachmentMapper;
     /**
      *
      * */
//     public int findCountByParam(QueryWrapper<ForumArticle> wq){
//         int ans = 0;
//
//         ans = forumArticleMapper.selectList(wq);
//
//         return  ans;
//     }

    @Override
     public ForumArticle readArticle(String articleId){
          ForumArticle forumArticle = forumArticleMapper.selectById(articleId);
          if(forumArticle == null){
              throw new BusinessException(ResponseCodeEnum.CODE_404);
          }
          if(ArticleStatusEnum.AUDTI.getStatus().equals(forumArticle.getStatus())){
              updateArticleCount(UpdateArticleCountTypeEnum.READ_COUNT.getType(), Constants.ONE,articleId);
          }

          return forumArticle;
     }

    @Override
    public int updateArticleCount(Integer updateType, Integer changeCount, String articleId) {

        QueryWrapper<ForumArticle> qw = new QueryWrapper<>();
        UpdateWrapper<ForumArticle> dw = new UpdateWrapper<>();
        qw.eq("article_id", articleId);
        UpdateWrapper<ForumArticle> articleId1 = dw.eq("article_id", articleId);
        ForumArticle forumArticle = forumArticleMapper.selectById(articleId);

        if(updateType == 0){
            articleId1.set("read_count",forumArticle.getReadCount()+changeCount);
        }else if(updateType ==1){
            articleId1.set("read_count",forumArticle.getReadCount()+changeCount);
        }else{
            articleId1.set("read_count",forumArticle.getReadCount()+changeCount);
        }
        forumArticleMapper.update(forumArticle,articleId1);


        return 0;
    }

    @Override
    public void postArticle(Boolean isAdmin, ForumArticle forumArticle,
                            ForumArticleAttachment forumArticleAttachment,
                            MultipartFile cover, MultipartFile attachment) {

        resetBoardInfo(isAdmin,forumArticle);

        Date curDate = new Date();
        String articleId = StringTools.getRandomString(Constants.LENGTH_15);
        forumArticle.setArticleId(articleId);
        forumArticle.setPostTime(curDate);
        forumArticle.setLastUpdateTime(curDate);

        if(cover!=null){
            FileUploadDto fileUploadDto = fileUtils.
                    uploadFile2Local(cover, Constants.FILE_FOLDER_IMAGE,
                            FileUploadTypeEnum.ARTICLE_COVER);

            forumArticle.setCover(fileUploadDto.getLocalPath());
        }
// 附件又没有 有则处理一下
        if(attachment!=null){
//            FileUploadDto fileUploadDto = fileUtils.uploadFile2Local(cover, Constants.FILE_FOLDER_FILE,
//                    FileUploadTypeEnum.ARTICLE_COVER);
//            forumArticle.setCover(fileUploadDto.getLocalPath());
            uploadAttachment(forumArticle,forumArticleAttachment,attachment,false);
        forumArticle.setAttachmentType(ArticleAttachmentTypeEnum.HAVE_ATTACHMENT.getType());
        }else {
            forumArticle.setAttachmentType(ArticleAttachmentTypeEnum.NO_ATTACHMENT.getType());
        }

        // 文章审核信息
        if(isAdmin){
            forumArticle.setStatus(ArticleStatusEnum.AUDTI.getStatus());
        }else{

            SysSetting4AuditDto auditDto = SysCacheUtils.getSysSetting().getAuditDto();
            forumArticle.setStatus(auditDto.getPostAudit()?ArticleStatusEnum.NO_AUDIT.getStatus() : ArticleStatusEnum.AUDTI.getStatus());
        }

        // 替换图片  防止用户贴图
        String content = forumArticle.getContent();
        if(!StringTools.isEmpty(content)){
            try {
                String month = imageUtils.resetImageHtml(content);
                String replaceMonth = "/"+month+"/";
                content = content
                        .replace(Constants.FILE_FOLDER_TEMP,replaceMonth);
                forumArticle.setContent(content);
                String markdownContent = forumArticle.getMarkdownContent();
                if(!StringTools.isEmpty(markdownContent)){
                    content.replace(Constants.FILE_FOLDER_TEMP,replaceMonth);
                    forumArticle.setContent(markdownContent);
                }

            }catch (Exception e){
                System.out.println("错误");
            }

        }
        forumArticleMapper.insert(forumArticle);

        // 增加积分
        Integer postIntegral = SysCacheUtils.getSysSetting().getPostDto().getPostIntegral();
        if(postIntegral>0&& ArticleStatusEnum.AUDTI.equals(forumArticle.getStatus())){
            userInfoService.updateUserIntegral(forumArticle.getUserId(),UserIntegralOperTypeEnum.POST_ARTICLE,
                    UserIntegralChangeTypeEnum.ADD.getChangeType(),
                    postIntegral);
        }
    }




    private void resetBoardInfo(Boolean isAdmin,ForumArticle forumArticle){

        ForumBoard board = forumBoardMapper.selectById(forumArticle.getBoardId());
        if(board == null || board.getPostType()== Constants.ZERO&& !isAdmin){
            throw new BusinessException("一级板块不存在");
        }

        forumArticle.setBoardName(board.getBoardName());

        if(forumArticle.getBoardId()!=null&& forumArticle.getBoardId()!=0){
           board = forumBoardMapper.selectById(forumArticle.getBoardId());

           if(board == null|| board.getPostType() == Constants.ZERO&& !isAdmin){
               throw new BusinessException("二级板块不存在");
           }
           forumArticle.setBoardId(0);
           forumArticle.setBoardName("");

        }

    }

    /**
     * 上传附件
     * */
    public void uploadAttachment(ForumArticle forumArticle,ForumArticleAttachment forumArticleAttachment,
                                 MultipartFile file,Boolean isUpdate){

        Integer allowSizeWeb = SysCacheUtils.getSysSetting().getPostDto().getAttachmentSize();
        long allowSize = allowSizeWeb* Constants.FILE_SIZE_1M;
        if(file.getSize()>allowSize){
            throw new BusinessException("附件最大只能上传"+ allowSize+"MB");
        }
        ForumArticleAttachment dbInfo  = null;
        // 修改
        if(isUpdate){
            QueryWrapper<ForumArticleAttachment> qw =
                    new QueryWrapper<>();
            QueryWrapper<ForumArticleAttachment> articleId = qw.eq("article_id", forumArticle.getArticleId());
            List<ForumArticleAttachment> forumArticleAttachments = forumArticleAttachmentMapper.selectList(articleId);
            if(!forumArticleAttachments.isEmpty()){
                dbInfo  = forumArticleAttachments.get(0);
                File file1 = new File(appConfig.getProjectFolder()+Constants.FILE_FOLDER_FILE+
                        Constants.FILE_FOLDER_ATTACHMENT+dbInfo.getFile_path());
                file1.delete();// 文件删除
            }
        }
        FileUploadDto fileUploadDto = fileUtils
                .uploadFile2Local(file, Constants.FILE_FOLDER_ATTACHMENT,
                        FileUploadTypeEnum.ARTICLE_ATTACHMENT);
        if(dbInfo == null){
            forumArticleAttachment
                    .setFile_id(StringTools.getRandomNumber(Constants.LENGTH_15));

            forumArticleAttachment
                    .setArticle_id(forumArticle.getArticleId());

            forumArticleAttachment
                    .setFile_path(fileUploadDto.getLocalPath());
            forumArticleAttachment
                    .setFile_size(file.getSize());

            forumArticleAttachment
                    .setUser_id(forumArticle.getUserId());
            forumArticleAttachment
                    .setDownload_count(Constants.ZERO);
            forumArticleAttachment
                    .setFile_type(AttachmentFileTypeEnum.ZIP.getType());
            forumArticleAttachmentMapper.insert(forumArticleAttachment);

        }else{
            ForumArticleAttachment updateInfo = new ForumArticleAttachment();
            updateInfo.setFile_name(fileUploadDto.getOriginalFileName());
            updateInfo.setFile_size(file.getSize());
            updateInfo.setFile_path(fileUploadDto.getLocalPath());
            forumArticleAttachmentMapper.insert(forumArticleAttachment);
        }

//        forumArticleAttachmentMapper.insert(forumArticleAttachment);
    }


    /**
     * 文章编辑修改
     * */
    @Override
    public void updateArticle(Boolean isAdmin, ForumArticle article,
                              ForumArticleAttachment articleAttachment,
                              MultipartFile cover,
                              MultipartFile attachment){

       ForumArticle dbInfo = forumArticleMapper
               .selectById(article.getArticleId());

       if(!isAdmin&&!dbInfo.getUserId().equals(article.getUserId())){
           throw new BusinessException(ResponseCodeEnum.CODE_600);
       }
       article.setLastUpdateTime(new Date());
       resetBoardInfo(isAdmin,article);
       if(cover!=null){
           FileUploadDto fileUploadDto =
                   fileUtils.uploadFile2Local(cover,Constants.FILE_FOLDER_IMAGE,
                           FileUploadTypeEnum.ARTICLE_COVER);
          article.setCover(fileUploadDto.getLocalPath());
       }
        if(attachment!=null){
            uploadAttachment(article,articleAttachment,
                    attachment,true);
            article.setAttachmentType(ArticleAttachmentTypeEnum
                    .HAVE_ATTACHMENT
                    .getType());
        }
        ForumArticleAttachment dbAttachment = null;
//        QueryWrapper<ForumArticleAttachment> qw
//                = ;

        QueryWrapper<ForumArticleAttachment> articleId
                = new QueryWrapper<ForumArticleAttachment>().eq("article_id", article.getArticleId());
        List<ForumArticleAttachment> forumArticleAttachments = forumArticleAttachmentMapper.selectList(articleId);
        if(!forumArticleAttachments.isEmpty()){
            dbAttachment = forumArticleAttachments.get(0);
        }

        if(dbAttachment!=null){

            // 附件删除
            if(article.getAttachmentType()==Constants.ZERO){
                new File(appConfig.getProjectFolder()+Constants.FILE_FOLDER_FILE+
                        Constants.FILE_FOLDER_ATTACHMENT+dbAttachment.getFile_path()).delete();

            }else{
                // 更新积分
                if(!Integer.toString(dbAttachment.getIntegral()).equals(dbAttachment.getIntegral())){
                   ForumArticleAttachment integralUpdate = new ForumArticleAttachment();
                   integralUpdate.setIntegral(articleAttachment.getIntegral());
                   forumArticleAttachmentMapper.updateById(integralUpdate);
                 }
            }
        }

        // 文章是否需要审核
        if(isAdmin){
            article.setStatus(ArticleStatusEnum.AUDTI.getStatus());

        }else{
            SysSetting4AuditDto auditDto = SysCacheUtils.getSysSetting().getAuditDto();
            article.setStatus(auditDto.getPostAudit()?ArticleStatusEnum.NO_AUDIT.getStatus() :
                    ArticleStatusEnum.AUDTI.getStatus());
        }
        // 替换图片  防止用户贴图
        String content = article.getContent();
        if(!StringTools.isEmpty(content)){
            try {
                String month = imageUtils.resetImageHtml(content);
                String replaceMonth = "/"+month+"/";
                content = content
                        .replace(Constants.FILE_FOLDER_TEMP,replaceMonth);
                article.setContent(content);
                String markdownContent = article.getMarkdownContent();
                if(!StringTools.isEmpty(markdownContent)){
                    content.replace(Constants.FILE_FOLDER_TEMP,replaceMonth);
                    article.setContent(markdownContent);
                }

            }catch (Exception e){
                System.out.println("错误");
            }

        }

        forumArticleMapper.updateById(article);

    }

}

