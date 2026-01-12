package com.interest.tracker.module.travel.service;

import com.interest.tracker.module.travel.controller.app.vo.*;

/**
 * 旅游服务接口
 *
 * @author interest-tracker
 */
public interface TravelService {

    /**
     * 创建旅游记录（手动）
     *
     * @param reqVO 创建请求
     * @return 创建的地点ID和记录ID
     */
    TravelCreateRespVO createTravel(TravelCreateReqVO reqVO);

    /**
     * 更新旅游记录
     *
     * @param reqVO 更新请求
     */
    void updateTravelRecord(TravelRecordUpdateReqVO reqVO);

    /**
     * 获取旅游详情
     *
     * @param id 地点ID
     * @return 旅游详情
     */
    TravelRespVO getTravel(Long id);

    /**
     * 获取旅游分页列表（包含统计信息）
     *
     * @param reqVO 分页请求
     * @return 分页结果和统计信息
     */
    TravelPageWithStatsRespVO getTravelPage(TravelPageReqVO reqVO);

    /**
     * 删除旅游记录
     *
     * @param id 记录ID
     */
    void deleteTravelRecord(Long id);

    /**
     * 更新旅游地点信息
     *
     * @param reqVO 更新请求
     */
    void updateTravelPlace(TravelPlaceUpdateReqVO reqVO);

}

