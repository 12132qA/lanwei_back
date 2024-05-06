package org.example.enums;

public enum ResponseCodeEnum {

    CODE_200(200,"请求成功"),
    CODE_404(404,"请求地址不能存在"),

    CODE_600(600,"请求参数错误"),
    CODE_601(601,"信息已经存在"),
    CODE_602(602,"信息提交过多"),

    CODE_500(500,"服务器返回错误,请联系管理员"),
    CODE_900(900,"http请求超时"),
    CODE_901(901,"登录超时");

    private Integer code;

    private String msg;

    ResponseCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    //    public static final Integer CODE_200 =200; // 请求成功
//
//    public static final  Integer CODE_600 = 600; // 请求成功
//
//    public static final Integer CODE_601 = 601; // 信息已经 存在
//
//    public static final Integer CODE_500 = 500; // 服务返回错误 请联系管理员
//
//    public static final Integer CODE_900 = 900; // http请求超时
//    public static final Integer CODE_404 = 404; //请求地址不存在
//    public static final Integer CODE_400 = 404; // 请求不存在
//
//    public static final Integer SAVE_OK = 20011;  // 存入数据成功
//    public static final Integer DELETE_OK = 20021; // 删除成功
//    public static final  Integer UPDA_OK = 20031; // 跟新成功
//    public static final Integer GET_OK = 20041; // 获取成功
//
//    public static final Integer SAVE_ERR = 20010;  // 存入数据失败
//    public static final Integer DELETE_ERR = 20020;  //  删除成功
//    public static final  Integer UPDA_ERR = 20030; // 跟新失败
//    public static final Integer GET_ERR = 20040; // 获取失败
//    public static final Integer SYSTEM_ERR = 50001; // 系统异常
//    public static final Integer BUSINESS_ERR = 50002; // 业务异常
//    public static final Integer SYSTEM_TIMEOUT_ERR = 50003; // 系统超时异常
//    public static final Integer SYSTEM_UNKNOW_ERR = 66666;  // 未知错误



}
