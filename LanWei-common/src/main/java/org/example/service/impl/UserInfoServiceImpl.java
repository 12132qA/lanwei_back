package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.ToString;
import org.apache.commons.lang3.AnnotationUtils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.message.Message;
import org.example.Mappers.UserInfoMapper;
import org.example.Mappers.UserIntegerRecordMapper;
import org.example.Mappers.UserMessageMapper;
import org.example.Utils.*;
import org.example.config.WebConfig;
import org.example.dto.SessionWebUserDto;
import org.example.enums.*;
import org.example.exception.BusinessException;
import org.example.pojo.UserInfo;
import org.example.pojo.UserIntegerRecord;
import org.example.pojo.UserMessage;
import org.example.pojo.contants.Constants;
import org.example.service.EmailCodeService;
import org.example.service.UserInfoService;
import org.example.service.UserIntegerRecordService;
import org.example.service.UserMessageService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import javax.jws.soap.SOAPBinding;
import java.awt.*;
import java.sql.Time;
import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    private static final Logger logger = LoggerFactory.getLogger(UserInfoServiceImpl.class);

    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private EmailCodeService emailCodeService;
    @Autowired
    private UserMessageMapper userMessageMapper;
    @Autowired
    private UserIntegerRecordMapper userIntegerRecordMapper;
    @Autowired
    private FileUtils fileUtils;

    @Autowired
    private WebConfig webConfig;

    @Override
    @Transactional(rollbackFor = BusinessException.class)
    public void register(String email, String emailCode, String nickName, String passWord) {

        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email",email);
        UserInfo userInfo = userInfoMapper.selectOne(queryWrapper);

        if(null != userInfo){

            throw new BusinessException("邮箱账号已经存在");
        }

        queryWrapper.eq("nickName",nickName);
        userInfo = userInfoMapper.selectOne(queryWrapper);

        if(userInfo != null){
            throw new BusinessException("昵称已经存在");

        }
        emailCodeService.checkCode(email,emailCode);

        String userId = StringTools.getRandomNumber(Constants.LENGTH_15);

        UserInfo  info = new UserInfo();
        info.setUserId(userId);
        info.setNickName(nickName);
        info.setEmail(email);
        // md5 加密
        info.setPassword(StringTools.encodeM5(passWord));
        info.setJoinTime(new Date());
        info.setTotalIntegral(Constants.ZERO);
        info.setCurrentIntegral(Constants.ZERO);

        this.userInfoMapper.insert(info);

        // 更新用户积分
        updateUserIntegral(userId,UserIntegralOperTypeEnum.REGISTER,UserIntegralChangeTypeEnum.ADD.getChangeType() ,Constants.LENGTH_5);

        // 记录消息

        UserMessage userMessage = new UserMessage();
        userMessage.setReceivedUserId(userId);
        userMessage.setMessageType(MessageTypeEnum.SYS.getType());

        userMessage.setCreateTime(new Date());
        userMessage.setStatus(MessageStatusEnum.NO_READ.getStatus());

        userMessage.setMessageContent(SysCacheUtils.getSysSetting().getRegisterDto().getRegisterWelcomeInfo());

        userMessageMapper.insert(userMessage);
    }
    /****
     *     跟新用户积分
      */
    public void updateUserIntegral(String userId , UserIntegralOperTypeEnum operTypeEnum,
                                   Integer changeType,Integer integral){

        integral = changeType*integral;
        if(integral ==0){
            return;
        }
        UserInfo userInfo = userInfoMapper.selectById(userId);

        if(UserIntegralChangeTypeEnum.REDUCE.getChangeType().equals(changeType) && userInfo.getCurrentIntegral()+integral < 0){
            integral  = changeType* userInfo.getCurrentIntegral();
        }

        UserIntegerRecord userIntegerRecord = new UserIntegerRecord();
        userIntegerRecord.setUser_id(userId);
        userIntegerRecord.setOper_type(operTypeEnum.getOperType());
       userIntegerRecord.setCreate_time(new Date());
       userIntegerRecord.setIntegral(integral);
       userIntegerRecordMapper.insert(userIntegerRecord);

       userInfo.setCurrentIntegral(userInfo.getCurrentIntegral()+integral);

       userInfoMapper.updateById(userInfo);

      int count =  userInfoMapper.updateIntegral(userId,integral);

      if(count ==0){
          throw new BusinessException("更新用户积分失败");
      }

    }

    /**
     *
     * 可能要设置登录超时
     *
     * */
    @Override
    public SessionWebUserDto login(String email,String password, String ip) {

        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("email",email);
        UserInfo userInfo =
                 userInfoMapper.selectOne(userInfoQueryWrapper);

        if(userInfo == null|| !userInfo.getPassword().equals(password)){
            throw  new BusinessException("账号或密码错误");
        }

        if(!UserStatusEnum.ENABLE.getStaus().equals(userInfo.getStatus())){
            throw new BusinessException("账号已禁用");
        }

        String ipAddress = getIpAddress(ip);
        userInfo.setLastLoginTime(new Date());
        userInfo.setLastLoginIp(ip);
        userInfo.setLastLoginIpAddress(ipAddress);

        userInfoMapper.updateById(userInfo);

        SessionWebUserDto sessionWebUserDto = new SessionWebUserDto();
        sessionWebUserDto.setNickName(userInfo.getNickName());
        sessionWebUserDto.setNickName(ip);
        sessionWebUserDto.setNickName(ipAddress);
        sessionWebUserDto.setUserId(userInfo.getUserId());

           /**
            * 判断 email 是否真实存在
            * */
        if(!StringTools.isEmpty(webConfig.getAdminEmails()) && ArrayUtils.contains(webConfig.getAdminEmails().split(","),userInfo.getEmail())){
             sessionWebUserDto.setAdmin(true);
        }else{
            sessionWebUserDto.setAdmin(false);
        }

        return sessionWebUserDto;
    }


    /**
     * 更改个人信息
     * */
    @Override
    public void updateUserInfo(UserInfo userInfo, MultipartFile avatar) {
        userInfoMapper.updateById(userInfo);
       if(avatar!=null){
           fileUtils.uploadFile2Local(avatar,
                   userInfo.getUserId(),FileUploadTypeEnum.AVATAR);
       }

    }

    /**
     *
     * 重置密码
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPwd(String email, String password, String emailCode) {

      QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
      queryWrapper.eq("email",email);
      UserInfo userInfo = userInfoMapper.selectOne(queryWrapper);
      if(userInfo == null){
          throw new BusinessException("邮箱不存在");
      }

        emailCodeService.checkCode(email,emailCode);
        UserInfo updateInfo = new UserInfo();
        updateInfo.setPassword(StringTools.encodeM5(password));
        QueryWrapper<UserInfo> qw = new QueryWrapper<>();
        qw.eq("email",email);
      userInfoMapper.update(updateInfo,qw);
    }

    /**
     *
     * 获取 IP 地址
     * */

    public String getIpAddress(String ip){
        Map<String,String> addressInfo = new HashMap<>();
         try{
         String url = "http://whois.pconline.com.cn/jpJson.jsp?json=true&ip="+ip;
         String responseJson = OkHttpUtils.getRequest(url);
         addressInfo  = JsonUtils.convertJson2Obj(responseJson,Map.class);

         if(responseJson==null){
             return Constants.NO_ADDRESS;
         }

         addressInfo = JsonUtils.convertJson2Obj(responseJson,Map.class);

         return addressInfo.get("pro");

        }catch (Exception e){
             logger.error("获取");
        }
         return Constants.NO_ADDRESS;
    }




}