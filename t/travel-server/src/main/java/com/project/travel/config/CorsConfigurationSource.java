package com.project.travel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfigurationSource {

    /**
     * @description: 配置跨域
     * @return: CorsFilter
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // Allow credentials (e.g., cookies, authorization headers)
        corsConfiguration.setAllowCredentials(true);

        // Allow all origins (reverted to wildcard for simplicity)
        corsConfiguration.addAllowedOriginPattern("*");

        // Allow all headers
        corsConfiguration.addAllowedHeader("*");

        // Allow all methods
        corsConfiguration.addAllowedMethod("*");

        // Pre-flight request cache time (seconds)
        corsConfiguration.setMaxAge(1800L);

        // Apply CORS configuration to all endpoints
        source.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(source);
    }
}