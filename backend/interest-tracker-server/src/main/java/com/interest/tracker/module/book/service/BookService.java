package com.interest.tracker.module.book.service;

import com.interest.tracker.module.book.controller.app.vo.*;

/**
 * 图书服务接口
 *
 * @author interest-tracker
 */
public interface BookService {

    /**
     * 创建图书记录（手动创建）
     *
     * @param reqVO 创建请求
     * @return 创建的书籍ID和记录ID
     */
    BookCreateRespVO createBook(BookCreateReqVO reqVO);

    /**
     * 更新阅读记录
     *
     * @param reqVO 更新请求
     */
    void updateBookRecord(BookRecordUpdateReqVO reqVO);

    /**
     * 获取图书详情
     *
     * @param id 书籍ID
     * @return 图书详情
     */
    BookRespVO getBook(Long id);

    /**
     * 获取图书分页列表（包含统计信息）
     *
     * @param reqVO 分页请求
     * @return 分页结果和统计信息
     */
    BookPageWithStatsRespVO getBookPage(BookPageReqVO reqVO);

    /**
     * 删除阅读记录
     *
     * @param id 记录ID
     */
    void deleteBookRecord(Long id);

}

