package org.example.vo;


import java.util.Map;

public class ResponseVO<T> {
    private String status;
    private Integer code;
    private String info;
    private T data;

    public ResponseVO(T data, Map<String, Object> stringObjectMap) {
        this.data = data;
    }
    public ResponseVO(T data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static ResponseVO getSuccessResponseVO (Object e ){

      return  new ResponseVO(e);
    }

    public static ResponseVO getSuccess(){

        return null;
    }

}