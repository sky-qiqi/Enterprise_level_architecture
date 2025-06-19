package com.project.travel.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.travel.enums.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {

    private boolean success;
    private String message;
    private Integer code;
    private Object data;

    public ApiResponse(ResultCode code) {
        this.success = code.isSuccess();
        this.code = code.getCode();
        this.message = code.getMessage();
    }

    public static ApiResponse success() {
        return new ApiResponse(ResultCode.COMMON_SUCCESS);
    }

    public static ApiResponse success(Object data) {
        return new ApiResponse(ResultCode.COMMON_SUCCESS, data);
    }

    public static ApiResponse success(String msg) {
        return new ApiResponse(ResultCode.COMMON_SUCCESS, msg);
    }

    public static ApiResponse alert(ResultCode code) {
        return new ApiResponse(code);
    }

    public static ApiResponse alert(ResultCode code, String message) {
        return new ApiResponse(code, message);
    }

    public static ApiResponse alert(ResultCode code, Exception e) {
        log.error(e.getMessage(), e);
        return new ApiResponse(code);
    }

    public static ApiResponse alert(Integer code, String message) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(code);
        apiResponse.setMessage(message);
        return apiResponse;
    }


    public static ApiResponse fail() {
        return new ApiResponse(ResultCode.COMMON_FAIL);
    }

    public static ApiResponse fail(String msg) {
        return new ApiResponse(ResultCode.COMMON_FAIL, msg);
    }

    public static ApiResponse fail(String msg, Exception e) {
        log.error(e.getMessage(), e);
        return new ApiResponse(ResultCode.COMMON_FAIL, msg);
    }


    /**
     * 统一返回码，信息自定义
     *
     * @param code
     * @param message
     */
    public ApiResponse(ResultCode code, String message) {
        this.success = code.isSuccess();
        this.code = code.getCode();
        this.message = message;
    }

    public ApiResponse(ResultCode code, Object object) {
        this.success = code.isSuccess();
        this.code = code.getCode();
        this.message = code.getMessage();
        this.data = object;
    }

}
