package com.interest.tracker.module.concert.constants;

import com.interest.tracker.framework.common.exception.ErrorCode;

/**
 * 演唱会模块错误码
 * 使用 1-002-000-000 错误码段
 *
 * @author interest-tracker
 */
public interface ConcertErrorCodeConstants {

    /** ==================== 演唱会相关 1-002-004-000 ==================== */
    ErrorCode CONCERT_NOT_EXISTS = new ErrorCode(1_002_004_001, "演唱会不存在");
    ErrorCode CONCERT_ALREADY_EXISTS = new ErrorCode(1_002_004_002, "该演唱会已存在，请勿重复添加");

    /** ==================== 观演记录相关 1-002-004-100 ==================== */
    ErrorCode CONCERT_RECORD_NOT_EXISTS = new ErrorCode(1_002_004_101, "观演记录不存在");
    ErrorCode CONCERT_RECORD_NOT_OWNER = new ErrorCode(1_002_004_102, "无权操作该观演记录");
    ErrorCode CONCERT_RECORD_ALREADY_EXISTS = new ErrorCode(1_002_004_103, "该演唱会已添加，请勿重复添加");

}

