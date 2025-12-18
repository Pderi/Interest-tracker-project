package com.interest.tracker.framework.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.interest.tracker.framework.common.exception.ServiceException;
import com.interest.tracker.framework.common.exception.util.ServiceExceptionUtil;
import com.interest.tracker.module.user.constants.UserErrorCodeConstants;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * JWT 工具类
 */
public class JwtUtils {

    private final String secret;
    private final long expireSeconds;

    public JwtUtils(String secret, long expireSeconds) {
        this.secret = secret;
        this.expireSeconds = expireSeconds;
    }

    /**
     * 生成 Token
     */
    public String generateToken(Long userId, String username) {
        Instant now = Instant.now();
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withClaim("username", username)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plus(expireSeconds, ChronoUnit.SECONDS)))
                .sign(algorithm);
    }

    /**
     * 解析 Token
     */
    public DecodedJWT parseToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm).build();
            return verifier.verify(token);
        } catch (com.auth0.jwt.exceptions.TokenExpiredException ex) {
            throw buildAuthException(UserErrorCodeConstants.AUTH_TOKEN_EXPIRED);
        } catch (Exception ex) {
            throw buildAuthException(UserErrorCodeConstants.AUTH_TOKEN_INVALID);
        }
    }

    public Long getUserId(String token) {
        DecodedJWT jwt = parseToken(token);
        String sub = jwt.getSubject();
        try {
            return Long.parseLong(sub);
        } catch (NumberFormatException ex) {
            throw buildAuthException(UserErrorCodeConstants.AUTH_TOKEN_INVALID);
        }
    }

    private ServiceException buildAuthException(com.interest.tracker.framework.common.exception.ErrorCode errorCode) {
        return ServiceExceptionUtil.exception(errorCode);
    }

}


