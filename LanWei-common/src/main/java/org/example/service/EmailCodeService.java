package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.*;
import org.example.pojo.EmailCode;
import org.example.pojo.ForumArticle;
import org.springframework.stereotype.Service;


public interface EmailCodeService extends IService<EmailCode> {

   void sendEmailCode(String email,Integer type);

   void checkCode(String email,String emailCode);

}
