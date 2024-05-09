package org.example.contorller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.Mappers.*;
import org.example.Utils.CopyTools;
import org.example.annotation.GlobalInterceptor;
import org.example.annotation.VerifyParam;
import org.example.dto.SessionWebUserDto;
import org.example.enums.*;
import org.example.exception.BusinessException;
import org.example.pojo.*;
import org.example.service.UserInfoService;
import org.example.service.impl.ForumArticleServiceImpl;
import org.example.service.impl.UserIntegerRecordServiceImpl;
import org.example.service.impl.UserMessageServiceImpl;
import org.example.vo.ResponseVO;
import org.example.vo.web.UserInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/userCenter")
public class UserCenterController {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private ForumArticleServiceImpl forumArticleService;

    @Autowired
    private UserIntegerRecordServiceImpl userIntegerRecordService;
    @Autowired
    private UserIntegerRecordMapper userIntegerRecordMapper;
    @Autowired
    private ForumArticleMapper forumArticleMapper;

    @Autowired
    private ForumCommentMapper forumcommentMapper;

    @Autowired
    private LikeRecordMapper likeRecordMapper;

    @Autowired
    private UserMessageServiceImpl userMessageService;

    @Autowired
    private UserMessageMapper userMessageMapper;

    @RequestMapping("/getUserInfo")
    @GlobalInterceptor(checkParam = true)
    public ResponseVO getUserInfo(@VerifyParam(required = true) String userId){
         // 获取 用户信息
            UserInfo userInfo = userInfoService.getById(userId);
        if(null == userInfo|| UserStatusEnum.DISABLE.getStaus().equals(userInfo.getStatus())){
            throw new BusinessException(ResponseCodeEnum.CODE_404);
        }

        QueryWrapper<ForumArticle> is = new QueryWrapper<>();
        QueryWrapper<ForumArticle> eq = is.eq("user_id", userId).eq("status", ArticleStatusEnum.AUDTI.getStatus());
        Integer postCount = forumArticleMapper.selectCount(eq);

        UserInfoVO userInfo1 = CopyTools.copy(userInfo, UserInfoVO.class);
        userInfo1.setPostCount(postCount);

        QueryWrapper<LikeRecord> qw = new QueryWrapper<>();
        qw.eq("user_id",userId);

        Integer i = likeRecordMapper.selectCount(qw);
        userInfo1.setPostCount(i);

        return ResponseVO.getSuccessResponseVO(userInfo1);
    }


    @RequestMapping("/loadUserArticle")
    @GlobalInterceptor(checkParam = true)
    public ResponseVO loadUserArticle(HttpSession session,
                                      @VerifyParam(required = true) String userId,
                                      @VerifyParam(required = true) Integer type,
                                      Integer pageNo){

        UserInfo userInfo = userInfoService.getById(userId);
        if(null == userInfo|| UserStatusEnum.DISABLE.getStaus().equals(userInfo.getStatus())){
            throw new BusinessException(ResponseCodeEnum.CODE_404);
        }
        QueryWrapper<ForumArticle> is = new QueryWrapper<>();
        QueryWrapper<ForumComment> is1 = new QueryWrapper<>();
        QueryWrapper<LikeRecord> is2 = new QueryWrapper<>();
        QueryWrapper<ForumArticle> eq = is.orderByDesc("post_time");

        /**
         * 多表联合查询
         * */
       if(type == null){
           type = 0;
       }
       if(pageNo==null){
           pageNo = 1;
       }
        // 创建存储 的 列表
        List<String> ids = new ArrayList<>();
        if(type == 0 ){
            eq= eq.eq("status", 1);
            eq = eq.or();
            eq = eq.eq("user_id",userId);
        }else if(type == 1){ // 评论
            eq= eq.eq("status", 1);
            is1 =is1.eq("user_id",userId);
            List<ForumComment> forumComments = forumcommentMapper.selectList(is1);
//            List<String> ids = new ArrayList<>();
            // 添加 值
            forumComments.forEach(e-> ids.add(e.getArticleId()));
            // 判断  值 是否相同
            eq = eq.in("article_id",is);
        }else if(type== 2){  // 点赞
            is2 = is2.and(i-> i.eq("op_type",0)); // 判断 是否 是同种类型
            List<LikeRecord> likeRecords = likeRecordMapper.selectList(is2); //
//            List<String> ids = new ArrayList<>();
            likeRecords.forEach(e-> ids.add(e.getObject_id()) );
            is2 = is2.eq("article_id",userId);
        }
        SessionWebUserDto userDto = ABASE.getUserInfoFormSession(session);
        if(userDto!=null){
              eq = eq.eq("user_id",userId);
        }else{
            eq = eq.eq("status",ArticleStatusEnum.AUDTI.getStatus());
        }
        // 分页查询
        //
        Page<ForumArticle> page = new Page<>();

         // 判断 查询 列表是否为空

        // 分页查询

//        if(!ids.isEmpty()){
//               //
//            for(String id: ids  ){
//                ForumArticle forumArticle = forumArticleMapper.selectById(id);
//                if(forumArticle != null){
//                }
//            }
//        }else{
//            page = forumArticleMapper.selectPage(new Page<>(pageNo, 10), eq);
//        }


        return ResponseVO.getSuccessResponseVO(page);
    }

