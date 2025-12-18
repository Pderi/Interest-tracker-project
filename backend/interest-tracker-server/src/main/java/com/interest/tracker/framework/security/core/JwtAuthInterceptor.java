package com.interest.tracker.framework.security.core;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.interest.tracker.framework.security.jwt.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 认证拦截器
 *
 * 从请求头中解析 Token，验证后将 userId 写入 UserContext
 */
public class JwtAuthInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;

    public JwtAuthInterceptor(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            DecodedJWT decodedJWT = jwtUtils.parseToken(token);
            String subject = decodedJWT.getSubject();
            if (subject != null) {
                try {
                    Long userId = Long.parseLong(subject);
                    UserContext.setUserId(userId);
                } catch (NumberFormatException ignored) {
                    // 解析失败时，由 JwtUtils.parseToken 抛出的异常已处理
                }
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }

}


