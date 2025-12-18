package com.interest.tracker.module.user.constants;

import com.interest.tracker.framework.common.exception.ErrorCode;

/**
 * 用户与认证模块错误码
 * 使用 1-000-001-000 错误码段
 */
public interface UserErrorCodeConstants {

    /** ==================== 用户基础 1-000-001-000 ==================== */
    ErrorCode USER_NOT_EXISTS = new ErrorCode(1_000_001_001, "用户不存在");
    ErrorCode USER_USERNAME_EXISTS = new ErrorCode(1_000_001_002, "用户名已存在");
    ErrorCode USER_EMAIL_EXISTS = new ErrorCode(1_000_001_003, "邮箱已存在");
    ErrorCode USER_PASSWORD_ERROR = new ErrorCode(1_000_001_004, "用户名或密码错误");
    ErrorCode USER_DISABLED = new ErrorCode(1_000_001_005, "用户已被禁用");

    /** ==================== 认证相关 1-000-002-000 ==================== */
    ErrorCode AUTH_TOKEN_INVALID = new ErrorCode(1_000_002_001, "认证信息无效，请重新登录");
    ErrorCode AUTH_TOKEN_EXPIRED = new ErrorCode(1_000_002_002, "登录状态已过期，请重新登录");

}


