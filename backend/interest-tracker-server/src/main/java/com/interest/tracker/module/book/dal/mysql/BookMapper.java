package com.interest.tracker.module.book.dal.mysql;

import com.interest.tracker.framework.mybatis.core.mapper.BaseMapperX;
import com.interest.tracker.module.book.dal.dataobject.BookDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 书籍 Mapper
 *
 * @author interest-tracker
 */
@Mapper
public interface BookMapper extends BaseMapperX<BookDO> {

}

