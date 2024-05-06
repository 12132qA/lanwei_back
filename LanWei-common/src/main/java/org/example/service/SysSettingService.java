package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.pojo.SysSetting;
import org.springframework.stereotype.Service;


public interface SysSettingService extends IService<SysSetting> {
    void refresCache();
}
