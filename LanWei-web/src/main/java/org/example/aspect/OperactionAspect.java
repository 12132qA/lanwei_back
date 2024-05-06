package org.example.aspect;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.Mappers.ForumArticleMapper;
import org.example.Mappers.ForumCommentMapper;
import org.example.Mappers.LikeRecordMapper;
import org.example.Utils.*;
import org.example.annotation.GlobalInterceptor;
import org.example.annotation.VerifyParam;
import org.example.dto.SessionWebUserDto;
import org.example.dto.SysSettingDto;
import org.example.enums.DateTimePatternEnum;
import org.example.enums.ResponseCodeEnum;
import org.example.enums.UserOperFrequencyTyoeEnum;
import org.example.exception.BusinessException;
import org.example.pojo.ForumArticle;
import org.example.pojo.ForumComment;
import org.example.pojo.LikeRecord;
import org.example.pojo.contants.Constants;
import org.example.vo.ResponseVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.text.ParseException;
import java.util.Date;


@Component
@Aspect
public class OperactionAspect {

    private static final Logger logger = LoggerFactory.getLogger(OperactionAspect.class);

    private static final String[] TYPE_BASES = {
            "java.lang.String",
            "java.lang.Integer",
            "java.lang.Long"};
//    private static final String TYPE_STRING = "java.lang.String";
//
//    private static final String TYPE_INTEGER= "java.lang.Integer";
//    private static final String TYPE_Long= "java.lang.Long";

    @Autowired
    private ForumArticleMapper forumArticleMapper;

    @Autowired
    private ForumCommentMapper forumCommentMapper;

    @Autowired
    private LikeRecordMapper likeRecordMapper;

    /**
     * 定义切点
     *
     * */
    @Pointcut("@annotation(org.example.annotation.GlobalInterceptor)")
    private void requestInterceptor(){

    }

    @Around("requestInterceptor()")
    public Object interceptorDo(ProceedingJoinPoint point){

        try{
            Object target =  point.getTarget();
            Object[] arguments = point.getArgs();
            String methodName = point.getSignature().getName();

            Class<?>[] prameterTypes = ((MethodSignature) point.getSignature())
                    .getMethod().getParameterTypes();

            Method method = target.getClass().getMethod(methodName,prameterTypes);
            if(method == null){
                System.out.println("无此方法");
            }

            GlobalInterceptor interceptor = method.getAnnotation(GlobalInterceptor.class);

            if(interceptor == null){
               return null;
            }
            /**
             * 校验登录
             */

            if(interceptor.checkLogin()){
                checkLogin();
                return null;
            }

            /**
            校验参数
            * **/
            if(interceptor.checkParams()){
                validateParams(method,arguments);
            }
            /**
             * 频次的校验
             * */

            this.checkFrequency(interceptor.frequencyType());
            Object pointResult = point.proceed();

            if(pointResult instanceof ResponseVO){
                ResponseVO responseVO = (ResponseVO) pointResult;
                if(Constants.STATUS_SUCCESS.equals(responseVO.getStatus())){
                   this.addOpCount(interceptor.frequencyType());
                }
            }

            return pointResult;

        }catch (BusinessException e){
            logger.error("全局异常处理器",e);
            throw e;
        } catch (Exception e){
            logger.error("全局异常处理器",e);
           throw  new BusinessException(ResponseCodeEnum.CODE_500);
        } catch (Throwable e){
            throw  new BusinessException(ResponseCodeEnum.CODE_500);
        }

//      return null;
    }

    /**
     *
     * */
    private void addOpCount(UserOperFrequencyTyoeEnum typeEnum) throws ParseException {
      if(typeEnum== null|| typeEnum== UserOperFrequencyTyoeEnum.DO_LIKE){
          return;
      }
      HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        HttpSession session = request.getSession();
        SessionWebUserDto userDto =  (SessionWebUserDto) session.getAttribute(Constants.SESSION_KEY);

        String curDate = DateUtil.format(new Date(), DateTimePatternEnum.YYYY_MM_DD.getPattern());
        String sessionKey = Constants.SESSION_KEY_FREQUENCY+ curDate+typeEnum.getOperType();
        Integer count = (Integer) session.getAttribute(sessionKey);
        session.setAttribute(sessionKey,count+1);

    }

    /**
     * 频次的校验
     * */

