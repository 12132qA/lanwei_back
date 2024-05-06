package org.example.contorller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.example.Mappers.ForumArticleAttachmentMapper;
import org.example.Mappers.ForumArticleMapper;
import org.example.Utils.CopyTools;
import org.example.Utils.StringTools;
import org.example.annotation.GlobalInterceptor;
import org.example.annotation.VerifyParam;
import org.example.config.WebConfig;
import org.example.dto.SessionWebUserDto;
import org.example.enums.*;
import org.example.exception.BusinessException;
import org.example.pojo.*;
import org.example.pojo.contants.Constants;
import org.example.service.ForumArticleAttachmentService;
import org.example.service.impl.*;
import org.example.vo.ResponseVO;
import org.example.vo.web.ForumArticleAttachmentVo;
import org.example.vo.web.ForumArticleDetailVO;
import org.example.vo.web.ForumArticleVO;
import org.example.vo.web.UserDownLoadInfoVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.*;
import java.net.URLEncoder;
import java.util.List;


import static org.example.contorller.ABASE.getUserInfoFormSession;

@RestController
@RequestMapping("/forum")
public class ForumArticleController {

    private static final Logger logger = LoggerFactory.getLogger(ForumArticleController.class);
    @Autowired
    private ForumBoardServiceImpl forumBoardService;

    @Autowired
    private ForumArticleMapper forumArticleMapper;

    @Autowired
    private ForumArticleAttachmentMapper fam;

    @Autowired
    private ForumArticleAttachmentService fas;

    @Autowired
    private ForumArticleServiceImpl forumArticleService;

    @Autowired
    private LikeRecordServiceImpl likeRecordService;

    @Autowired
    private UserInfoServiceImpl userInfoService;

    @Autowired
    private ForumArticleAttachmentDownloadServiceImpl forumArticleAttachmentDownloadService;


    @Autowired
    private ForumArticleAttachmentServiceImpl forumArticleAttachmentService;

    @Autowired
    private WebConfig webConfig;

    @RequestMapping("/loadArticle")
    public ResponseVO loadArticle(HttpSession session,
                                  Integer boardId,
                                  Integer pBoardId ,
                                  Integer orderType,Integer pageNo){
        QueryWrapper<ForumArticle> fw = new QueryWrapper<>();

        Page<ForumArticle> page=new Page<>();
        page.setSize(10);//每页的长度
        if(pageNo ==null){
            pageNo = 1;
        }
        page.setPages(pageNo);//第几页
        page.setCurrent(pageNo);
        ArticleOrderTypeEnum orderTypeEnum = ArticleOrderTypeEnum.getByType(orderType);
        orderTypeEnum = orderType==null?ArticleOrderTypeEnum.HOT:orderTypeEnum;
        /**
         *  分页查询功能自己实现
         * */

        logger.info("sort way:"+ orderType);
        SessionWebUserDto sessionWebUserDto = getUserInfoFormSession(session);

        if(sessionWebUserDto!=null){ //  已登录情况
            fw.eq("status", ArticleStatusEnum.AUDTI.getStatus());
            fw.eq("user_id",sessionWebUserDto.getUserId());
        }else{  // 和 未登录
            fw.eq("status", ArticleStatusEnum.AUDTI.getStatus());
        }

        assert orderTypeEnum != null;
        fw.orderByDesc(orderTypeEnum.getOrderSql());
        // 已审核状态

        Page<ForumArticle> pageResult = forumArticleMapper.selectPage(page, fw);
//        fw.eq("status", ArticleStatusEnum.AUDTI.getStatus());
      return ResponseVO.getSuccessResponseVO(pageResult);
    }


