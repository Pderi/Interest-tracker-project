package com.interest.tracker.module.movie.constants;

import com.interest.tracker.framework.common.exception.ErrorCode;

/**
 * 影视模块错误码
 * 使用 1-001-000-000 错误码段
 *
 * @author interest-tracker
 */
public interface MovieErrorCodeConstants {

    /** ==================== 影视相关 1-001-001-000 ==================== */
    ErrorCode MOVIE_NOT_EXISTS = new ErrorCode(1_001_001_001, "影视不存在");
    ErrorCode MOVIE_TITLE_DUPLICATE = new ErrorCode(1_001_001_002, "影视标题已存在");

    /** ==================== 观看记录相关 1-001-002-000 ==================== */
    ErrorCode MOVIE_RECORD_NOT_EXISTS = new ErrorCode(1_001_002_001, "观看记录不存在");
    ErrorCode MOVIE_RECORD_NOT_OWNER = new ErrorCode(1_001_002_002, "无权操作该观看记录");
    ErrorCode MOVIE_RECORD_ALREADY_EXISTS = new ErrorCode(1_001_002_003, "该影视已添加，请勿重复添加");

    /** ==================== TMDB相关 1-001-003-000 ==================== */
    ErrorCode TMDB_API_ERROR = new ErrorCode(1_001_003_001, "TMDB API调用失败");
    ErrorCode TMDB_MOVIE_NOT_FOUND = new ErrorCode(1_001_003_002, "TMDB中未找到该影视");

}

