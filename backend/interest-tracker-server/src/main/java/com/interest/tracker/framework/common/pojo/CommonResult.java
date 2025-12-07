package com.interest.tracker.framework.common.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

import static com.interest.tracker.framework.common.exception.enums.GlobalErrorCodeConstants.SUCCESS;

/**
 * 通用返回结果
 *
 * @param <T> 数据泛型
 */
@Data
@Schema(description = "通用返回结果")
public class CommonResult<T> implements Serializable {

    /**
     * 错误码
     */
    @Schema(description = "错误码", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer code;

    /**
     * 返回数据
     */
    @Schema(description = "返回数据")
    private T data;

    /**
     * 错误提示
     */
    @Schema(description = "错误提示", requiredMode = Schema.RequiredMode.REQUIRED, example = "操作成功")
    private String msg;

    /**
     * 将传入的 result 对象，转换成另外一个泛型结果的对象
     * <p>
     * 因为 A 方法返回的 CommonResult<String>，被 B 方法返回了，B 方法返回的是 CommonResult<Integer>
     * 此时需要类型转换
     *
     * @param result 传入的 result 对象
     * @param <T>    返回的泛型
     * @return 新的 CommonResult 对象
     */
    public static <T> CommonResult<T> error(CommonResult<?> result) {
        return error(result.getCode(), result.getMsg());
    }

    public static <T> CommonResult<T> error(Integer code, String message) {
        CommonResult<T> result = new CommonResult<>();
        result.code = code;
        result.msg = message;
        return result;
    }

    public static <T> CommonResult<T> success(T data) {
        CommonResult<T> result = new CommonResult<>();
        result.code = SUCCESS.getCode();
        result.data = data;
        result.msg = SUCCESS.getMsg();
        return result;
    }

    public static <T> CommonResult<T> success() {
        CommonResult<T> result = new CommonResult<>();
        result.code = SUCCESS.getCode();
        result.msg = SUCCESS.getMsg();
        return result;
    }

    @JsonIgnore
    public boolean isSuccess() {
        return SUCCESS.getCode().equals(code);
    }

    @JsonIgnore
    public boolean isError() {
        return !isSuccess();
    }
}

