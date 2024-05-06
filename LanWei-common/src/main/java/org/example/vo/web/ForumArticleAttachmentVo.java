package org.example.vo.web;

import lombok.Data;

/**
 * 详情页ForumArticleDetailVO
 *
 * */
@Data
public class ForumArticleAttachmentVo {
    /**
     * 文件ID
     * */
    private String firstId;

    /**
     *文件大小
     * */
    private long fileSize;

    /**
     * 文件名称
     * */
    private String fileName;

    /**
     * 下载次数
     * */
    private Integer downloadCount;

    /**
     * 文件类型
     * */
    private Integer fileType;

    /**
     *
     * */
    private Integer integral;


}
