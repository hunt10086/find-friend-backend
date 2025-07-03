package com.dying.common;

import lombok.Data;

@Data
public class BaseResponse<T> {
    private int code;

    private T data;

    private String message;

    private String description;

    BaseResponse(int code, T data, String message,String description)  {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }
    BaseResponse(int code, T data,String message) {
        this(code,data,message,"");
    }
    BaseResponse(int code, T data) {
        this(code,data,"","");
    }

    BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(),null,errorCode.getMessage(),errorCode.getDescription());
    }

}
