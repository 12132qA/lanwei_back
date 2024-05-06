package org.example.pojo;


import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@TableName("user_message")
public class UserMessage implements Serializable {

    String messageId;

    String receivedUserId;

    String articleId;

    String articleTitle;

    Integer commentId;

    String sendNickName;

    @TableField("send_user_id")
    String sendUserId;

    Integer messageType;

    String messageContent;

    Integer status;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:SS")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;
}
