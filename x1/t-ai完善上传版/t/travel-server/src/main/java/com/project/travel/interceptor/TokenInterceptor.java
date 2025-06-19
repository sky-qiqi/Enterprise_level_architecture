package com.project.travel.interceptor;

import com.project.travel.constant.Constants;
import com.project.travel.domain.User;
import com.project.travel.service.UserService;
import com.project.travel.utils.JwtUtil;
import com.project.travel.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //是否登录
        boolean isAuthenticated=false;
        // 请求头带上令牌 x_access_token :Bearer token
        String userId = JwtUtil.getUserIdByToken(request);
        String storedToken = redisUtils.get(Constants.PREFIX_USER_TOKEN + userId);
        if(null != storedToken){
            User user = userService.getById(userId);
            //解析token
            try {
                //校验token有效性
                if (JwtUtil.verify(storedToken, userId, user.getPassword())) {
                    isAuthenticated = true;
                }
            }catch (Exception e){
                System.out.println("解码异常为》》》》"+e);
            }
        }
        if(!isAuthenticated){
            //未登录，则响应信息
            response.setContentType("application/json;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(1011);
            response.getWriter().write("登录已过期或未登录，请重新登录");
        }
        //不放行
        return isAuthenticated;
    }

}
