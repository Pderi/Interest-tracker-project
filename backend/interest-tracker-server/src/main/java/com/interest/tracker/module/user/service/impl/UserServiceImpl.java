package com.interest.tracker.module.user.service.impl;

import com.interest.tracker.framework.common.util.object.BeanUtils;
import com.interest.tracker.framework.security.jwt.JwtUtils;
import com.interest.tracker.module.user.constants.UserErrorCodeConstants;
import com.interest.tracker.module.user.controller.app.vo.UserLoginReqVO;
import com.interest.tracker.module.user.controller.app.vo.UserRegisterReqVO;
import com.interest.tracker.module.user.controller.app.vo.UserRespVO;
import com.interest.tracker.module.user.dal.dataobject.UserDO;
import com.interest.tracker.module.user.dal.mysql.UserMapper;
import com.interest.tracker.module.user.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.interest.tracker.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 用户服务实现
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private JwtUtils jwtUtils;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long register(UserRegisterReqVO reqVO) {
        // 1. 唯一性校验
        UserDO existByUsername = userMapper.selectOne("username", reqVO.getUsername());
        if (existByUsername != null) {
            throw exception(UserErrorCodeConstants.USER_USERNAME_EXISTS);
        }
        if (reqVO.getEmail() != null && !reqVO.getEmail().isEmpty()) {
            UserDO existByEmail = userMapper.selectOne("email", reqVO.getEmail());
            if (existByEmail != null) {
                throw exception(UserErrorCodeConstants.USER_EMAIL_EXISTS);
            }
        }

        // 2. 数据转换
        UserDO user = BeanUtils.toBean(reqVO, UserDO.class);
        // 密码需要加密存储
        user.setPassword(passwordEncoder.encode(reqVO.getPassword()));
        // 默认启用
        user.setStatus(1);

        // 3. 保存
        userMapper.insert(user);
        return user.getId();
    }

    @Override
    public String login(UserLoginReqVO reqVO) {
        // 1. 根据用户名查询
        UserDO user = userMapper.selectOne("username", reqVO.getUsername());
        if (user == null) {
            throw exception(UserErrorCodeConstants.USER_PASSWORD_ERROR);
        }
        // 2. 校验状态
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw exception(UserErrorCodeConstants.USER_DISABLED);
        }
        // 3. 校验密码
        if (!passwordEncoder.matches(reqVO.getPassword(), user.getPassword())) {
            throw exception(UserErrorCodeConstants.USER_PASSWORD_ERROR);
        }
        // 4. 生成 Token
        return jwtUtils.generateToken(user.getId(), user.getUsername());
    }

    @Override
    public UserRespVO getUser(Long userId) {
        UserDO user = userMapper.selectById(userId);
        if (user == null) {
            throw exception(UserErrorCodeConstants.USER_NOT_EXISTS);
        }
        return BeanUtils.toBean(user, UserRespVO.class);
    }

    @Override
    public String refreshToken(Long userId) {
        UserDO user = userMapper.selectById(userId);
        if (user == null) {
            throw exception(UserErrorCodeConstants.USER_NOT_EXISTS);
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw exception(UserErrorCodeConstants.USER_DISABLED);
        }
        return jwtUtils.generateToken(user.getId(), user.getUsername());
    }

}


