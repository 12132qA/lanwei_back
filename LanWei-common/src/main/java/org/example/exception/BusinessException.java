package org.example.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.example.enums.ResponseCodeEnum;
import org.example.vo.ResponseVO;


@Data
@ToString
@NoArgsConstructor
public class BusinessException extends RuntimeException{


    private Integer code;

    private ResponseCodeEnum codeEnum;

    private String message;

    public BusinessException(Integer code, ResponseCodeEnum codeEnum, String message) {
        this.code = code;
        this.codeEnum = codeEnum;
        this.message = message;
    }
    public BusinessException(ResponseCodeEnum codeEnum) {

        this.codeEnum = codeEnum;

    }
    public BusinessException(String message, Integer code, ResponseCodeEnum codeEnum, String message1) {
        super(message);
        this.code = code;
        this.codeEnum = codeEnum;
        this.message = message1;
    }

    public BusinessException(String message, Throwable cause, Integer code, ResponseCodeEnum codeEnum, String message1) {
        super(message, cause);
        this.code = code;
        this.codeEnum = codeEnum;
        this.message = message1;
    }
    public BusinessException( String message) {

        this.message = message;
    }


    public BusinessException(Throwable cause, Integer code, ResponseCodeEnum codeEnum, String message) {
        super(cause);
        this.code = code;
        this.codeEnum = codeEnum;
        this.message = message;
    }

    public BusinessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Integer code, ResponseCodeEnum codeEnum, String message1) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
        this.codeEnum = codeEnum;
        this.message = message1;
    }


    public BusinessException(Integer code) {
        this.code = code;
    }
}
