package com.interest.tracker.framework.web.core.handler;

import com.interest.tracker.framework.common.exception.ErrorCode;
import com.interest.tracker.framework.common.exception.ServiceException;
import com.interest.tracker.framework.common.pojo.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;

import static com.interest.tracker.framework.common.exception.enums.GlobalErrorCodeConstants.BAD_REQUEST;
import static com.interest.tracker.framework.common.exception.enums.GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理 SpringMVC 请求参数缺失
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public CommonResult<?> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex) {
        log.warn("[methodArgumentNotValidExceptionHandler]", ex);
        FieldError fieldError = ex.getBindingResult().getFieldError();
        assert fieldError != null;
        return CommonResult.error(BAD_REQUEST.getCode(), String.format("请求参数不正确:%s", fieldError.getDefaultMessage()));
    }

    /**
     * 处理 SpringMVC 请求参数缺失
     */
    @ExceptionHandler(value = BindException.class)
    public CommonResult<?> bindExceptionHandler(BindException ex) {
        log.warn("[bindExceptionHandler]", ex);
        FieldError fieldError = ex.getFieldError();
        assert fieldError != null;
        return CommonResult.error(BAD_REQUEST.getCode(), String.format("请求参数不正确:%s", fieldError.getDefaultMessage()));
    }

    /**
     * 处理 Validator 校验不通过产生的异常
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    public CommonResult<?> constraintViolationExceptionHandler(ConstraintViolationException ex) {
        log.warn("[constraintViolationExceptionHandler]", ex);
        String message = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        return CommonResult.error(BAD_REQUEST.getCode(), String.format("请求参数不正确:%s", message));
    }

    /**
     * 处理业务异常 ServiceException
     */
    @ExceptionHandler(value = ServiceException.class)
    public CommonResult<?> serviceExceptionHandler(ServiceException ex) {
        log.info("[serviceExceptionHandler]", ex);
        return CommonResult.error(ex.getCode(), ex.getMsg());
    }

    /**
     * 处理系统异常，兜底处理所有的一切
     */
    @ExceptionHandler(value = Exception.class)
    public CommonResult<?> defaultExceptionHandler(HttpServletRequest req, Exception ex) {
        log.error("[defaultExceptionHandler]", ex);
        return CommonResult.error(INTERNAL_SERVER_ERROR.getCode(), INTERNAL_SERVER_ERROR.getMsg());
    }

}

