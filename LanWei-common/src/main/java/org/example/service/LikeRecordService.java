package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.enums.OperaRecordOpTypeEnum;
import org.example.pojo.LikeRecord;
import org.springframework.stereotype.Service;

import javax.management.openmbean.OpenDataException;


public interface LikeRecordService extends IService<LikeRecord> {

    void doLike(String objectId, String userId, String nickName, OperaRecordOpTypeEnum opTypeEnum);

    LikeRecord getLikeRecordByObjectIdAndUserIdAndOpType(String objectId, String userId, Integer type);
}
