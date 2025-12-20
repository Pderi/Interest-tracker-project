package com.interest.tracker.module.movie.service;

import com.interest.tracker.framework.common.pojo.PageResult;
import com.interest.tracker.module.movie.controller.app.vo.*;

/**
 * 影视服务接口
 *
 * @author interest-tracker
 */
public interface MovieService {

    /**
     * 创建影视记录（从TMDB或手动）
     *
     * @param reqVO 创建请求
     * @return 创建的影视ID和记录ID
     */
    MovieCreateRespVO createMovie(MovieCreateReqVO reqVO);

    /**
     * 更新观看记录
     *
     * @param reqVO 更新请求
     */
    void updateMovieRecord(MovieRecordUpdateReqVO reqVO);

    /**
     * 获取影视详情
     *
     * @param id 影视ID
     * @return 影视详情
     */
    MovieRespVO getMovie(Long id);

    /**
     * 获取影视分页列表（包含统计信息）
     *
     * @param reqVO 分页请求
     * @return 分页结果和统计信息
     */
    MoviePageWithStatsRespVO getMoviePage(MoviePageReqVO reqVO);

    /**
     * 删除观看记录
     *
     * @param id 记录ID
     */
    void deleteMovieRecord(Long id);

    /**
     * 搜索影视（TMDB）
     *
     * @param keyword 搜索关键词
     * @param page 页码
     * @return 搜索结果
     */
    PageResult<MovieSearchRespVO> searchMovies(String keyword, Integer page);

}

