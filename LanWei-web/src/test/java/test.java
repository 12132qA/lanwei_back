import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.example.LanWeiWebApplication;
import org.example.Mappers.UserInfoMapper;
import org.example.Mappers.UserMessageMapper;
import org.example.pojo.UserInfo;
import org.example.pojo.UserMessage;
import org.example.service.UserMessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@SpringBootTest(classes = LanWeiWebApplication.class)
public class test {

     @Autowired
     private UserInfoMapper userInfoMapper ;
//     private String icons;
     @Autowired
     private UserMessageMapper userMessageMapper;
    @Autowired
    private UserMessageService userMessageService;
      @Test
      void ContextLoad(){
          QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
          queryWrapper.eq("email","test@qq.com");
          List<UserInfo> userInfos = userInfoMapper.selectList(queryWrapper);
          System.out.println("result:");
          System.out.println(userInfos.get(0));
     }

     @Test
    void UserMessageContextLoad(){
         Map<String,String> ans = new HashMap<>();
//.select("count(*)")
         String userId = "1890524956";
         QueryWrapper<UserMessage> uq =  new QueryWrapper<UserMessage>()

                 .eq("received_user_id", userId)
                 .eq("status", 1)
                 .groupBy("message_type");

//                uq.select("count(*)");
//                uq.eq("received_user_id", userId);
//                uq.eq("status",1);
//                uq.groupBy("message_type");
//                uq.having("count(*)>=1");
//                 uq.eq("status", 1);
//                 uq.groupBy("message_type");
//                 uq.having("count(*)>=1");   List<Map<String, Object>> maps

         List<UserMessage> userMessages = userMessageMapper.selectList(uq);
         System.out.println("------------------------------");
//         System.out.println(maps.get(0).get("count(*)"));
//         maps.forEach(System.out::println);
         System.out.println(userMessages.get(0));
//         System.out.println(maps.size());
         System.out.println("------------------------------");
     }


}
