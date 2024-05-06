package org.example.dto;

/**
 * 发帖设置
 * */
public class SysSetting4PostDto {
   /*
   * 一天发帖数量
   * */
    private Integer postIntegral;

    /**
     *  每天上传图片数量
     *
     * */
    private Integer postDayCountThread;

    private Integer dayImageUploadCount;

    /**
     *
     * 附件大小  单位 mb
     * */

    private Integer attachmentSize;

    public Integer getPostIntegral() {
        return postIntegral;
    }

    public void setPostIntegral(Integer postIntegral) {
        this.postIntegral = postIntegral;
    }

    public Integer getPostDayCountThread() {
        return postDayCountThread;
    }

    public void setPostDayCountThread(Integer postDayCountThread) {
        this.postDayCountThread = postDayCountThread;
    }

    public Integer getDayImageUploadCount() {
        return dayImageUploadCount;
    }

    public void setDayImageUploadCount(Integer dayImageUploadCount) {
        this.dayImageUploadCount = dayImageUploadCount;
    }

    public Integer getAttachmentSize() {
        return attachmentSize;
    }

    public void setAttachmentSize(Integer attachmentSize) {
        this.attachmentSize = attachmentSize;
    }
}