    @RequestMapping("/getArticleDetail")
    @GlobalInterceptor(checkParam = true)
    public ResponseVO getArticleDetail(HttpSession session, @VerifyParam(required = true) String articleId){
        QueryWrapper<ForumArticle> fw = new QueryWrapper<>();

        SessionWebUserDto sessionWebUserDto = getUserInfoFormSession(session);

        ForumArticle forumArticle = forumArticleService.readArticle(articleId);

        boolean canShowNoAudit = false;
        if(sessionWebUserDto!=null){
            canShowNoAudit = sessionWebUserDto.getUserId().equals(forumArticle.getUserId()) || sessionWebUserDto.getAdmin();
        }


       if(forumArticle == null||(ArticleStatusEnum.NO_AUDIT.getStatus().equals(forumArticle.getStatus())
               &&!canShowNoAudit     // x || y <==>  !(!x&&!y)
       ||ArticleStatusEnum.DEL.getStatus().equals(forumArticle.getStatus()))){
           throw new BusinessException(ResponseCodeEnum.CODE_404);
       }
       /***
        * 文件查询
        * */

        ForumArticleDetailVO detailVO
                = new ForumArticleDetailVO();

        detailVO.setForumArticle(CopyTools.copy(forumArticle, ForumArticleVO.class));

        //判断是否存在 有 附件
        if(forumArticle.getAttachmentType() == Constants.ONE){
            QueryWrapper<ForumArticleAttachment> wrapper = new QueryWrapper<>();
            QueryWrapper<ForumArticleAttachment> ai1 = wrapper.eq("article_id", articleId);
            List<ForumArticleAttachment> forumArticleAttachments = fam.selectList(ai1);
            if(!forumArticleAttachments.isEmpty()){
                detailVO.setAttachment(CopyTools.copy(forumArticleAttachments.get(0),
                        ForumArticleAttachmentVo.class));
            }
        }
        // 是否 已经点赞

        if(sessionWebUserDto!=null){
            LikeRecord likeRecord = likeRecordService.getLikeRecordByObjectIdAndUserIdAndOpType(articleId,
                    sessionWebUserDto.getUserId(),
                    OperaRecordOpTypeEnum.ARTICLE_LIKE.getType());
            if(likeRecord == null) {
                detailVO.setHaveLike(true);
            }
        }

        /***
         * 附件信息
         * */
        return ResponseVO.getSuccessResponseVO(detailVO);
    }
/**
 * 文章点赞接口
 * */
   @RequestMapping("/doLike")
   @GlobalInterceptor(checkLogin = true,checkParam = true )
    public ResponseVO dolike(HttpSession session,@VerifyParam(required = true) String articleId){
        SessionWebUserDto sessionWebUserDto = getUserInfoFormSession(session);
        likeRecordService.doLike(articleId,sessionWebUserDto.getUserId(),sessionWebUserDto.getNickName(),OperaRecordOpTypeEnum.ARTICLE_LIKE);
        return ResponseVO.getSuccessResponseVO(null);
    }

    /**
     * 下载文件
     *
     * 1 获取用户下载信息接口
     *
     * */

    @RequestMapping("/getUserDownloadInfo")
    @GlobalInterceptor(checkLogin = true,checkParam = true)
    public ResponseVO getUserDownloadInfo(HttpSession session,@VerifyParam(required = true) String fileId){
        SessionWebUserDto webUserDto = getUserInfoFormSession(session);
        UserInfo userInfo = userInfoService.getById(getUserInfoFormSession(session).getUserId());

        UserDownLoadInfoVO downLoadInfoVO = new UserDownLoadInfoVO();
        downLoadInfoVO.setUserIntegral(userInfo.getCurrentIntegral());

        ForumArticleAttachmentDownload fd = forumArticleAttachmentDownloadService
                .getForumArticleAttachmentDownLoadByFileIdAndUserId(fileId,webUserDto.getUserId());

        if(fd!=null){
            downLoadInfoVO.setHaveDownLoad(true);
        }
       return ResponseVO.getSuccessResponseVO(downLoadInfoVO);
    }

