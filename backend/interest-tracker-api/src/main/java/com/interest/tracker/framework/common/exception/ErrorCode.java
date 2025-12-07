package com.interest.tracker.framework.common.exception;

import lombok.Data;

import java.io.Serializable;

/**
 * 错误码对象
 */
@Data
public class ErrorCode implements Serializable {

    /**
     * 错误码
     */
    private final Integer code;
    /**
     * 错误提示
     */
    private final String msg;

    public ErrorCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}

