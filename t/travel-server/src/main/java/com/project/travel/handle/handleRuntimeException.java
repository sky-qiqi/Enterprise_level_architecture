package com.project.travel.handle;

import com.project.travel.domain.ApiResponse;
import com.project.travel.enums.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class handleRuntimeException {

    @ExceptionHandler(RuntimeException.class)
    public ApiResponse handleRuntimeException(RuntimeException e) {
        log.error("运行时异常：", e);
        return ApiResponse.alert(ResultCode.COMMON_SERVER_ERROR, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse handleException(Exception e) {
        log.error("系统异常：", e);
        return ApiResponse.alert(ResultCode.COMMON_SERVER_ERROR, e.getMessage());
    }

}
