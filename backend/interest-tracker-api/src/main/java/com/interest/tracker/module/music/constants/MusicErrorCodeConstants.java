package com.interest.tracker.module.music.constants;

import com.interest.tracker.framework.common.exception.ErrorCode;

/**
 * 音乐模块错误码
 * 使用 1-002-000-000 错误码段
 *
 * @author interest-tracker
 */
public interface MusicErrorCodeConstants {

    /** ==================== 专辑相关 1-002-001-000 ==================== */
    ErrorCode ALBUM_NOT_EXISTS = new ErrorCode(1_002_001_001, "专辑不存在");
    ErrorCode ALBUM_ALREADY_EXISTS = new ErrorCode(1_002_001_002, "该专辑已存在，请勿重复添加");

    /** ==================== 听歌记录相关 1-002-002-000 ==================== */
    ErrorCode ALBUM_RECORD_NOT_EXISTS = new ErrorCode(1_002_002_001, "听歌记录不存在");
    ErrorCode ALBUM_RECORD_NOT_OWNER = new ErrorCode(1_002_002_002, "无权操作该听歌记录");
    ErrorCode ALBUM_RECORD_ALREADY_EXISTS = new ErrorCode(1_002_002_003, "该专辑已添加，请勿重复添加");

}

