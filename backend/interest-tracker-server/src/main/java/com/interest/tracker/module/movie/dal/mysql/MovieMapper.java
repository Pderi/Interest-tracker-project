package com.interest.tracker.module.movie.dal.mysql;

import com.interest.tracker.framework.mybatis.core.mapper.BaseMapperX;
import com.interest.tracker.module.movie.dal.dataobject.MovieDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 影视 Mapper
 *
 * @author interest-tracker
 */
@Mapper
public interface MovieMapper extends BaseMapperX<MovieDO> {

}

