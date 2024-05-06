package org.example.vo.web;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 文章信息
 * */
@ToString
@Data
public class ForumArticleVO implements Serializable {

    /**
     * 文章ID
     * */
    private String articleId;

    /**
     * 板块ID
     * */
    private Integer boardId;

    /**
     * 板块名称
     * */
    private String boardName;

    /**
     * 父板块ID
     * */
    private Integer pBoardId;

    /**
     * 父板块名称
     * */
    private Integer pBoardName;

    /**
     * 用户ID
     * */
    private String userId;

    /**
     * 昵称
     * */
    private String nickName;

   /**
    * 最后登录ip地址
    * */
   private String userIpAddress;

   /**
    * 标题
    * */
   private String title;

   /**
    * 0 富文本编辑器
    * 1 markdown编辑器
    * */
   private Integer editorType;

   /**
    * 封面
    * */
   private String cover;

   /**
    * 内容
    * */
   private String content;

   /**
    * 摘要
    * */
   private String summary;

   /**
    * 发布时间
    * */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
   private Date postTime;

  /**
   * 阅读数量
   * */
  private Integer readCount;

  /**
   * 点赞数
   * */
  private Integer goodCount;

  /**
   * 评论数
   * */
  private Integer commentCount;

  /**
   * 0 未置顶 1 已置顶
   * */
  private Integer topType;






}
