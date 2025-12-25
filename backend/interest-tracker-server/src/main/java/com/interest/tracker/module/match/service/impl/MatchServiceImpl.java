package com.interest.tracker.module.match.service.impl;

import com.interest.tracker.framework.common.pojo.PageResult;
import com.interest.tracker.framework.common.util.object.BeanUtils;
import com.interest.tracker.framework.security.core.UserContext;
import com.interest.tracker.module.match.controller.app.vo.*;
import com.interest.tracker.module.match.dal.dataobject.MatchRecordDO;
import com.interest.tracker.module.match.dal.mysql.MatchRecordMapper;
import com.interest.tracker.module.match.service.MatchService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.interest.tracker.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.interest.tracker.framework.common.exception.enums.GlobalErrorCodeConstants.UNAUTHORIZED;
import static com.interest.tracker.module.match.constants.MatchErrorCodeConstants.*;

/**
 * 比赛服务实现
 *
 * @author interest-tracker
 */
@Service
@Validated
@Slf4j
public class MatchServiceImpl implements MatchService {

    @Resource
    private MatchRecordMapper matchRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MatchCreateRespVO createMatch(MatchCreateReqVO reqVO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        // 1. 校验主队和客队不能相同
        if (reqVO.getHomeTeamName().trim().equals(reqVO.getAwayTeamName().trim())) {
            throw exception(MATCH_RECORD_TEAM_SAME);
        }

        // 2. 创建比赛记录
        MatchRecordDO recordDO = new MatchRecordDO();
        recordDO.setUserId(userId);
        recordDO.setHomeTeamName(reqVO.getHomeTeamName().trim());
        recordDO.setAwayTeamName(reqVO.getAwayTeamName().trim());
        recordDO.setMatchDate(reqVO.getMatchDate());
        recordDO.setHomeScore(reqVO.getHomeScore());
        recordDO.setAwayScore(reqVO.getAwayScore());
        // 设置默认值
        if (reqVO.getMatchType() != null) {
            recordDO.setMatchType(reqVO.getMatchType());
        } else {
            recordDO.setMatchType(1); // 默认联赛
        }
        if (reqVO.getWatchType() != null) {
            recordDO.setWatchType(reqVO.getWatchType());
        } else {
            recordDO.setWatchType(2); // 默认直播
        }
        recordDO.setVenue(reqVO.getVenue());
        recordDO.setNotes(reqVO.getNotes());
        matchRecordMapper.insert(recordDO);

        // 3. 返回结果
        MatchCreateRespVO respVO = new MatchCreateRespVO();
        respVO.setRecordId(recordDO.getId());
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMatch(MatchUpdateReqVO reqVO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        // 1. 校验记录存在
        MatchRecordDO recordDO = validateMatchRecordExists(reqVO.getId());

        // 2. 校验权限（只能更新自己的记录）
        if (!recordDO.getUserId().equals(userId)) {
            throw exception(MATCH_RECORD_NOT_OWNER);
        }

        // 3. 应用更新字段
        applyMatchUpdate(reqVO, recordDO);

        // 4. 如果更新了主队和客队，校验不能相同
        String homeTeamName = recordDO.getHomeTeamName();
        String awayTeamName = recordDO.getAwayTeamName();
        if (homeTeamName != null && awayTeamName != null && homeTeamName.trim().equals(awayTeamName.trim())) {
            throw exception(MATCH_RECORD_TEAM_SAME);
        }

        matchRecordMapper.updateById(recordDO);
    }

    /**
     * 将更新请求中的非空字段合并到已有的记录上
     */
    private void applyMatchUpdate(MatchUpdateReqVO reqVO, MatchRecordDO recordDO) {
        if (reqVO.getHomeTeamName() != null) {
            recordDO.setHomeTeamName(reqVO.getHomeTeamName().trim());
        }
        if (reqVO.getAwayTeamName() != null) {
            recordDO.setAwayTeamName(reqVO.getAwayTeamName().trim());
        }
        if (reqVO.getMatchDate() != null) {
            recordDO.setMatchDate(reqVO.getMatchDate());
        }
        if (reqVO.getHomeScore() != null) {
            recordDO.setHomeScore(reqVO.getHomeScore());
        }
        if (reqVO.getAwayScore() != null) {
            recordDO.setAwayScore(reqVO.getAwayScore());
        }
        if (reqVO.getMatchType() != null) {
            recordDO.setMatchType(reqVO.getMatchType());
        }
        if (reqVO.getWatchType() != null) {
            recordDO.setWatchType(reqVO.getWatchType());
        }
        if (reqVO.getVenue() != null) {
            recordDO.setVenue(reqVO.getVenue());
        }
        if (reqVO.getNotes() != null) {
            recordDO.setNotes(reqVO.getNotes());
        }
    }

    @Override
    public MatchRespVO getMatch(Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        // 1. 查询比赛记录
        MatchRecordDO recordDO = validateMatchRecordExists(id);

        // 2. 校验权限（只能查看自己的记录）
        if (!recordDO.getUserId().equals(userId)) {
            throw exception(MATCH_RECORD_NOT_OWNER);
        }

        // 3. 组装返回数据
        MatchRespVO respVO = BeanUtils.toBean(recordDO, MatchRespVO.class);
        respVO.setRecordId(recordDO.getId());

        return respVO;
    }

    @Override
    public MatchPageWithStatsRespVO getMatchPage(MatchPageReqVO reqVO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        // 设置用户ID
        reqVO.setUserId(userId);

        // 1. 分页查询比赛记录
        PageResult<MatchRecordDO> pageResult = matchRecordMapper.selectPage(reqVO);

        // 2. 组装返回数据
        List<MatchPageRespVO> voList = pageResult.getList().stream()
                .map(record -> {
                    MatchPageRespVO vo = BeanUtils.toBean(record, MatchPageRespVO.class);
                    vo.setRecordId(record.getId());
                    return vo;
                })
                .collect(Collectors.toList());

        // 3. 统计各类型数量
        java.util.Map<Integer, Long> typeCounts = matchRecordMapper.countByType(userId);

        // 4. 组装响应
        MatchPageWithStatsRespVO respVO = new MatchPageWithStatsRespVO();
        respVO.setPage(new PageResult<>(voList, pageResult.getTotal()));
        respVO.setTypeCounts(typeCounts);
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMatch(Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        // 1. 校验记录存在
        MatchRecordDO recordDO = validateMatchRecordExists(id);

        // 2. 校验权限（只能删除自己的记录）
        if (!recordDO.getUserId().equals(userId)) {
            throw exception(MATCH_RECORD_NOT_OWNER);
        }

        // 3. 删除（软删除）
        matchRecordMapper.deleteById(id);
    }

    /* ================== 私有方法 ================== */

    /**
     * 校验比赛记录是否存在
     */
    private MatchRecordDO validateMatchRecordExists(Long id) {
        MatchRecordDO recordDO = matchRecordMapper.selectById(id);
        if (recordDO == null) {
            throw exception(MATCH_RECORD_NOT_EXISTS);
        }
        return recordDO;
    }

}

