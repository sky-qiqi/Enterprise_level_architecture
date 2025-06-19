package com.project.travel.interceptor;

import com.project.travel.constant.Constants;
import com.project.travel.domain.User;
import com.project.travel.service.UserService;
import com.project.travel.utils.JwtUtil;
import com.project.travel.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 打印所有header
        log.info("----拦截器收到的header----");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            log.info("{}: {}", name, request.getHeader(name));
        }
        
        //是否登录
        boolean isAuthenticated = false;
        // 请求头带上令牌 x_access_token :Bearer token
        String userId = JwtUtil.getUserIdByToken(request);
        log.info("从token中获取的userId: {}", userId);
        
        if(userId != null) {
            String storedToken = redisUtils.get(Constants.PREFIX_USER_TOKEN + userId);
            log.info("从Redis获取的token: {}", storedToken);
            
            if(null != storedToken){
                User user = userService.getById(userId);
                if(user != null) {
                    //解析token
                    try {
                        //校验token有效性
                        if (JwtUtil.verify(storedToken, userId, user.getPassword())) {
                            isAuthenticated = true;
                            log.info("token验证通过");
                        } else {
                            log.warn("token验证失败");
                        }
                    }catch (Exception e){
                        log.error("token验证异常：", e);
                    }
                } else {
                    log.warn("用户不存在: {}", userId);
                }
            } else {
                log.warn("Redis中未找到token");
            }
        } else {
            log.warn("请求头中未找到token或token解析失败");
        }
        
        if(!isAuthenticated){
            //未登录，则响应信息
            response.setContentType("application/json;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(1011);
            response.getWriter().write("登录已过期或未登录，请重新登录");
            log.warn("请求被拦截：未通过token验证");
        }
        //不放行
        return isAuthenticated;
    }

}
