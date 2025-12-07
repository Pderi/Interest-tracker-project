package com.interest.tracker.framework.common.exception;

import com.interest.tracker.framework.common.exception.ErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务逻辑异常
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ServiceException extends RuntimeException {

    /**
     * 业务错误码
     */
    private Integer code;
    /**
     * 错误提示
     */
    private String msg;

    /**
     * 空构造方法，避免反序列化问题
     */
    public ServiceException() {
    }

    public ServiceException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.code = errorCode.getCode();
        this.msg = errorCode.getMsg();
    }

    public ServiceException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

}

