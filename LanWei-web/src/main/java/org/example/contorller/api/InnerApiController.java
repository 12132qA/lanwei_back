package org.example.contorller.api;

import lombok.extern.slf4j.Slf4j;
import org.example.Utils.StringTools;
import org.example.annotation.VerifyParam;
import org.example.config.WebConfig;
import org.example.enums.ResponseCodeEnum;
import org.example.exception.BusinessException;
import org.example.service.SysSettingService;
import org.example.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/innerApi")
@RestController("innerApiController")
public class InnerApiController {
    @Autowired
    private WebConfig webConfig;
    @Autowired
    private SysSettingService sysSettingService;

    /**
     *
     * */
    @RequestMapping("/refresSysSetting")
    public ResponseVO refersSysSetting(@VerifyParam(required = true) String appKey,
                                      @VerifyParam(required = true) Long timeStamp,
                                      @VerifyParam(required = true) String sign){

        if(!webConfig.getInnerApiAppKey().equals(appKey)){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        /**
         *  System.currentTimeMillis  缓存 时间
         * */
        if(System.currentTimeMillis()-timeStamp>1000*1000){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        String mySign = StringTools.encodeM5(appKey+timeStamp+webConfig.getInnerApiAppSecret());

        if(!mySign.equals(sign)){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        sysSettingService.refresCache();

        return ResponseVO.getSuccessResponseVO(null);
    }

}
