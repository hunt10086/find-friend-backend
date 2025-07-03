package com.dying.exception;

import com.dying.common.BaseResponse;
import com.dying.common.ErrorCode;
import com.dying.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse handleBusinessException(BusinessException e) {
        log.error("BusinessException"+e.getMessage(), e);
        return ResultUtils.error(e.getCode(),e.getMessage(),"");
    }


    @ExceptionHandler(RuntimeException.class)
    public BaseResponse handleRuntimeException(RuntimeException e) {
        log.error("RuntimeException", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR,e.getMessage(),"");
    }
}
