package com.interest.tracker.framework.mybatis.core.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

import java.util.Collection;

/**
 * 拓展 MyBatis Plus QueryWrapper 类，主要增加如下功能：
 *
 * 1. 拼接条件的方法，增加 xxxIfPresent 方法，用于判断值不存在的时候，不要拼接到条件中。
 */
public class LambdaQueryWrapperX<T> extends LambdaQueryWrapper<T> {

    public LambdaQueryWrapperX<T> likeIfPresent(SFunction<T, ?> column, String val) {
        if (val != null && !val.isEmpty()) {
            return (LambdaQueryWrapperX<T>) super.like(column, val);
        }
        return this;
    }

    public LambdaQueryWrapperX<T> inIfPresent(SFunction<T, ?> column, Collection<?> values) {
        if (values != null && !values.isEmpty()) {
            return (LambdaQueryWrapperX<T>) super.in(column, values);
        }
        return this;
    }

    public LambdaQueryWrapperX<T> eqIfPresent(SFunction<T, ?> column, Object val) {
        if (val != null) {
            return (LambdaQueryWrapperX<T>) super.eq(column, val);
        }
        return this;
    }

    public LambdaQueryWrapperX<T> neIfPresent(SFunction<T, ?> column, Object val) {
        if (val != null) {
            return (LambdaQueryWrapperX<T>) super.ne(column, val);
        }
        return this;
    }

    public LambdaQueryWrapperX<T> gtIfPresent(SFunction<T, ?> column, Object val) {
        if (val != null) {
            return (LambdaQueryWrapperX<T>) super.gt(column, val);
        }
        return this;
    }

    public LambdaQueryWrapperX<T> geIfPresent(SFunction<T, ?> column, Object val) {
        if (val != null) {
            return (LambdaQueryWrapperX<T>) super.ge(column, val);
        }
        return this;
    }

    public LambdaQueryWrapperX<T> ltIfPresent(SFunction<T, ?> column, Object val) {
        if (val != null) {
            return (LambdaQueryWrapperX<T>) super.lt(column, val);
        }
        return this;
    }

    public LambdaQueryWrapperX<T> leIfPresent(SFunction<T, ?> column, Object val) {
        if (val != null) {
            return (LambdaQueryWrapperX<T>) super.le(column, val);
        }
        return this;
    }

    public LambdaQueryWrapperX<T> betweenIfPresent(SFunction<T, ?> column, Object val1, Object val2) {
        if (val1 != null && val2 != null) {
            return (LambdaQueryWrapperX<T>) super.between(column, val1, val2);
        }
        return this;
    }

    // 手动传入字段名，避免反射
    public LambdaQueryWrapperX<T> eq(String field, Object val) {
        if (val != null) {
            return (LambdaQueryWrapperX<T>) apply(field + " = {0}", val);
        }
        return this;
    }

    public LambdaQueryWrapperX<T> in(String field, Collection<?> values) {
        if (values != null && !values.isEmpty()) {
            return (LambdaQueryWrapperX<T>) apply(field + " IN (" + String.join(",", values.stream().map(v -> "#{" + v + "}").toArray(String[]::new)) + ")");
        }
        return this;
    }

}

