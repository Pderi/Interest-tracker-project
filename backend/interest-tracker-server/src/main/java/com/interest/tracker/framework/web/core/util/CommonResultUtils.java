package com.interest.tracker.framework.web.core.util;

import com.interest.tracker.framework.common.pojo.CommonResult;

/**
 * 通用返回结果工具类
 * 提供便捷的success方法
 */
public class CommonResultUtils {

    /**
     * 返回成功，无数据
     */
    public static <T> CommonResult<T> success() {
        return CommonResult.success();
    }

    /**
     * 返回成功，带数据
     */
    public static <T> CommonResult<T> success(T data) {
        return CommonResult.success(data);
    }

}

