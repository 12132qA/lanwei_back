package org.example.contorller;

import lombok.extern.slf4j.Slf4j;

import org.example.enums.ResponseCodeEnum;
import org.example.exception.BusinessException;
import org.example.vo.ResponseVO;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody ;import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest ;

@Slf4j
@ControllerAdvice
@RestController /***/
public class AGlobalExceptionHandle {

    @ResponseBody
    @ExceptionHandler(BusinessException.class)
    public Object handlerException(BusinessException e, HttpServletRequest request) {
        log.info("-----------内部 错误 :" + e.getMessage());
        //
        ResponseVO<String> server_error = new ResponseVO<>(e.getMessage());
        //
        server_error.setCode(ResponseCodeEnum.CODE_500.getCode());
        return server_error;
    }

}