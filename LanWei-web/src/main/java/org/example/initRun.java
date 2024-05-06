package org.example;

import lombok.AllArgsConstructor;
import org.example.Mappers.SysSettingMapper;
import org.example.service.SysSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class initRun implements ApplicationRunner {

    @Autowired
    private SysSettingService sysSettingService;
    //  初始化 配置
    @Override
    public void run(ApplicationArguments args) throws Exception {

    }

    

}