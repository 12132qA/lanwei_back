package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sun.xml.internal.org.jvnet.mimepull.MIMEMessage;
import javafx.scene.effect.SepiaTone;
import org.example.Mappers.EmailCodeMapper;
import org.example.Mappers.UserInfoMapper;
import org.example.Utils.StringTools;
import org.example.config.WebConfig;
import org.example.exception.BusinessException;
import org.example.pojo.EmailCode;
import org.example.pojo.UserInfo;
import org.example.pojo.contants.Constants;
import org.example.service.EmailCodeService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
//import org.springframework.
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.sql.Wrapper;
import java.util.Date;

import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;


@Service
public class EmailCodeServiceImpl extends ServiceImpl<EmailCodeMapper, EmailCode> implements EmailCodeService {

    @Autowired
    private EmailCodeMapper emailCodeMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private WebConfig webConfig;

    private static final Logger logger =
            LoggerFactory.getLogger(EmailCodeServiceImpl.class);

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void sendEmailCode(String email, Integer type) {
        if(type== 0) {
            QueryWrapper<UserInfo> emailEqu = new QueryWrapper<UserInfo>().eq("email", email);
            UserInfo userInfo = userInfoMapper.selectOne(emailEqu);

            if(userInfo!=null){
                throw new BusinessException("邮箱已经存在");
            }

        }
        String code = StringTools
                .getRandomString(Constants.LENGTH_5);

        sendEmailCodeDo(email,code);
        // 将未使用的置为已使用
        emailCodeMapper.disableEmailCode(email);

        EmailCode emailCode = new EmailCode();

        emailCode.setCode(code);
        emailCode.setEmail(email);
        emailCode.setStatus(Constants.ZERO );
        //@TODO
        emailCodeMapper.insert(emailCode);

    }

    @Override
    public void checkCode(String email, String emailCode) {

        QueryWrapper<EmailCode> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email",email);
        queryWrapper.eq("emailCode",emailCode);
        EmailCode dbInfo = emailCodeMapper.selectOne(queryWrapper);

        if(dbInfo != null){
          throw new BusinessException("邮箱验证码不正确");
        }

        if(dbInfo.getStatus() != Constants.ZERO || System.currentTimeMillis()-dbInfo.getCreate_time().getTime() > 1000L *60*Constants.LENGTH_5){
            throw new BusinessException("邮箱验证码已失效");
        }

        emailCodeMapper.disableEmailCode(email);
    }

    /**
     * 向用户发送密码
     * */
    private void sendEmailCodeDo(String toEmail,String code) {
//       MinMes
        try{
             MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true);
          // 邮件发送人
        helper.setFrom(webConfig.getSendUsername());
            // 邮件收件人
            helper.setTo(toEmail);
            helper.setSubject("注册邮箱验证码");
            helper.setText("邮箱验证码为: "+code);
            helper.setSentDate(new Date());
            javaMailSender.send(message);


        }catch (MessagingException e) {

            logger.error("邮件发送失败!!!",e);
            throw new BusinessException("邮件发送失败");
        }

    }


}
