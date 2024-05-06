package org.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.Mappers.UserIntegerRecordMapper;
import org.example.pojo.UserIntegerRecord;
import org.example.service.UserInfoService;
import org.example.service.UserIntegerRecordService;
import org.springframework.stereotype.Service;

@Service
public class UserIntegerRecordServiceImpl extends ServiceImpl<UserIntegerRecordMapper, UserIntegerRecord> implements UserIntegerRecordService {
}