    void checkFrequency(UserOperFrequencyTyoeEnum typeEnum) throws ParseException {

        if(typeEnum ==null|| typeEnum==UserOperFrequencyTyoeEnum.NO_CHECK){
            return;
        }
        HttpServletRequest  request = ((ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
        SessionWebUserDto userDto =  (SessionWebUserDto) session.getAttribute(Constants.SESSION_KEY);

        String curDate = DateUtil.format(new Date(), DateTimePatternEnum.YYYY_MM_DD.getPattern());
        String sessionKey = Constants.SESSION_KEY_FREQUENCY+ curDate+typeEnum.getOperType();
        Integer count = (Integer) session.getAttribute(sessionKey);
        SysSettingDto sysSettingDto = SysCacheUtils.getSysSetting();

        switch (typeEnum){
            case POST_ARTICLE:
                if(count == null){
                    QueryWrapper<ForumArticle> wq = new QueryWrapper<>();
                    wq.eq("user_id",userDto.getUserId()).between("post_time",curDate,curDate);
                    count = forumArticleMapper.selectCount(wq);
                }
                if(count>=sysSettingDto.getPostDto().getPostDayCountThread()){
                    throw new BusinessException(ResponseCodeEnum.CODE_602);
                }
                break;
            case POST_COMMENT:
                if(count == null){
                    QueryWrapper<ForumComment> wq = new QueryWrapper<>();
                    wq.eq("user_id",userDto.getUserId())
                            .between("post_time",curDate,curDate);
                    count = forumCommentMapper.selectCount(wq);
               }
                if(count>=sysSettingDto.getCommentDto().getCommentDayCountThreshold()){
                    throw new BusinessException(ResponseCodeEnum.CODE_602);
                }
                break;
            case DO_LIKE:
                if(count == null){
                    QueryWrapper<LikeRecord> wq = new QueryWrapper<>();
                    wq.eq("user_id",userDto.getUserId())
                            .between("post_time",curDate,curDate);
                    count = likeRecordMapper.selectCount(wq);
                }
                if(count>=sysSettingDto.getLikeDto().getLikeDayCountThreshold()){
                    throw new BusinessException(ResponseCodeEnum.CODE_602);
                }
                break;
            case IMAGE_UPLOAD:
                if(count==null) {
                    count = 0;
                }
                if(count>=sysSettingDto.getPostDto().getDayImageUploadCount() ){
                    throw new BusinessException(ResponseCodeEnum.CODE_602);
                }
                break;
        }
        session.setAttribute(sessionKey,count);

    }

    private void checkLogin(){ // Parameter parameter,Object value

        HttpServletRequest request =((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
        Object o = session.getAttribute(Constants.SESSION_KEY);
        if(o == null){
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }
    }
    private void checkObjValue(Parameter parameter,Object value){

    }

    private void validateParams(Method method ,Object[] arguments){

        Parameter[] parameters = method.getParameters();
        int i =0;
        for(Parameter p: parameters){
            Parameter parameter = p;
            Object value = arguments[i];
            VerifyParam verifyParam = parameter.getAnnotation(VerifyParam.class);
            if(verifyParam == null){
                continue;
            }
            if(ArrayUtils.contains(TYPE_BASES,parameter.getParameterizedType().getTypeName()) ){
               checkValue(value,verifyParam);
            }else{

            }
            logger.info(JsonUtils.convertObj2Json(value));
          i++;
        }
    }

    private void checkValue(Object value,VerifyParam verifyParam){
        Boolean isEmpty = value== null|| StringTools.isEmpty(value.toString());
        Integer length = value == null?0:value.toString().length();
        /**
         *
         * 校验空
         */
        if(isEmpty&& verifyParam.required()){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }


        /**
         * 校验长度
         *
         * */
        if(!isEmpty&& (verifyParam.max()!=-1&& verifyParam.max()<length||verifyParam.min()!=-1&& verifyParam.min()>length)){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        /**
        * 校验正则
        * *
         */

        if(!isEmpty&& !StringTools.isEmpty(verifyParam.regx().getRegex())&& !VerifyUtils.verfiy(verifyParam.regx(),String.valueOf(value))){
          throw  new BusinessException(ResponseCodeEnum.CODE_600);
        }

    }

//    @Around()


//    @Around()


}
