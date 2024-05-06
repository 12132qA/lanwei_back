package org.example.contorller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.Mappers.ForumCommentMapper;
import org.example.Utils.CopyTools;
import org.example.Utils.StringTools;
import org.example.Utils.SysCacheUtils;
import org.example.annotation.GlobalInterceptor;
import org.example.annotation.VerifyParam;
import org.example.dto.SessionWebUserDto;
import org.example.dto.SysSettingDto;
import org.example.enums.*;
import org.example.exception.BusinessException;
import org.example.pojo.ForumComment;
import org.example.pojo.LikeRecord;
import org.example.pojo.contants.Constants;
import org.example.query.ForumCommentQuery;
import org.example.service.impl.ForumCommentServiceImpl;
import org.example.service.impl.LikeRecordServiceImpl;
import org.example.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 评论
 * */

@RestController
@RequestMapping("/comment")
public class ForumCommentController {

    @Autowired
    private ForumCommentServiceImpl forumCommentService;
    @Autowired
    private ForumCommentMapper forumCommentMapper;
    @Autowired
    private LikeRecordServiceImpl likeRecordService;


    @RequestMapping("/loadComment")
    @GlobalInterceptor(checkParam  = true)
    public ResponseVO loadComment(HttpSession session,
                                  @VerifyParam(required = true) String articleId,
                                  Integer pageNo ,
                                  Integer orderType){

        final String order_type0 = "good_count desc,comment_id asc";
        final String order_type1 = "comment_id desc";

        SysSettingDto sysSetting = SysCacheUtils.getSysSetting();
        // 暂时不做判断
//
//        if(!SysCacheUtils.getSysSetting().getCommentDto().getCommentOpen()){
//            throw new BusinessException(ResponseCodeEnum.CODE_600);
//        }
        //  对 查询 结果 根据 指定的传入字段 进行排序
        QueryWrapper<ForumComment> wq = new QueryWrapper<>();
        int ju = (orderType==null|| orderType.equals(Constants.ZERO)) ?0:1;
        QueryWrapper<ForumComment> eq =  null;
        if(ju==0){
          eq=   wq.eq("article_id", articleId).orderByDesc("top_type ,good_count").orderByAsc("comment_id");
        }else{
           eq =  wq.eq("article_id", articleId).orderByDesc("top_type ,comment_id");
        }
        //        Page<ForumComment> page = new Page<>(pageNo,50);
        // 暂时不做分页

        SessionWebUserDto userDto = ABASE.getUserInfoFormSession(session);

       if(userDto!=null){ //  判断是否登录 没登录
           wq =  eq.eq("user_id",userDto.getUserId());
//                  .eq("like_type",true);
       }else{   //  没登录 未审核
           wq =  eq.eq("status", ArticleStatusEnum.AUDTI.getStatus());
         }
         wq =  wq.eq("p_comment_id",0); // 查询一级评论

        List<ForumCommentQuery> listByParam = forumCommentService.findListByParam(wq);
        return ResponseVO.getSuccessResponseVO(listByParam);

    }


    /***
     * 文章点赞
     * */

    @RequestMapping("/doLike")
    @GlobalInterceptor(checkParam = true,
            checkLogin = true,
             frequencyType = UserOperFrequencyTyoeEnum.DO_LIKE)
    public ResponseVO doLike( HttpSession session,
                              @VerifyParam(required = true)
                              String commentId){

        SessionWebUserDto userDto = ABASE.getUserInfoFormSession(session);
        String objectId = String.valueOf(commentId);
        likeRecordService.doLike(objectId,userDto.getUserId(),userDto.getNickName(),
                OperaRecordOpTypeEnum.COMMENT_LIKE);

        LikeRecord likeRecord = likeRecordService
                .getLikeRecordByObjectIdAndUserIdAndOpType(objectId,userDto.getUserId(),
                        OperaRecordOpTypeEnum.COMMENT_LIKE.getType());

        ForumComment comment = forumCommentService.getForumCommentByCommentId(commentId);
        ForumCommentQuery copy = CopyTools.copy(comment, ForumCommentQuery.class);
//        comment.setLikeType(likeRecord ==null?0:1);
        copy.setLikeType(likeRecord ==null?0:1);


        return ResponseVO.getSuccessResponseVO(copy);
    }



    /**
     * 设置置顶
     * */


    @RequestMapping("/changeTopType")
    @GlobalInterceptor(checkParam = true,checkLogin = true)
    public ResponseVO changeTopChange(HttpSession session,
                                      @VerifyParam(required = true) String commentId,
                                      @VerifyParam(required = true) Integer topType){


        forumCommentService.changeTopType(ABASE.getUserInfoFormSession(session).getUserId(),commentId,topType);

        return ResponseVO.getSuccessResponseVO(null);

    }


    @RequestMapping("/postComment")
    @GlobalInterceptor(checkParam = true,checkLogin = true,frequencyType = UserOperFrequencyTyoeEnum.POST_COMMENT)
    public ResponseVO postComment(HttpSession session,
                                  @VerifyParam(required = true) String articleId,
                                  @VerifyParam(required = true) Integer pCommentId,
                                  @VerifyParam(required = true)String content,
                                  MultipartFile image,
                                  String replyUserId){

        if(!SysCacheUtils.getSysSetting().getCommentDto().getCommentOpen()){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        if(image == null && StringTools.isEmpty(content)){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }


        SessionWebUserDto userDto = ABASE.getUserInfoFormSession(session);
        ForumComment forumComment = new ForumComment();
        content = StringTools.escapeHtml(content);
        forumComment.setUserId(userDto.getUserId());
        forumComment.setNickName(userDto.getNickName());
        forumComment.setCommentId(pCommentId);
        forumComment.setArticleId(articleId);
        forumComment.setContent(content);
        forumComment.setReplyUserId(replyUserId);
        forumComment.setTopType(CommentTopTypeEnum.NO_TOP.getType());

        forumCommentService.postComment(forumComment,image);

        if(pCommentId!=0){
            QueryWrapper<ForumComment> qw = new QueryWrapper<>();
            ForumComment fC  = new ForumComment();
            QueryWrapper<ForumComment> eq = qw.eq("article_id", articleId)
                    .eq("p_comment_id", pCommentId);

            List<ForumCommentQuery> listByParam = forumCommentService.findListByParam(eq);

            return ResponseVO.getSuccessResponseVO(listByParam);
        }


        return ResponseVO.getSuccessResponseVO(forumComment);


    }


}