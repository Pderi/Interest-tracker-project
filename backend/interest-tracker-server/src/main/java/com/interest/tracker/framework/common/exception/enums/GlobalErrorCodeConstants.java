package com.interest.tracker.framework.common.exception.enums;

import com.interest.tracker.framework.common.exception.ErrorCode;

/**
 * 全局错误码常量
 */
public interface GlobalErrorCodeConstants {

    ErrorCode SUCCESS = new ErrorCode(0, "成功");

    // ========== 客户端错误 400-499 ==========
    ErrorCode BAD_REQUEST = new ErrorCode(400, "请求参数不正确");
    ErrorCode UNAUTHORIZED = new ErrorCode(401, "账号未登录");
    ErrorCode FORBIDDEN = new ErrorCode(403, "没有该操作权限");
    ErrorCode NOT_FOUND = new ErrorCode(404, "请求未找到");
    ErrorCode METHOD_NOT_ALLOWED = new ErrorCode(405, "请求方法不正确");

    // ========== 服务端错误 500-599 ==========
    ErrorCode INTERNAL_SERVER_ERROR = new ErrorCode(500, "系统异常");

    // ========== 自定义错误码段 1xxx ==========
    ErrorCode UNKNOWN = new ErrorCode(1000, "未知错误");

}

