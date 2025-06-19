package com.project.travel.config;

import com.project.travel.interceptor.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 注册拦截器
 * @author AA
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private TokenInterceptor tokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor)
                // 拦截所有请求
                .addPathPatterns("/**")
                // 排除请求 (including the new AI endpoint)
                .excludePathPatterns("/file/**", "/video/**", "/img/**", "/login", "/common/**", "/user/setUserAvatar/**",
                        "/user/getEmailReg/**", "/user/forgetPassword/**", "/user/saveUser/**", "/ai/**", "/api/chat/**");
    }
}