package com.interest.tracker.module.match.service;

import com.interest.tracker.module.match.controller.app.vo.*;

/**
 * 比赛服务接口
 *
 * @author interest-tracker
 */
public interface MatchService {

    /**
     * 创建比赛记录
     *
     * @param reqVO 创建请求
     * @return 创建的记录ID
     */
    MatchCreateRespVO createMatch(MatchCreateReqVO reqVO);

    /**
     * 更新比赛记录
     *
     * @param reqVO 更新请求
     */
    void updateMatch(MatchUpdateReqVO reqVO);

    /**
     * 获取比赛详情
     *
     * @param id 记录ID
     * @return 比赛详情
     */
    MatchRespVO getMatch(Long id);

    /**
     * 获取比赛分页列表（包含统计信息）
     *
     * @param reqVO 分页请求
     * @return 分页结果（包含类型统计）
     */
    MatchPageWithStatsRespVO getMatchPage(MatchPageReqVO reqVO);

    /**
     * 删除比赛记录
     *
     * @param id 记录ID
     */
    void deleteMatch(Long id);

}

