package com.interest.tracker.module.photo.dal.mysql;

import com.interest.tracker.framework.mybatis.core.mapper.BaseMapperX;
import com.interest.tracker.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.interest.tracker.module.photo.dal.dataobject.PhotoCategoryDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 照片分类 Mapper
 *
 * @author interest-tracker
 */
@Mapper
public interface PhotoCategoryMapper extends BaseMapperX<PhotoCategoryDO> {

    /**
     * 查询用户的所有分类列表（按排序顺序）
     *
     * @param userId 用户ID
     * @return 分类列表
     */
    default List<PhotoCategoryDO> selectListByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<PhotoCategoryDO>()
                .eq(PhotoCategoryDO::getUserId, userId)
                .orderByAsc(PhotoCategoryDO::getSortOrder)
                .orderByDesc(PhotoCategoryDO::getCreateTime));
    }

    /**
     * 根据用户ID和分类名称查询
     *
     * @param userId 用户ID
     * @param name 分类名称
     * @return 分类
     */
    default PhotoCategoryDO selectByUserIdAndName(Long userId, String name) {
        return selectOne(new LambdaQueryWrapperX<PhotoCategoryDO>()
                .eq(PhotoCategoryDO::getUserId, userId)
                .eq(PhotoCategoryDO::getName, name));
    }

    /**
     * 查询用户的最大排序顺序
     *
     * @param userId 用户ID
     * @return 最大排序顺序，如果没有则返回null
     */
    default Integer selectMaxSortOrder(Long userId) {
        List<PhotoCategoryDO> list = selectList(new LambdaQueryWrapperX<PhotoCategoryDO>()
                .eq(PhotoCategoryDO::getUserId, userId)
                .orderByDesc(PhotoCategoryDO::getSortOrder)
                .last("LIMIT 1"));
        return list.isEmpty() ? null : list.get(0).getSortOrder();
    }

    /**
     * 统计用户的分类数量
     *
     * @param userId 用户ID
     * @return 分类数量
     */
    default long countByUserId(Long userId) {
        return selectCount(new LambdaQueryWrapperX<PhotoCategoryDO>()
                .eq(PhotoCategoryDO::getUserId, userId));
    }

}

