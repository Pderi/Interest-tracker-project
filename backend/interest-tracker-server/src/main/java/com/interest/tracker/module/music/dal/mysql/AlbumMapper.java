package com.interest.tracker.module.music.dal.mysql;

import com.interest.tracker.framework.mybatis.core.mapper.BaseMapperX;
import com.interest.tracker.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.interest.tracker.module.music.dal.dataobject.AlbumDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 专辑 Mapper
 *
 * @author interest-tracker
 */
@Mapper
public interface AlbumMapper extends BaseMapperX<AlbumDO> {

    /**
     * 根据标题和艺术家查询专辑
     *
     * @param title 专辑名称
     * @param artist 艺术家
     * @return 专辑
     */
    default AlbumDO selectByTitleAndArtist(String title, String artist) {
        return selectOne(new LambdaQueryWrapperX<AlbumDO>()
                .eq(AlbumDO::getTitle, title)
                .eq(AlbumDO::getArtist, artist));
    }

}

