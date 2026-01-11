package com.interest.tracker.module.photo.constants;

import com.interest.tracker.framework.common.exception.ErrorCode;

/**
 * 摄影模块错误码
 * 使用 1-002-000-000 错误码段
 *
 * @author interest-tracker
 */
public interface PhotoErrorCodeConstants {

    /** ==================== 照片相关 1-002-001-000 ==================== */
    ErrorCode PHOTO_NOT_EXISTS = new ErrorCode(1_002_001_001, "照片不存在");
    ErrorCode PHOTO_NOT_OWNER = new ErrorCode(1_002_001_002, "无权操作该照片");
    ErrorCode PHOTO_FILE_TOO_LARGE = new ErrorCode(1_002_001_003, "照片文件过大");
    ErrorCode PHOTO_FILE_TYPE_INVALID = new ErrorCode(1_002_001_004, "照片文件类型不支持");
    ErrorCode PHOTO_UPLOAD_FAILED = new ErrorCode(1_002_001_005, "照片上传失败");

    /** ==================== 分类相关 1-002-002-000 ==================== */
    ErrorCode PHOTO_CATEGORY_NOT_EXISTS = new ErrorCode(1_002_002_001, "照片分类不存在");
    ErrorCode PHOTO_CATEGORY_NOT_OWNER = new ErrorCode(1_002_002_002, "无权操作该分类");
    ErrorCode PHOTO_CATEGORY_NAME_EXISTS = new ErrorCode(1_002_002_003, "分类名称已存在");
    ErrorCode PHOTO_CATEGORY_LIMIT_EXCEEDED = new ErrorCode(1_002_002_004, "分类数量已达上限");
    ErrorCode PHOTO_CATEGORY_HAS_PHOTOS = new ErrorCode(1_002_002_005, "该分类下还有照片，无法删除");

    /** ==================== 相册相关 1-002-003-000 ==================== */
    ErrorCode PHOTO_ALBUM_NOT_EXISTS = new ErrorCode(1_002_003_001, "相册不存在");
    ErrorCode PHOTO_ALBUM_NOT_OWNER = new ErrorCode(1_002_003_002, "无权操作该相册");
    ErrorCode PHOTO_ALBUM_NAME_EXISTS = new ErrorCode(1_002_003_003, "相册名称已存在");

}

