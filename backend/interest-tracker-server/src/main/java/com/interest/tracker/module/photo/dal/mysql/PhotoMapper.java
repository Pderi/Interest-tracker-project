package com.interest.tracker.module.photo.dal.mysql;

import com.interest.tracker.framework.common.pojo.PageResult;
import com.interest.tracker.framework.mybatis.core.mapper.BaseMapperX;
import com.interest.tracker.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.interest.tracker.module.photo.controller.app.vo.PhotoPageReqVO;
import com.interest.tracker.module.photo.dal.dataobject.PhotoDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 照片 Mapper
 *
 * @author interest-tracker
 */
@Mapper
public interface PhotoMapper extends BaseMapperX<PhotoDO> {

    /**
     * 分页查询照片（支持跨模块筛选）
     *
     * @param reqVO 分页请求参数
     * @return 分页结果
     */
    default PageResult<PhotoDO> selectPage(PhotoPageReqVO reqVO) {
        LambdaQueryWrapperX<PhotoDO> wrapper = new LambdaQueryWrapperX<>();
        wrapper.eq(PhotoDO::getUserId, reqVO.getUserId());
        
        if (reqVO.getCategoryId() != null) {
            wrapper.eq(PhotoDO::getCategoryId, reqVO.getCategoryId());
        }
        if (reqVO.getTravelRecordId() != null) {
            wrapper.eq(PhotoDO::getTravelRecordId, reqVO.getTravelRecordId());
        }
        if (reqVO.getConcertRecordId() != null) {
            wrapper.eq(PhotoDO::getConcertRecordId, reqVO.getConcertRecordId());
        }
        if (reqVO.getShootTimeStart() != null) {
            wrapper.ge(PhotoDO::getShootTime, reqVO.getShootTimeStart());
        }
        if (reqVO.getShootTimeEnd() != null) {
            wrapper.le(PhotoDO::getShootTime, reqVO.getShootTimeEnd());
        }

        // 如果只查询未关联的照片
        if (Boolean.TRUE.equals(reqVO.getUnlinkedOnly())) {
            wrapper.isNull(PhotoDO::getTravelRecordId)
                   .isNull(PhotoDO::getConcertRecordId);
        }

        // 关键词搜索
        if (reqVO.getKeyword() != null && !reqVO.getKeyword().isEmpty()) {
            wrapper.and(w -> w.like(PhotoDO::getTitle, reqVO.getKeyword())
                    .or()
                    .like(PhotoDO::getDescription, reqVO.getKeyword())
                    .or()
                    .like(PhotoDO::getTags, reqVO.getKeyword()));
        }

        wrapper.orderByDesc(PhotoDO::getShootTime)
               .orderByDesc(PhotoDO::getCreateTime);

        return selectPage(reqVO, wrapper);
    }

    /**
     * 根据关联记录查询照片列表
     *
     * @param userId 用户ID
     * @param travelRecordId 旅游记录ID（可选）
     * @param concertRecordId 观演记录ID（可选）
     * @return 照片列表
     */
    default List<PhotoDO> selectListByRelation(Long userId, Long travelRecordId, Long concertRecordId) {
        LambdaQueryWrapperX<PhotoDO> wrapper = new LambdaQueryWrapperX<>();
        wrapper.eq(PhotoDO::getUserId, userId);
        if (travelRecordId != null) {
            wrapper.eq(PhotoDO::getTravelRecordId, travelRecordId);
        }
        if (concertRecordId != null) {
            wrapper.eq(PhotoDO::getConcertRecordId, concertRecordId);
        }
        wrapper.orderByDesc(PhotoDO::getShootTime)
               .orderByDesc(PhotoDO::getCreateTime);
        return selectList(wrapper);
    }

    /**
     * 统计照片数量（按关联记录）
     *
     * @param userId 用户ID
     * @param travelRecordId 旅游记录ID（可选）
     * @param concertRecordId 观演记录ID（可选）
     * @return 照片数量
     */
    default long countByRelation(Long userId, Long travelRecordId, Long concertRecordId) {
        LambdaQueryWrapperX<PhotoDO> wrapper = new LambdaQueryWrapperX<>();
        wrapper.eq(PhotoDO::getUserId, userId);
        if (travelRecordId != null) {
            wrapper.eq(PhotoDO::getTravelRecordId, travelRecordId);
        }
        if (concertRecordId != null) {
            wrapper.eq(PhotoDO::getConcertRecordId, concertRecordId);
        }
        return selectCount(wrapper);
    }

}

