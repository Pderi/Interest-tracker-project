package com.interest.tracker.framework.security.config;

import com.interest.tracker.framework.security.core.JwtAuthInterceptor;
import com.interest.tracker.framework.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 安全相关配置（JWT 拦截器等）
 */
@Configuration
public class SecurityConfig implements WebMvcConfigurer {

    @Value("${jwt.secret:interest-tracker-default-secret}")
    private String jwtSecret;

    @Value("${jwt.expire-seconds:604800}") // 默认 7 天
    private long jwtExpireSeconds;

    @Bean
    public JwtUtils jwtUtils() {
        return new JwtUtils(jwtSecret, jwtExpireSeconds);
    }

    @Bean
    public JwtAuthInterceptor jwtAuthInterceptor(JwtUtils jwtUtils) {
        return new JwtAuthInterceptor(jwtUtils);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthInterceptor(jwtUtils()))
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/test/**",
                        "/api/user/login",
                        "/api/user/register",
                        "/v3/api-docs/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**"
                );
    }

}


