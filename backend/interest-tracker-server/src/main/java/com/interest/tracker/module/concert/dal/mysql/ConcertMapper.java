package com.interest.tracker.module.concert.dal.mysql;

import com.interest.tracker.framework.mybatis.core.mapper.BaseMapperX;
import com.interest.tracker.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.interest.tracker.module.concert.dal.dataobject.ConcertDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 演唱会 Mapper
 *
 * @author interest-tracker
 */
@Mapper
public interface ConcertMapper extends BaseMapperX<ConcertDO> {

    /**
     * 根据艺术家和标题查询演唱会
     *
     * @param artist 艺术家
     * @param title 演出名称
     * @return 演唱会
     */
    default ConcertDO selectByArtistAndTitle(String artist, String title) {
        return selectOne(new LambdaQueryWrapperX<ConcertDO>()
                .eq(ConcertDO::getArtist, artist)
                .eq(ConcertDO::getTitle, title));
    }

}

