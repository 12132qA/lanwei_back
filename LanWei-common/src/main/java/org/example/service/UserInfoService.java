package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.dto.SessionWebUserDto;
import org.example.enums.UserIntegralOperTypeEnum;
import org.example.pojo.UserInfo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

public interface UserInfoService extends IService<UserInfo> {

     void register(String email,String emailCode,String nickName,String passWord);

     void updateUserIntegral(String userId, UserIntegralOperTypeEnum operTypeEnum,
                             Integer changeType,Integer integral);

     SessionWebUserDto login(String email,String password,String ip);

     void updateUserInfo(UserInfo userInfo, MultipartFile avatar);

     void resetPwd(String email,String password,String emailCode);
}


