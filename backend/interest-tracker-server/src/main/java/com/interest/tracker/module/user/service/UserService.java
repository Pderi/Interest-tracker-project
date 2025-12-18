package com.interest.tracker.module.user.service;

import com.interest.tracker.module.user.controller.app.vo.UserLoginReqVO;
import com.interest.tracker.module.user.controller.app.vo.UserRegisterReqVO;
import com.interest.tracker.module.user.controller.app.vo.UserRespVO;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户注册
     *
     * @param reqVO 注册请求
     * @return 用户ID
     */
    Long register(UserRegisterReqVO reqVO);

    /**
     * 用户登录
     *
     * @param reqVO 登录请求
     * @return JWT Token
     */
    String login(UserLoginReqVO reqVO);

    /**
     * 根据用户ID获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    UserRespVO getUser(Long userId);

    /**
     * 刷新当前用户的 Token
     *
     * @param userId 用户ID
     * @return 新的 JWT Token
     */
    String refreshToken(Long userId);

}


