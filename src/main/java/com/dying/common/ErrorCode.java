package com.dying.common;

public enum ErrorCode {

    PARAMS_ERROR(4000,"请求数据错误",""),
    NULL_ERROR(4001,"请求值为空",""),
    NOT_LOGIN(40100,"未登录",""),
    NO_AUTO(401001,"无权限",""),
    SYSTEM_ERROR(50000,"系统内部异常","");
    



    private int code;
    private String message;
    private String description;


    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }


}
