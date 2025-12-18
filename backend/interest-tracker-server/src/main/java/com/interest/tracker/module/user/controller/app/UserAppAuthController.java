package com.interest.tracker.module.user.controller.app;

import com.interest.tracker.framework.common.pojo.CommonResult;
import com.interest.tracker.framework.security.core.UserContext;
import com.interest.tracker.module.user.controller.app.vo.UserLoginReqVO;
import com.interest.tracker.module.user.controller.app.vo.UserRegisterReqVO;
import com.interest.tracker.module.user.controller.app.vo.UserRespVO;
import com.interest.tracker.module.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.interest.tracker.framework.common.exception.enums.GlobalErrorCodeConstants.UNAUTHORIZED;
import static com.interest.tracker.framework.common.pojo.CommonResult.error;

/**
 * 用户 App - 认证相关接口
 */
@Tag(name = "用户 App - 认证")
@RestController
@RequestMapping("/api/user")
@Validated
public class UserAppAuthController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public CommonResult<Long> register(@Valid @RequestBody UserRegisterReqVO reqVO) {
        Long userId = userService.register(reqVO);
        return CommonResult.success(userId);
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public CommonResult<String> login(@Valid @RequestBody UserLoginReqVO reqVO) {
        String token = userService.login(reqVO);
        return CommonResult.success(token);
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "刷新当前登录用户的 Token")
    public CommonResult<String> refreshToken() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return error(UNAUTHORIZED.getCode(), UNAUTHORIZED.getMsg());
        }
        String token = userService.refreshToken(userId);
        return CommonResult.success(token);
    }

    @GetMapping("/info")
    @Operation(summary = "获取当前登录用户信息")
    public CommonResult<UserRespVO> getCurrentUserInfo() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            // 直接返回未登录错误
            return error(UNAUTHORIZED.getCode(), UNAUTHORIZED.getMsg());
        }
        UserRespVO user = userService.getUser(userId);
        return CommonResult.success(user);
    }

}


