package com.interest.tracker.module.travel.constants;

import com.interest.tracker.framework.common.exception.ErrorCode;

/**
 * 旅游模块错误码
 * 使用 1-002-000-000 错误码段
 *
 * @author interest-tracker
 */
public interface TravelErrorCodeConstants {

    /** ==================== 旅游地点相关 1-002-005-000 ==================== */
    ErrorCode TRAVEL_PLACE_NOT_EXISTS = new ErrorCode(1_002_005_001, "旅游地点不存在");
    ErrorCode TRAVEL_PLACE_ALREADY_EXISTS = new ErrorCode(1_002_005_002, "该旅游地点已存在，请勿重复添加");

    /** ==================== 旅游记录相关 1-002-005-100 ==================== */
    ErrorCode TRAVEL_RECORD_NOT_EXISTS = new ErrorCode(1_002_005_101, "旅游记录不存在");
    ErrorCode TRAVEL_RECORD_NOT_OWNER = new ErrorCode(1_002_005_102, "无权操作该旅游记录");
    ErrorCode TRAVEL_RECORD_ALREADY_EXISTS = new ErrorCode(1_002_005_103, "该旅游地点已添加，请勿重复添加");

}

