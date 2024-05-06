package org.example.contorller;

import lombok.extern.slf4j.Slf4j;
import org.example.Utils.CreateImageCode;
import org.example.Utils.StringTools;
import org.example.Utils.SysCacheUtils;
import org.example.annotation.GlobalInterceptor;
import org.example.annotation.VerifyParam;
import org.example.dto.SessionWebUserDto;
import org.example.dto.SysSetting4CommentDto;
import org.example.dto.SysSettingDto;
import org.example.enums.ResponseCodeEnum;
import org.example.enums.VerifyRegexEnum;
import org.example.exception.BusinessException;
import org.example.pojo.contants.Constants;
import org.example.service.EmailCodeService;
import org.example.service.UserInfoService;
import org.example.service.impl.UserInfoServiceImpl;
import org.example.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.example.contorller.ABASE.getIpAddr;
import static org.example.contorller.ABASE.getUserInfoFormSession;
import static org.example.vo.ResponseVO.getSuccessResponseVO;

@Slf4j
@RestController
public class AccountController {
   @Autowired
    private EmailCodeService emailCodeService;
   @Autowired
   private UserInfoServiceImpl userInfoService;

    /***
     * 验证码
     */


    @RequestMapping("/checkCode")
    public void checkCode(HttpServletResponse httpServletResponse,
                          HttpSession httpSession,Integer type) throws IOException {
        System.out.println("验证码 请求成功");
        CreateImageCode vCode = new CreateImageCode(130, 38, 5, 10);
        httpServletResponse.setHeader("pragma", "no-cache");
        httpServletResponse.setHeader("Cache-Control", "no-cache");
        httpServletResponse.setDateHeader("Expires", 0);
        httpServletResponse.setContentType("image/jpeg");
        String code = vCode.getCode();
        // 登录注册
        if(code!=null){
            log.info("登录成功");
        }
        if (type == null || type == 0||type==1) {
            httpSession.setAttribute(Constants.CHECK_CODE_KEY, code);
        } else {
            // 获取邮箱
            httpSession.setAttribute(Constants.CHECK_CODE_KEY_EMAIL, code);
        }
        // 写入流数据
        vCode.write(httpServletResponse.getOutputStream()); //  调用相关接口
    }


    /**
     *org.example.contorller.AccountController.sendEmailCode
     * 邮箱注册前请求验证码接口
     * */
    @RequestMapping("/sendEmailCode")
    @GlobalInterceptor(checkParam = true)
    public ResponseVO sendEmailCode(HttpSession session
            ,@VerifyParam(required = true) String email,
                                    @VerifyParam(required = true) String checkCode,
                                    @VerifyParam(required = true) Integer type){
         /**
          * 校验一下
          *
          * */
         try{
             if(StringTools.isEmpty(email)||StringTools.isEmpty(checkCode)||type==null ){
                 throw new BusinessException(ResponseCodeEnum.CODE_600);
             }
             emailCodeService.sendEmailCode(email,type);

//       session.getAttribute(Constants.CHECK_CODE_KEY).equals(checkCode);
             return  ResponseVO.getSuccess();
         } finally {
             // 已经校验的验证码 错误
             session.removeAttribute(Constants.CHECK_CODE_KEY_EMAIL );
         }
    }

   @RequestMapping("/register")
   @GlobalInterceptor(checkParam = true)
    public ResponseVO register(HttpSession httpSession,
       @VerifyParam(required = true,regx = VerifyRegexEnum.EMAIL,max = 150) String email,
       @VerifyParam(required = true,max = 20) String emailCode ,
       @VerifyParam(required = true,max = 20) String nickName,
       @VerifyParam(required = true,min = 8,max = 18,regx = VerifyRegexEnum.PASSWORD) String password , String checkCode)  {
       log.info("验证码 请求成功");
      try{
          if(StringTools.isEmpty(email)||StringTools.isEmpty(emailCode) ||
                  StringTools.isEmpty(nickName)||
                  StringTools.isEmpty(password)||StringTools.isEmpty(checkCode)){
              throw new BusinessException(ResponseCodeEnum.CODE_600);
          }
          if(!checkCode.equalsIgnoreCase(
                  (String) httpSession.getAttribute(Constants.CHECK_CODE_KEY) )){
            throw new BusinessException("图片验证码错误");
          }


          userInfoService.register(email,emailCode,nickName,password);
          return ResponseVO.getSuccess();

      }finally {
          httpSession.removeAttribute(Constants.CHECK_CODE_KEY);
      }
    }