    /**
     * 下载接口
     * */
    @RequestMapping("/attachmentDownload")
    @GlobalInterceptor(checkLogin = true,checkParam = true)
    public void attachmentDownload(HttpSession session, HttpServletRequest request,
                                   HttpServletResponse response,
                                   @VerifyParam(required = true) String fileId){

        ForumArticleAttachment articleAttachment = forumArticleAttachmentService.downloadAttachment(fileId,getUserInfoFormSession(session));
        InputStream in = null;
        OutputStream out = null;
        String download = articleAttachment.getFile_name();
        String filePath = webConfig.getProjectFolder()+Constants.FILE_FOLDER_FILE+Constants.FILE_FOLDER_ATTACHMENT + articleAttachment.getFile_name();

        File file = new File(filePath);

        try{
          in = new FileInputStream(file);
          out = response.getOutputStream();
          response.setContentType("application/x-msdownload; charset=UTF-8");
          // 解决中文乱码问题
            if(request.getHeader("User-Agent").toLowerCase().indexOf("msie")>0){
                // iE 浏览器
               download = URLEncoder.encode(download,"UTF-8");
            }else{
                download = new String(download.getBytes("UTF-8"),"ISO8859-1");
            }
            response.setHeader("Content-Disposition","attachment:filename=\""+download+"\"");
            byte[] bytes = new byte[1024];
            int len = 0;
            while( (len = in.read(bytes))!=-1){
                out.write(bytes,0,len);
            }
            out.flush();
         }catch (Exception e){

            logger.error("下载异常",e);
        }finally {
            try{
               if(in!=null){
                   in.close();
               }
            }catch (IOException e){
                logger.error("IO异常",e);
            }
            try{
                if(out!=null){
                    in.close();
                }
            }catch (IOException e){
                logger.error("IO异常",e);
            }
        }
    }

    //
    @RequestMapping("/loadBoard4Post")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO loadBoard4Post(HttpSession session,
                                     @VerifyParam(required = true)  String fileId){

        SessionWebUserDto userDto = getUserInfoFormSession(session);
        Integer postType = null;

        if(!userDto.getAdmin()){
            postType = Constants.ONE;
        }
//        List<ForumBoard> list = forumBoardService.getBoardTree(postType);
        return ResponseVO.getSuccessResponseVO(forumBoardService.getBoardTree(postType));
    }


    /**
     * 发布文章
     * */

    @RequestMapping("/postArticle")

