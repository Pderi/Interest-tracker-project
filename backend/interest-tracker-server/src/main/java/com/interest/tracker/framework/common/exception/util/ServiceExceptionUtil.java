package com.interest.tracker.framework.common.exception.util;

import com.interest.tracker.framework.common.exception.ErrorCode;
import com.interest.tracker.framework.common.exception.ServiceException;

/**
 * 异常工具类
 */
public class ServiceExceptionUtil {

    public static ServiceException exception(ErrorCode errorCode) {
        return new ServiceException(errorCode);
    }

    public static ServiceException exception(Integer code, String msg) {
        return new ServiceException(code, msg);
    }

}

