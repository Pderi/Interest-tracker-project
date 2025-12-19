package com.interest.tracker.module.movie.dal.mysql;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseMapper;
import com.interest.tracker.framework.common.pojo.PageResult;
import com.interest.tracker.framework.mybatis.core.query.MPJLambdaWrapperX;
import com.interest.tracker.module.movie.controller.app.vo.MoviePageReqVO;
import com.interest.tracker.module.movie.dal.dataobject.MovieDO;
import com.interest.tracker.module.movie.dal.dataobject.MovieRecordDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 观看记录 Mapper
 *
 * @author interest-tracker
 */
@Mapper
public interface MovieRecordMapper extends MPJBaseMapper<MovieRecordDO> {

    /**
     * 分页查询观看记录（支持关联 Movie 表筛选）
     *
     * @param reqVO 分页请求参数
     * @return 分页结果
     */
    default PageResult<MovieRecordDO> selectPage(MoviePageReqVO reqVO) {
        MPJLambdaWrapperX<MovieRecordDO> wrapper = new MPJLambdaWrapperX<MovieRecordDO>();
        wrapper.leftJoin(MovieDO.class, MovieDO::getId, MovieRecordDO::getMovieId);
        wrapper.eqIfPresent(MovieRecordDO::getUserId, reqVO.getUserId())
                .eqIfPresent(MovieRecordDO::getWatchStatus, reqVO.getWatchStatus())
                .betweenIfPresent(MovieRecordDO::getWatchDate, reqVO.getStartWatchDate(), reqVO.getEndWatchDate())
                .eqIfPresent(MovieDO::getType, reqVO.getType())
                .likeIfPresent(MovieDO::getTitle, reqVO.getKeyword())
                .likeIfPresent(MovieRecordDO::getTags, reqVO.getTag())
                .selectAll(MovieRecordDO.class);

        // 排序
        if ("watchDate".equals(reqVO.getSort())) {
            wrapper.orderByDesc(MovieRecordDO::getWatchDate);
        } else if ("rating".equals(reqVO.getSort())) {
            wrapper.orderByDesc(MovieRecordDO::getPersonalRating);
        } else {
            // 默认按创建时间倒序
            wrapper.orderByDesc(MovieRecordDO::getCreateTime);
        }

        // 分页查询
        Page<MovieRecordDO> mpPage = new Page<>(reqVO.getPageNo(), reqVO.getPageSize());
        Page<MovieRecordDO> page = selectJoinPage(mpPage, MovieRecordDO.class, wrapper.distinct());
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    /**
     * 根据用户ID和影视ID查询记录
     *
     * @param userId 用户ID
     * @param movieId 影视ID
     * @return 观看记录
     */
    default MovieRecordDO selectByUserIdAndMovieId(Long userId, Long movieId) {
        MPJLambdaWrapperX<MovieRecordDO> wrapper = new MPJLambdaWrapperX<MovieRecordDO>()
                .eqIfPresent(MovieRecordDO::getUserId, userId)
                .eqIfPresent(MovieRecordDO::getMovieId, movieId)
                .selectAll(MovieRecordDO.class);
        return selectJoinOne(MovieRecordDO.class, wrapper);
    }

}