    @GlobalInterceptor(checkLogin = true,checkParam = true,frequencyType = UserOperFrequencyTyoeEnum.POST_ARTICLE)
    public ResponseVO postArticle(HttpSession session,
                                  MultipartFile cover,
                                  MultipartFile attachment,
                                  Integer integral,
                                  @VerifyParam(required = true,max = 150) String title,
                                  @VerifyParam(required = true)Integer pBoard,
                                  Integer boardId,
                                  @VerifyParam(max = 200) String summary,
                                  @VerifyParam(required = true)Integer editorType,
                                  @VerifyParam(required = true)String content,
                                  String markdownContent){

        title = StringTools.escapeHtml(title);
        SessionWebUserDto userDto = getUserInfoFormSession(session);
//        Integer postType = null;

        ForumArticle forumArticle = new ForumArticle();
        forumArticle.setBoardId(pBoard);
        forumArticle.setTitle(title);
        forumArticle.setSummary(summary);
        forumArticle.setContent(content);
        forumArticle.setPBoardId(pBoard);


        EditorTypeEnum typeEnum = EditorTypeEnum.getByType(editorType);
        if(typeEnum ==null){
           throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        if(EditorTypeEnum.MARKDOWN.getType().equals(editorType)
                &&StringTools.isEmpty(markdownContent)){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

       forumArticle.setMarkdownContent(markdownContent);
        forumArticle.setEditorType(editorType);
        forumArticle.setUserId(userDto.getUserId());
        forumArticle.setNickName(userDto.getNickName());
        forumArticle.setUserIpAddress(userDto.getProvince());

        // 附件信息
        ForumArticleAttachment  forumArticleAttachment = new ForumArticleAttachment();
        forumArticleAttachment.setIntegral(integral == null?0:integral);
        forumArticleService.postArticle(userDto.getAdmin(),forumArticle,forumArticleAttachment,
                cover,attachment);


         return ResponseVO.getSuccessResponseVO(forumArticle.getArticleId());
    }


    @RequestMapping("/articleDetail4Update")
    @GlobalInterceptor(checkParam = true,checkLogin = true)
    public ResponseVO articleDetail4Update(HttpSession session,
                                           @VerifyParam(required = true)  String articleId){
        SessionWebUserDto userDto = getUserInfoFormSession(session);
        ForumArticle forumArticle = forumArticleService.getById(articleId);
        if(forumArticle==null|| !forumArticle.getUserId().equals(userDto.getUserId())){
          throw new BusinessException("文章不存在或你无权编辑该文章");
        }
        ForumArticleDetailVO detailVO = new ForumArticleDetailVO();
        detailVO.setForumArticle(CopyTools.copy(forumArticle,ForumArticleVO.class));
        if(forumArticle.getAttachmentType()==Constants.ONE){
            QueryWrapper<ForumArticleAttachment> wrapper = new QueryWrapper<>();
            QueryWrapper<ForumArticleAttachment> ai1 = wrapper.eq("articleId", articleId);
            List<ForumArticleAttachment> forumArticleAttachments = fam.selectList(ai1);
            if(!forumArticleAttachments.isEmpty()){
                detailVO.
                        setAttachment(CopyTools.copy(forumArticleAttachments.get(0),
                                ForumArticleAttachmentVo.class));
            }
        }
        return ResponseVO
                .getSuccessResponseVO(detailVO);


    }

    @RequestMapping("/updateArticle")
    @GlobalInterceptor(checkLogin = true,checkParam = true)
    public ResponseVO updateArticle(HttpSession session,
                                  MultipartFile cover,
                                  MultipartFile attachment,
                                  Integer integral,
                                  @VerifyParam(required = true,max = 150) String title,
                                  @VerifyParam(required = true)Integer pBoard,
                                  @VerifyParam(required = true,max = 150)String articleId,
                                  Integer boardId,
                                  @VerifyParam(max = 200) String summary,
                                  @VerifyParam(required = true)Integer editorType,
                                  @VerifyParam(required = true)String content,
                                  String markdownContent,
                                    @VerifyParam Integer attachmentType){

        title = StringTools.escapeHtml(title);
        SessionWebUserDto userDto = getUserInfoFormSession(session);

        ForumArticle forumArticle = new ForumArticle();
        forumArticle.setBoardId(pBoard);
        forumArticle.setTitle(title);
        forumArticle.setSummary(summary);
        forumArticle.setContent(content);
        forumArticle.setPBoardId(pBoard);


        EditorTypeEnum typeEnum = EditorTypeEnum.getByType(editorType);
        if(typeEnum ==null){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        if(EditorTypeEnum.MARKDOWN.getType().equals(editorType)
                &&StringTools.isEmpty(markdownContent)){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        forumArticle.setMarkdownContent(markdownContent);
        forumArticle.setEditorType(editorType);
        forumArticle.setUserId(userDto.getUserId());
        forumArticle.setNickName(userDto.getNickName());
        forumArticle.setUserIpAddress(userDto.getProvince());

        // 附件信息
        ForumArticleAttachment  forumArticleAttachment = new ForumArticleAttachment();
        forumArticleAttachment.setIntegral(integral == null?0:integral);
        forumArticleService.postArticle(userDto.getAdmin(),forumArticle,forumArticleAttachment,
                cover,attachment);

        return ResponseVO.getSuccessResponseVO
                (forumArticle.getArticleId());
    }

    /**
     * 搜索功能
     * */

    @RequestMapping("/search")
    @GlobalInterceptor(checkParam = true)
    public ResponseVO search(@VerifyParam(required = true,min = 3)  String keyWord){
//        SessionWebUserDto userDto = getUserInfoFormSession(session);

        QueryWrapper<ForumArticle> wq = new QueryWrapper<>();
        wq.eq("title",keyWord);

        Page<ForumArticle> forumArticlePage = forumArticleMapper.selectPage(new Page<>(1, 100), wq);


        return ResponseVO
                .getSuccessResponseVO(forumArticlePage);


    }


}
