package com.interest.tracker.framework.web.core.controller;

import com.interest.tracker.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试Controller
 * 用于验证框架是否正常工作
 */
@Tag(name = "测试接口")
@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/hello")
    @Operation(summary = "测试接口")
    public CommonResult<String> hello() {
        return CommonResult.success("Hello, Interest Tracker!");
    }

    @GetMapping("/error")
    @Operation(summary = "测试异常处理")
    public CommonResult<String> testError() {
        throw new RuntimeException("测试异常");
    }

}