    /**
     *
     * */

    @RequestMapping("/login")
    @GlobalInterceptor(checkParam = true)
    public ResponseVO login(HttpSession httpSession,
      HttpServletRequest request,
      @VerifyParam(required = true) String email,
     @VerifyParam(required = true) String checkCode,
     @VerifyParam(required = true) String password )  {
        log.info("验证码 请求成功");
        try{


            log.info(": ->"+httpSession.getAttribute(Constants.CHECK_CODE_KEY) );

            if(!checkCode.equalsIgnoreCase(
                    (String) httpSession.getAttribute(Constants.CHECK_CODE_KEY) )){
                throw new BusinessException("图片验证码错误");
            }
            SessionWebUserDto  sessionWebUserDto = userInfoService.login(email,password,getIpAddr(request));
            httpSession.setAttribute(Constants.SESSION_KEY,sessionWebUserDto);
            log.info("session:" + sessionWebUserDto);

            return getSuccessResponseVO(sessionWebUserDto);
        }finally {
            httpSession.removeAttribute(Constants.CHECK_CODE_KEY);
        }

//      return null;
    }

    @RequestMapping("/getUserInfo")
    @GlobalInterceptor(checkParam = true)
    public ResponseVO getUserInfo(HttpSession httpSession)  {
       log.info("验证码 请求成功");

       return getSuccessResponseVO(getUserInfoFormSession(httpSession));
    }


    @RequestMapping("/logout")
    @GlobalInterceptor()
    public ResponseVO logout(HttpSession httpSession)  {
        log.info("退出登录");
         httpSession.invalidate();
        return getSuccessResponseVO(null);
        //   return null;
    }

    @RequestMapping("/getSysSetting")
    @GlobalInterceptor()
    public ResponseVO getSysSetting(HttpSession httpSession)  {
//        log.info("退出登录");
//        httpSession.invalidate();

        SysSettingDto sysSettingDto = SysCacheUtils.getSysSetting();
        SysSetting4CommentDto commentDto =  sysSettingDto.getCommentDto();
        Map<Object,Object> result = new HashMap<>();
        result.put("commentOpen",commentDto.getCommentOpen());
        return getSuccessResponseVO(result);
        //   return null;
    }

    @RequestMapping("/resetPwd")
    @GlobalInterceptor(checkParam = true)
    public ResponseVO resetPwd(HttpSession httpSession,
      @VerifyParam(required = true) String email,
      @VerifyParam(required = true,min = 8,max = 18,regx = VerifyRegexEnum.PASSWORD) String password,
      @VerifyParam(required = true) String checkCode,
      @VerifyParam(required = true) String emailCode )  {
        log.info("重置密码 ");
//        httpSession.invalidate();

        try {
            if(!checkCode.equalsIgnoreCase(
                    (String) httpSession.getAttribute(Constants.CHECK_CODE_KEY) )){
                throw new BusinessException("图片验证码错误");
            }

            userInfoService.resetPwd(email,password,emailCode);

            /**
             * 对项目进行缓存
             * */

            SysSettingDto sysSettingDto = SysCacheUtils.getSysSetting();
            SysSetting4CommentDto commentDto =  sysSettingDto.getCommentDto();
            Map<Object,Object> result = new HashMap<>();
            result.put("commentOpen",commentDto.getCommentOpen());
            return getSuccessResponseVO(result);
        }finally {
            httpSession.removeAttribute(Constants.CHECK_CODE_KEY);
        }


        //   return null;
    }




}