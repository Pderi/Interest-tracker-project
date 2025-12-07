package com.interest.tracker.framework.common.util.object;

import com.interest.tracker.framework.common.pojo.PageResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Bean 工具类
 */
public class BeanUtils {

    public static <T> T toBean(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        T target;
        try {
            target = targetClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("创建对象失败", e);
        }
        org.springframework.beans.BeanUtils.copyProperties(source, target);
        return target;
    }

    public static <T> List<T> toBean(Collection<?> sourceList, Class<T> targetClass) {
        if (sourceList == null || sourceList.isEmpty()) {
            return new ArrayList<>();
        }
        List<T> result = new ArrayList<>(sourceList.size());
        for (Object source : sourceList) {
            result.add(toBean(source, targetClass));
        }
        return result;
    }

    public static <S, T> PageResult<T> toBean(PageResult<S> sourcePage, Class<T> targetClass) {
        if (sourcePage == null) {
            return new PageResult<>();
        }
        List<T> targetList = toBean(sourcePage.getList(), targetClass);
        return new PageResult<>(targetList, sourcePage.getTotal());
    }

}

