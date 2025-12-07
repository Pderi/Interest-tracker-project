package com.interest.tracker.module.interest.constants;

import com.interest.tracker.framework.common.exception.ErrorCode;

/**
 * Interest 模块错误码
 * 使用 1-001-000-000 错误码段
 */
public interface ErrorCodeConstants {

    /** ==================== 摄影功能 1-001-001-000 ==================== */
    ErrorCode PHOTO_NOT_EXISTS = new ErrorCode(1_001_001_001, "照片不存在");
    ErrorCode PHOTO_ALBUM_NOT_EXISTS = new ErrorCode(1_001_001_002, "相册不存在");

    /** ==================== 影视功能 1-001-002-000 ==================== */
    ErrorCode MOVIE_NOT_EXISTS = new ErrorCode(1_001_002_001, "影视不存在");
    ErrorCode MOVIE_RECORD_NOT_EXISTS = new ErrorCode(1_001_002_002, "观看记录不存在");

    /** ==================== 音乐功能 1-001-003-000 ==================== */
    ErrorCode SONG_NOT_EXISTS = new ErrorCode(1_001_003_001, "歌曲不存在");
    ErrorCode PLAYLIST_NOT_EXISTS = new ErrorCode(1_001_003_002, "歌单不存在");

    /** ==================== 球赛功能 1-001-004-000 ==================== */
    ErrorCode TEAM_NOT_EXISTS = new ErrorCode(1_001_004_001, "球队不存在");
    ErrorCode MATCH_RECORD_NOT_EXISTS = new ErrorCode(1_001_004_002, "比赛记录不存在");

}

