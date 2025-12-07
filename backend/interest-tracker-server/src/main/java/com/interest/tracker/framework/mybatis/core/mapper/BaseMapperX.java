package com.interest.tracker.framework.mybatis.core.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.interest.tracker.framework.common.pojo.PageParam;
import com.interest.tracker.framework.common.pojo.PageResult;
import com.interest.tracker.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * 在 MyBatis Plus 的 BaseMapper 的基础上拓展，提供更多的能力
 */
public interface BaseMapperX<T> extends BaseMapper<T> {

    default PageResult<T> selectPage(PageParam reqVO, Wrapper<T> queryWrapper) {
        // 转换成 MyBatis Plus 的分页对象
        Page<T> mpPage = new Page<>(reqVO.getPageNo(), reqVO.getPageSize());
        // 执行分页查询
        Page<T> page = selectPage(mpPage, queryWrapper);
        // 转换返回
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    default PageResult<T> selectPage(PageParam reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<>());
    }

    default T selectOne(String field, Object value) {
        return selectOne(new LambdaQueryWrapperX<T>().eq(field, value));
    }

    default T selectOne(String field1, Object value1, String field2, Object value2) {
        return selectOne(new LambdaQueryWrapperX<T>().eq(field1, value1).eq(field2, value2));
    }

    default Long selectCount() {
        return selectCount(new LambdaQueryWrapperX<>());
    }

    default Long selectCount(String field, Object value) {
        return selectCount(new LambdaQueryWrapperX<T>().eq(field, value));
    }

    default List<T> selectList() {
        return selectList(new LambdaQueryWrapperX<>());
    }

    default List<T> selectList(String field, Object value) {
        return selectList(new LambdaQueryWrapperX<T>().eq(field, value));
    }

    default List<T> selectList(String field, Collection<?> values) {
        return selectList(new LambdaQueryWrapperX<T>().in(field, values));
    }

    default void insertBatch(Collection<T> entities) {
        entities.forEach(this::insert);
    }

}

