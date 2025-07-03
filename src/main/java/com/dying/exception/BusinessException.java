package com.dying.exception;

import com.dying.common.ErrorCode;
import lombok.Data;

@Data
public class BusinessException extends RuntimeException {
    private final int code;
    private final String description;

    public BusinessException(ErrorCode errorCode,int code,String description) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }

    public BusinessException(ErrorCode errorCode, String description) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }

}
