package com.interest.tracker.module.match.dal.mysql;

import com.interest.tracker.framework.common.pojo.PageResult;
import com.interest.tracker.framework.mybatis.core.mapper.BaseMapperX;
import com.interest.tracker.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.interest.tracker.module.match.controller.app.vo.MatchPageReqVO;
import com.interest.tracker.module.match.dal.dataobject.MatchRecordDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 比赛记录 Mapper
 *
 * @author interest-tracker
 */
@Mapper
public interface MatchRecordMapper extends BaseMapperX<MatchRecordDO> {

    /**
     * 分页查询比赛记录
     *
     * @param reqVO 分页请求参数
     * @return 分页结果
     */
    default PageResult<MatchRecordDO> selectPage(MatchPageReqVO reqVO) {
        LambdaQueryWrapperX<MatchRecordDO> wrapper = new LambdaQueryWrapperX<>();
        wrapper.eqIfPresent(MatchRecordDO::getUserId, reqVO.getUserId())
                .eqIfPresent(MatchRecordDO::getMatchType, reqVO.getMatchType())
                .eqIfPresent(MatchRecordDO::getWatchType, reqVO.getWatchType())
                .likeIfPresent(MatchRecordDO::getHomeTeamName, reqVO.getHomeTeamName())
                .likeIfPresent(MatchRecordDO::getAwayTeamName, reqVO.getAwayTeamName())
                .betweenIfPresent(MatchRecordDO::getMatchDate, reqVO.getStartMatchDate(), reqVO.getEndMatchDate())
                .likeIfPresent(MatchRecordDO::getVenue, reqVO.getVenue())
                .orderByDesc(MatchRecordDO::getMatchDate);
        return selectPage(reqVO, wrapper);
    }

    /**
     * 统计各类型的数量（不包含搜索和筛选条件，只按用户ID）
     *
     * @param userId 用户ID
     * @return Map<类型值, 数量>
     */
    default java.util.Map<Integer, Long> countByType(Long userId) {
        java.util.Map<Integer, Long> result = new java.util.HashMap<>();
        for (int type = 1; type <= 3; type++) {
            LambdaQueryWrapperX<MatchRecordDO> wrapper = new LambdaQueryWrapperX<>();
            wrapper.eq(MatchRecordDO::getUserId, userId)
                    .eq(MatchRecordDO::getMatchType, type);
            long count = selectCount(wrapper);
            result.put(type, count);
        }
        return result;
    }

}

