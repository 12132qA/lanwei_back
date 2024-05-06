package org.example.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserMessageCountDto {

    Long total;

    Long sys;

    Long reply;

    Long likePost;

    Long likeComment;

    Long downloadAttachment;

}
