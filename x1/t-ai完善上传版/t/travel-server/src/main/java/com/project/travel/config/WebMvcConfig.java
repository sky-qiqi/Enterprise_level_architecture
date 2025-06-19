package com.project.travel.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射 /img/** 请求到 classpath:/static/assets/img/ 目录
        // 假设前端打包后的图片在 dist/assets/img/ 目录下，
        // 经过 maven-resources-plugin 复制后，在后端 JAR 包内会位于 static/assets/img/
        registry.addResourceHandler("/img/**")
                .addResourceLocations("classpath:/static/assets/img/");

        // 默认的静态资源处理，确保其他静态文件（如 index.html, css, js）也能访问
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }
}