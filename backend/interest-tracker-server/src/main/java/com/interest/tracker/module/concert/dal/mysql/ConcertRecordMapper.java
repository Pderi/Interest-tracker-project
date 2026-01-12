package com.interest.tracker.module.concert.dal.mysql;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseMapper;
import com.interest.tracker.framework.common.pojo.PageResult;
import com.interest.tracker.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.interest.tracker.framework.mybatis.core.query.MPJLambdaWrapperX;
import com.interest.tracker.module.concert.controller.app.vo.ConcertPageReqVO;
import com.interest.tracker.module.concert.dal.dataobject.ConcertDO;
import com.interest.tracker.module.concert.dal.dataobject.ConcertRecordDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 观演记录 Mapper
 *
 * @author interest-tracker
 */
@Mapper
public interface ConcertRecordMapper extends MPJBaseMapper<ConcertRecordDO> {

    /**
     * 分页查询观演记录（支持关联 Concert 表筛选）
     *
     * @param reqVO 分页请求参数
     * @return 分页结果
     */
    default PageResult<ConcertRecordDO> selectPage(ConcertPageReqVO reqVO) {
        MPJLambdaWrapperX<ConcertRecordDO> wrapper = new MPJLambdaWrapperX<ConcertRecordDO>();
        wrapper.leftJoin(ConcertDO.class, ConcertDO::getId, ConcertRecordDO::getConcertId);
        wrapper.eqIfPresent(ConcertRecordDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ConcertRecordDO::getWatchStatus, reqVO.getStatus())
                .betweenIfPresent(ConcertRecordDO::getWatchDate, reqVO.getStartWatchDate(), reqVO.getEndWatchDate())
                .eqIfPresent(ConcertDO::getArtist, reqVO.getArtist())
                .eqIfPresent(ConcertDO::getCity, reqVO.getCity())
                .eqIfPresent(ConcertDO::getConcertType, reqVO.getConcertType())
                .likeIfPresent(ConcertRecordDO::getTags, reqVO.getTag());
        
        // 关键词搜索：演出名称或艺术家匹配（OR 关系）
        if (reqVO.getKeyword() != null && !reqVO.getKeyword().trim().isEmpty()) {
            wrapper.and(w -> w.like(ConcertDO::getTitle, reqVO.getKeyword())
                    .or()
                    .like(ConcertDO::getArtist, reqVO.getKeyword()));
        }
        
        wrapper.selectAll(ConcertRecordDO.class);

        // 排序
        if ("watchDate".equals(reqVO.getSort())) {
            wrapper.orderByDesc(ConcertRecordDO::getWatchDate);
        } else if ("rating".equals(reqVO.getSort())) {
            wrapper.orderByDesc(ConcertRecordDO::getPersonalRating);
        } else {
            // 默认按创建时间倒序
            wrapper.orderByDesc(ConcertRecordDO::getCreateTime);
        }

        // 分页查询
        Page<ConcertRecordDO> mpPage = new Page<>(reqVO.getPageNo(), reqVO.getPageSize());
        Page<ConcertRecordDO> page = selectJoinPage(mpPage, ConcertRecordDO.class, wrapper.distinct());
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    /**
     * 根据用户ID和演唱会ID查询记录
     *
     * @param userId 用户ID
     * @param concertId 演唱会ID
     * @return 观演记录
     */
    default ConcertRecordDO selectByUserIdAndConcertId(Long userId, Long concertId) {
        MPJLambdaWrapperX<ConcertRecordDO> wrapper = new MPJLambdaWrapperX<ConcertRecordDO>()
                .eqIfPresent(ConcertRecordDO::getUserId, userId)
                .eqIfPresent(ConcertRecordDO::getConcertId, concertId)
                .selectAll(ConcertRecordDO.class);
        return selectJoinOne(ConcertRecordDO.class, wrapper);
    }

    /**
     * 统计各状态的数量（不包含搜索和筛选条件，只按用户ID）
     *
     * @param userId 用户ID
     * @return Map<状态值, 数量>
     */
    default java.util.Map<Integer, Long> countByStatus(Long userId) {
        java.util.Map<Integer, Long> result = new java.util.HashMap<>();
        for (int status = 1; status <= 2; status++) {
            LambdaQueryWrapperX<ConcertRecordDO> wrapper = new LambdaQueryWrapperX<>();
            wrapper.eq(ConcertRecordDO::getUserId, userId)
                    .eq(ConcertRecordDO::getWatchStatus, status);
            long count = selectCount(wrapper);
            result.put(status, count);
        }
        return result;
    }

}

