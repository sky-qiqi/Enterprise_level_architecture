package com.project.travel.utils;

import com.project.travel.constant.Constants;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class TokenUtils {

    /**
    * 解析token获取userId
    */
    public static String getUserIdByToken() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String token = request.getHeader(Constants.X_ACCESS_TOKEN);
        return JwtUtil.getUserId(token);
    }

}
