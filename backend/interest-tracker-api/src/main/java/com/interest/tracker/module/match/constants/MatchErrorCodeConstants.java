package com.interest.tracker.module.match.constants;

import com.interest.tracker.framework.common.exception.ErrorCode;

/**
 * 球赛模块错误码
 * 使用 1-003-000-000 错误码段
 *
 * @author interest-tracker
 */
public interface MatchErrorCodeConstants {

    /** ==================== 比赛记录相关 1-003-001-000 ==================== */
    ErrorCode MATCH_RECORD_NOT_EXISTS = new ErrorCode(1_003_001_001, "比赛记录不存在");
    ErrorCode MATCH_RECORD_NOT_OWNER = new ErrorCode(1_003_001_002, "无权操作该比赛记录");
    ErrorCode MATCH_RECORD_TEAM_SAME = new ErrorCode(1_003_001_003, "主队和客队不能相同");

}

