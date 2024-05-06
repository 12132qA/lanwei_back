package org.example.dto;

public class SysSetting4LikeDto {
    /**
     * 点赞数量阈值
     * */

    private Integer likeDayCountThreshold;

    public SysSetting4LikeDto(Integer likeDayCountThreshold) {
        this.likeDayCountThreshold = likeDayCountThreshold;
    }

    public Integer getLikeDayCountThreshold() {
        return likeDayCountThreshold;
    }

    public void setLikeDayCountThreshold(Integer likeDayCountThreshold) {
        this.likeDayCountThreshold = likeDayCountThreshold;
    }
}
