package com.interest.tracker.framework.mybatis.core.query;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.github.yulichang.wrapper.MPJLambdaWrapper;

import java.util.Collection;

/**
 * 拓展 MPJLambdaWrapper 类，提供 xxxIfPresent 方法
 *
 * @author interest-tracker
 */
public class MPJLambdaWrapperX<T> extends MPJLambdaWrapper<T> {

    public <R> MPJLambdaWrapperX<T> eqIfPresent(SFunction<R, ?> column, Object val) {
        if (val != null) {
            super.eq(column, val);
        }
        return this;
    }

    public <R> MPJLambdaWrapperX<T> likeIfPresent(SFunction<R, ?> column, String val) {
        if (val != null && !val.isEmpty()) {
            super.like(column, val);
        }
        return this;
    }

    public <R> MPJLambdaWrapperX<T> likeIfExists(SFunction<R, ?> column, String val) {
        if (val != null && !val.isEmpty()) {
            super.like(column, val);
        }
        return this;
    }

    public <R> MPJLambdaWrapperX<T> inIfPresent(SFunction<R, ?> column, Collection<?> values) {
        if (values != null && !values.isEmpty()) {
            super.in(column, values);
        }
        return this;
    }

    public <R> MPJLambdaWrapperX<T> betweenIfPresent(SFunction<R, ?> column, Object val1, Object val2) {
        if (val1 != null && val2 != null) {
            super.between(column, val1, val2);
        }
        return this;
    }

    public MPJLambdaWrapperX<T> selectAll(Class<?> clazz) {
        super.selectAll(clazz);
        return this;
    }

    public <R> MPJLambdaWrapperX<T> orderByDesc(SFunction<R, ?> column) {
        super.orderByDesc(column);
        return this;
    }

}

