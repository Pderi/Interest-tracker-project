package com.interest.tracker.module.photo.dal.mysql;

import com.interest.tracker.framework.mybatis.core.mapper.BaseMapperX;
import com.interest.tracker.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.interest.tracker.module.photo.dal.dataobject.PhotoAlbumDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 相册 Mapper
 *
 * @author interest-tracker
 */
@Mapper
public interface PhotoAlbumMapper extends BaseMapperX<PhotoAlbumDO> {

    /**
     * 查询用户的所有相册列表
     *
     * @param userId 用户ID
     * @return 相册列表
     */
    default List<PhotoAlbumDO> selectListByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<PhotoAlbumDO>()
                .eq(PhotoAlbumDO::getUserId, userId)
                .orderByDesc(PhotoAlbumDO::getCreateTime));
    }

}