    /**
     * 修改用户信息
     * */

    @RequestMapping("/updateUserInfo")
    @GlobalInterceptor(checkParam = true)
    public ResponseVO updateUserInfo(HttpSession session,
                                     Integer sex,
                                     @VerifyParam(max = 100) String personDescription,
                                     MultipartFile avatar){

        SessionWebUserDto userDto = ABASE.getUserInfoFormSession(session);
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userInfo.getUserId());
        userInfo.setSex(sex);
        userInfo.setPersonDescription(personDescription);
        userInfoService.updateUserInfo(userInfo,avatar);

        return ResponseVO.getSuccessResponseVO(null);
    }
    /**
     * 获取用户的消费记录
     * */

    @RequestMapping("/loadUserItegralRecord")
    @GlobalInterceptor(checkParam = true)
    public ResponseVO loadUserIntegralRecord(HttpSession session,Integer pageNo,
                                             String createTimeStart
            ,String createTimeEnd){

        QueryWrapper<UserIntegerRecord> qw = new QueryWrapper<>();
        qw.eq("user_id",ABASE.getUserInfoFormSession(session)
                .getUserId())
                .between("create_time",createTimeStart,createTimeEnd)
                .orderByDesc("record_id");



        Page page1 = userIntegerRecordMapper.selectPage(new Page(1,20), qw);

        List<UserIntegerRecord> records = page1.getRecords();

        // get 对应方法中也有
        records.forEach(r->{
            UserIntegralOperTypeEnum operTypeEnum = UserIntegralOperTypeEnum.getByType(r.getOper_type());
            r.setOperTypeName( operTypeEnum ==null?"":operTypeEnum.getDesc());
        });

        return ResponseVO.getSuccessResponseVO(page1);
    }

    @RequestMapping("/getMessageCount")
    @GlobalInterceptor(checkParam = true)
    public ResponseVO getMessageCount(HttpSession session){

       SessionWebUserDto userDto = new SessionWebUserDto();
       return ResponseVO.getSuccessResponseVO(userMessageService
               .getUserMessageCount(userDto.getUserId()));

    }


    @RequestMapping("/loadMessageList")
    @GlobalInterceptor(checkParam = true)
    public ResponseVO loadMessageList(HttpSession session,
                                      @VerifyParam(required = true)String code,
                                      Integer pageNo){
        MessageTypeEnum typeEnum = MessageTypeEnum.getByCode(code);
        if(typeEnum == null){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        SessionWebUserDto userDto = new SessionWebUserDto();
        QueryWrapper<UserMessage> wq = new QueryWrapper<>();

        wq.eq("received_user_id",userDto.getUserId())
                .eq("message_type",typeEnum.getType())
                .orderByDesc("message_id");

        userMessageMapper.selectPage(new Page<>(pageNo,10),wq);

        if(pageNo==null||pageNo==1){
            // 标记类型 的 方法
            userMessageService.readMessageByType(userDto.getUserId(),typeEnum.getType());

        }

        return ResponseVO.getSuccessResponseVO(userMessageService.getUserMessageCount(userDto.getUserId()));
    }

}