package com.interest.tracker.module.book.constants;

import com.interest.tracker.framework.common.exception.ErrorCode;

/**
 * 图书模块错误码
 * 使用 1-002-000-000 错误码段
 *
 * @author interest-tracker
 */
public interface BookErrorCodeConstants {

    /** ==================== 书籍相关 1-002-001-000 ==================== */
    ErrorCode BOOK_NOT_EXISTS = new ErrorCode(1_002_001_001, "书籍不存在");
    ErrorCode BOOK_TITLE_DUPLICATE = new ErrorCode(1_002_001_002, "书籍标题已存在");

    /** ==================== 阅读记录相关 1-002-002-000 ==================== */
    ErrorCode BOOK_RECORD_NOT_EXISTS = new ErrorCode(1_002_002_001, "阅读记录不存在");
    ErrorCode BOOK_RECORD_NOT_OWNER = new ErrorCode(1_002_002_002, "无权操作该阅读记录");
    ErrorCode BOOK_RECORD_ALREADY_EXISTS = new ErrorCode(1_002_002_003, "该书籍已添加，请勿重复添加");

}

