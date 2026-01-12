package com.interest.tracker.module.travel.dal.mysql;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseMapper;
import com.interest.tracker.framework.common.pojo.PageResult;
import com.interest.tracker.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.interest.tracker.framework.mybatis.core.query.MPJLambdaWrapperX;
import com.interest.tracker.module.travel.controller.app.vo.TravelPageReqVO;
import com.interest.tracker.module.travel.dal.dataobject.TravelPlaceDO;
import com.interest.tracker.module.travel.dal.dataobject.TravelRecordDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 旅游记录 Mapper
 *
 * @author interest-tracker
 */
@Mapper
public interface TravelRecordMapper extends MPJBaseMapper<TravelRecordDO> {

    /**
     * 分页查询旅游记录（支持关联 TravelPlace 表筛选）
     *
     * @param reqVO 分页请求参数
     * @return 分页结果
     */
    default PageResult<TravelRecordDO> selectPage(TravelPageReqVO reqVO) {
        MPJLambdaWrapperX<TravelRecordDO> wrapper = new MPJLambdaWrapperX<TravelRecordDO>();
        wrapper.leftJoin(TravelPlaceDO.class, TravelPlaceDO::getId, TravelRecordDO::getPlaceId);
        wrapper.eqIfPresent(TravelRecordDO::getUserId, reqVO.getUserId())
                .eqIfPresent(TravelRecordDO::getTravelStatus, reqVO.getStatus())
                .betweenIfPresent(TravelRecordDO::getTravelDate, reqVO.getStartTravelDate(), reqVO.getEndTravelDate())
                .eqIfPresent(TravelPlaceDO::getCountry, reqVO.getCountry())
                .eqIfPresent(TravelPlaceDO::getCity, reqVO.getCity())
                .eqIfPresent(TravelPlaceDO::getPlaceType, reqVO.getPlaceType())
                .likeIfPresent(TravelRecordDO::getTags, reqVO.getTag());
        
        // 关键词搜索：地点名称、城市或国家匹配（OR 关系）
        if (reqVO.getKeyword() != null && !reqVO.getKeyword().trim().isEmpty()) {
            wrapper.and(w -> w.like(TravelPlaceDO::getName, reqVO.getKeyword())
                    .or()
                    .like(TravelPlaceDO::getCity, reqVO.getKeyword())
                    .or()
                    .like(TravelPlaceDO::getCountry, reqVO.getKeyword()));
        }
        
        wrapper.selectAll(TravelRecordDO.class);

        // 排序
        if ("travelDate".equals(reqVO.getSort())) {
            wrapper.orderByDesc(TravelRecordDO::getTravelDate);
        } else if ("rating".equals(reqVO.getSort())) {
            wrapper.orderByDesc(TravelRecordDO::getPersonalRating);
        } else {
            // 默认按创建时间倒序
            wrapper.orderByDesc(TravelRecordDO::getCreateTime);
        }

        // 分页查询
        Page<TravelRecordDO> mpPage = new Page<>(reqVO.getPageNo(), reqVO.getPageSize());
        Page<TravelRecordDO> page = selectJoinPage(mpPage, TravelRecordDO.class, wrapper.distinct());
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    /**
     * 根据用户ID和地点ID查询记录
     *
     * @param userId 用户ID
     * @param placeId 地点ID
     * @return 旅游记录
     */
    default TravelRecordDO selectByUserIdAndPlaceId(Long userId, Long placeId) {
        MPJLambdaWrapperX<TravelRecordDO> wrapper = new MPJLambdaWrapperX<TravelRecordDO>()
                .eqIfPresent(TravelRecordDO::getUserId, userId)
                .eqIfPresent(TravelRecordDO::getPlaceId, placeId)
                .selectAll(TravelRecordDO.class);
        return selectJoinOne(TravelRecordDO.class, wrapper);
    }

    /**
     * 统计各状态的数量（不包含搜索和筛选条件，只按用户ID）
     *
     * @param userId 用户ID
     * @return Map<状态值, 数量>
     */
    default java.util.Map<Integer, Long> countByStatus(Long userId) {
        java.util.Map<Integer, Long> result = new java.util.HashMap<>();
        for (int status = 1; status <= 3; status++) {
            LambdaQueryWrapperX<TravelRecordDO> wrapper = new LambdaQueryWrapperX<>();
            wrapper.eq(TravelRecordDO::getUserId, userId)
                    .eq(TravelRecordDO::getTravelStatus, status);
            long count = selectCount(wrapper);
            result.put(status, count);
        }
        return result;
    }

}

