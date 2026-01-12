package com.interest.tracker.module.concert.service;

import com.interest.tracker.module.concert.controller.app.vo.*;

/**
 * 演唱会服务接口
 *
 * @author interest-tracker
 */
public interface ConcertService {

    /**
     * 创建演唱会记录（手动）
     *
     * @param reqVO 创建请求
     * @return 创建的演唱会ID和记录ID
     */
    ConcertCreateRespVO createConcert(ConcertCreateReqVO reqVO);

    /**
     * 更新观演记录
     *
     * @param reqVO 更新请求
     */
    void updateConcertRecord(ConcertRecordUpdateReqVO reqVO);

    /**
     * 获取演唱会详情
     *
     * @param id 演唱会ID
     * @return 演唱会详情
     */
    ConcertRespVO getConcert(Long id);

    /**
     * 获取演唱会分页列表（包含统计信息）
     *
     * @param reqVO 分页请求
     * @return 分页结果和统计信息
     */
    ConcertPageWithStatsRespVO getConcertPage(ConcertPageReqVO reqVO);

    /**
     * 删除观演记录
     *
     * @param id 记录ID
     */
    void deleteConcertRecord(Long id);

    /**
     * 更新演唱会信息
     *
     * @param reqVO 更新请求
     */
    void updateConcert(ConcertUpdateReqVO reqVO);

}

