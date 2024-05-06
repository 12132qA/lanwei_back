package org.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.Mappers.SysSettingMapper;
import org.example.Utils.JsonUtils;
import org.example.Utils.StringTools;
import org.example.Utils.SysCacheUtils;
import org.example.dto.SysSetting4AuditDto;
import org.example.dto.SysSetting4CommentDto;
import org.example.dto.SysSettingDto;
import org.example.enums.SysSettingCodeEnum;
import org.example.pojo.SysSetting;
import org.example.service.SysSettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.List;


@Service
public class SysSettingServiceImpl extends ServiceImpl<SysSettingMapper, SysSetting> implements SysSettingService {

    private static final Logger logger = LoggerFactory.getLogger(SysSettingServiceImpl.class);

    @Autowired
    private SysSettingMapper sysSettingMapper;

    @Override
    public void refresCache() {
//        System.out.println("xxx");

        try{

            SysSettingDto sysSettingDto = new SysSettingDto();
            List<SysSetting> list =  sysSettingMapper.selectList(null);
            for(SysSetting sysSetting: list){
                String jsonContent =  sysSetting.getJson_content();
                if(StringTools.isEmpty(jsonContent)){
                   continue;
                }

                String code = sysSetting.getCode();

                SysSettingCodeEnum sysSettingCodeEnum = SysSettingCodeEnum.getByCode(code);
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(sysSettingCodeEnum.getPropName(), SysSettingDto.class);
                Method method =  propertyDescriptor.getReadMethod();

                Class subClass = Class.forName(sysSettingCodeEnum.getClassz());

                // 反射

                method.invoke(sysSettingDto,JsonUtils.convertJson2Obj(jsonContent,subClass));



//                if(sysSetting.getCode().equals("audit")){
//                   SysSetting4AuditDto auditDto = JsonUtils.convertJson2Obj(sysSetting.getJson_content(),SysSetting4AuditDto.class);
//                   logger.info(auditDto.getPostAudit()+ "");
//                  sysSettingDto.setAuditDto(auditDto);
//                }else if(sysSetting.getCode().equals("comment")){
//                    SysSetting4CommentDto commentDto =
//                            JsonUtils.convertJson2Obj(sysSetting.getJson_content(),SysSetting4CommentDto.class);
//                    sysSettingDto.setCommentDto(commentDto);
//                }


            }
            logger.info(JsonUtils.convertObj2Json(sysSettingDto));

            SysCacheUtils.refresh(sysSettingDto);
        }catch (Exception e){
                logger.error("刷新缓存失败",e);
        }


    }
}
