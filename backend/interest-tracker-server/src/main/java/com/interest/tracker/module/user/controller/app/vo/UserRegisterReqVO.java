package com.interest.tracker.module.user.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户注册请求 VO
 */
@Data
@Schema(description = "用户 App - 注册请求")
public class UserRegisterReqVO {

    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "test_user")
    @NotBlank(message = "用户名不能为空")
    @Size(max = 64, message = "用户名长度不能超过64个字符")
    private String username;

    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 64, message = "密码长度需要在 6-64 位之间")
    private String password;

    @Schema(description = "昵称", example = "兴趣用户")
    @Size(max = 64, message = "昵称长度不能超过64个字符")
    private String nickname;

    @Schema(description = "邮箱", example = "test@example.com")
    @Email(message = "邮箱格式不正确")
    @Size(max = 128, message = "邮箱长度不能超过128个字符")
    private String email;

    @Schema(description = "手机号", example = "13800000000")
    @Size(max = 32, message = "手机号长度不能超过32个字符")
    private String phone;

}


