package com.interest.tracker.module.concert.service.impl;

import com.interest.tracker.framework.common.pojo.PageResult;
import com.interest.tracker.framework.common.util.object.BeanUtils;
import com.interest.tracker.framework.security.core.UserContext;
import com.interest.tracker.module.concert.controller.app.vo.*;
import com.interest.tracker.module.concert.dal.dataobject.ConcertDO;
import com.interest.tracker.module.concert.dal.dataobject.ConcertRecordDO;
import com.interest.tracker.module.concert.dal.mysql.ConcertMapper;
import com.interest.tracker.module.concert.dal.mysql.ConcertRecordMapper;
import com.interest.tracker.module.concert.enums.WatchStatusEnum;
import com.interest.tracker.module.concert.service.ConcertService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.interest.tracker.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.interest.tracker.framework.common.exception.enums.GlobalErrorCodeConstants.UNAUTHORIZED;
import static com.interest.tracker.module.concert.constants.ConcertErrorCodeConstants.*;

/**
 * 演唱会服务实现
 *
 * @author interest-tracker
 */
@Service
@Validated
@Slf4j
public class ConcertServiceImpl implements ConcertService {

    @Resource
    private ConcertMapper concertMapper;

    @Resource
    private ConcertRecordMapper concertRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConcertCreateRespVO createConcert(ConcertCreateReqVO reqVO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        // 1. 创建演唱会
        ConcertDO concertDO = BeanUtils.toBean(reqVO, ConcertDO.class);
        concertMapper.insert(concertDO);

        // 2. 检查是否已存在观演记录
        ConcertRecordDO existRecord = concertRecordMapper.selectByUserIdAndConcertId(userId, concertDO.getId());
        if (existRecord != null) {
            throw exception(CONCERT_RECORD_ALREADY_EXISTS);
        }

        // 3. 创建观演记录
        ConcertRecordDO recordDO = BeanUtils.toBean(reqVO, ConcertRecordDO.class);
        recordDO.setUserId(userId);
        recordDO.setConcertId(concertDO.getId());
        // 设置默认观演状态
        if (recordDO.getWatchStatus() == null) {
            recordDO.setWatchStatus(1); // 默认"想看"
        }
        concertRecordMapper.insert(recordDO);

        // 4. 返回结果
        ConcertCreateRespVO respVO = new ConcertCreateRespVO();
        respVO.setConcertId(concertDO.getId());
        respVO.setRecordId(recordDO.getId());
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateConcertRecord(ConcertRecordUpdateReqVO reqVO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        // 1. 校验记录存在
        ConcertRecordDO recordDO = validateConcertRecordExists(reqVO.getId());

        // 2. 校验权限（只能更新自己的记录）
        if (!recordDO.getUserId().equals(userId)) {
            throw exception(CONCERT_RECORD_NOT_OWNER);
        }

        // 3. 应用记录更新字段
        applyRecordUpdate(reqVO, recordDO);
        // 4. 根据业务规则自动填充字段
        autoFillWatchDateIfNeeded(reqVO, recordDO);

        concertRecordMapper.updateById(recordDO);
    }

    /**
     * 将更新请求中的非空字段合并到已有的记录上
     */
    private void applyRecordUpdate(ConcertRecordUpdateReqVO reqVO, ConcertRecordDO recordDO) {
        ConcertRecordDO updateDO = BeanUtils.toBean(reqVO, ConcertRecordDO.class);
        if (updateDO.getWatchStatus() != null) {
            recordDO.setWatchStatus(updateDO.getWatchStatus());
        }
        if (updateDO.getPersonalRating() != null) {
            recordDO.setPersonalRating(updateDO.getPersonalRating());
        }
        if (updateDO.getWatchDate() != null) {
            recordDO.setWatchDate(updateDO.getWatchDate());
        }
        if (updateDO.getTicketPrice() != null) {
            recordDO.setTicketPrice(updateDO.getTicketPrice());
        }
        if (updateDO.getSeatInfo() != null) {
            recordDO.setSeatInfo(updateDO.getSeatInfo());
        }
        if (updateDO.getComment() != null) {
            recordDO.setComment(updateDO.getComment());
        }
    }

    /**
     * 状态改为「已看」且未填写观演日期时，自动补当天
     */
    private void autoFillWatchDateIfNeeded(ConcertRecordUpdateReqVO reqVO, ConcertRecordDO recordDO) {
        if (reqVO.getWatchStatus() != null
                && WatchStatusEnum.WATCHED.getValue().equals(reqVO.getWatchStatus())
                && recordDO.getWatchDate() == null) {
            recordDO.setWatchDate(LocalDate.now());
        }
    }

    @Override
    public ConcertRespVO getConcert(Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        // 1. 查询演唱会信息
        ConcertDO concertDO = validateConcertExists(id);

        // 2. 查询观演记录
        ConcertRecordDO recordDO = concertRecordMapper.selectByUserIdAndConcertId(userId, id);
        if (recordDO == null) {
            throw exception(CONCERT_RECORD_NOT_EXISTS);
        }

        // 3. 组装返回数据
        ConcertRespVO respVO = new ConcertRespVO();
        ConcertRespVO.ConcertInfo concertInfo = BeanUtils.toBean(concertDO, ConcertRespVO.ConcertInfo.class);
        ConcertRespVO.RecordInfo recordInfo = BeanUtils.toBean(recordDO, ConcertRespVO.RecordInfo.class);
        respVO.setConcert(concertInfo);
        respVO.setRecord(recordInfo);

        return respVO;
    }

    @Override
    public ConcertPageWithStatsRespVO getConcertPage(ConcertPageReqVO reqVO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        // 设置用户ID
        reqVO.setUserId(userId);

        // 1. 分页查询观演记录（筛选逻辑已在 Mapper 层通过 SQL 实现）
        PageResult<ConcertRecordDO> pageResult = concertRecordMapper.selectPage(reqVO);

        // 2. 批量查询演唱会信息
        List<Long> concertIds = pageResult.getList().stream()
                .map(ConcertRecordDO::getConcertId)
                .distinct()
                .collect(Collectors.toList());
        List<ConcertDO> concertList = concertIds.isEmpty() ? List.of() : concertMapper.selectBatchIds(concertIds);

        // 3. 构建演唱会信息Map
        Map<Long, ConcertDO> concertMap = concertList.stream()
                .collect(Collectors.toMap(ConcertDO::getId, concert -> concert));

        // 4. 组装返回数据
        List<ConcertPageRespVO> voList = pageResult.getList().stream()
                .map(record -> {
                    ConcertPageRespVO vo = BeanUtils.toBean(record, ConcertPageRespVO.class);
                    // 设置记录ID和演唱会ID（字段名不同）
                    vo.setRecordId(record.getId());
                    vo.setConcertId(record.getConcertId());

                    // 填充演唱会信息
                    ConcertDO concert = concertMap.get(record.getConcertId());
                    if (concert != null) {
                        vo.setArtist(concert.getArtist());
                        vo.setTitle(concert.getTitle());
                        vo.setConcertDate(concert.getConcertDate());
                        vo.setVenue(concert.getVenue());
                        vo.setCity(concert.getCity());
                        vo.setCountry(concert.getCountry());
                        vo.setConcertType(concert.getConcertType());
                        vo.setPosterUrl(concert.getPosterUrl());
                    }
                    // 填充评价
                    vo.setComment(record.getComment());

                    return vo;
                })
                .collect(Collectors.toList());

        // 5. 统计各状态数量
        Map<Integer, Long> statusCounts = concertRecordMapper.countByStatus(userId);

        // 6. 组装响应
        ConcertPageWithStatsRespVO respVO = new ConcertPageWithStatsRespVO();
        respVO.setPage(new PageResult<>(voList, pageResult.getTotal()));
        respVO.setStatusCounts(statusCounts);
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteConcertRecord(Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        // 1. 校验记录存在
        ConcertRecordDO recordDO = validateConcertRecordExists(id);

        // 2. 校验权限（只能删除自己的记录）
        if (!recordDO.getUserId().equals(userId)) {
            throw exception(CONCERT_RECORD_NOT_OWNER);
        }

        // 3. 软删除
        concertRecordMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateConcert(ConcertUpdateReqVO reqVO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        // 1. 校验演唱会存在
        ConcertDO concertDO = validateConcertExists(reqVO.getId());

        // 2. 应用更新字段（只更新非空字段）
        if (reqVO.getArtist() != null) {
            concertDO.setArtist(reqVO.getArtist());
        }
        if (reqVO.getTitle() != null) {
            concertDO.setTitle(reqVO.getTitle());
        }
        if (reqVO.getConcertDate() != null) {
            concertDO.setConcertDate(reqVO.getConcertDate());
        }
        if (reqVO.getVenue() != null) {
            concertDO.setVenue(reqVO.getVenue());
        }
        if (reqVO.getCity() != null) {
            concertDO.setCity(reqVO.getCity());
        }
        if (reqVO.getCountry() != null) {
            concertDO.setCountry(reqVO.getCountry());
        }
        if (reqVO.getConcertType() != null) {
            concertDO.setConcertType(reqVO.getConcertType());
        }
        if (reqVO.getDescription() != null) {
            concertDO.setDescription(reqVO.getDescription());
        }
        if (reqVO.getPosterUrl() != null) {
            concertDO.setPosterUrl(reqVO.getPosterUrl());
        }

        // 3. 更新演唱会
        concertMapper.updateById(concertDO);
    }

    /**
     * 校验演唱会是否存在
     */
    private ConcertDO validateConcertExists(Long id) {
        ConcertDO concertDO = concertMapper.selectById(id);
        if (concertDO == null) {
            throw exception(CONCERT_NOT_EXISTS);
        }
        return concertDO;
    }

    /**
     * 校验观演记录是否存在
     */
    private ConcertRecordDO validateConcertRecordExists(Long id) {
        ConcertRecordDO recordDO = concertRecordMapper.selectById(id);
        if (recordDO == null) {
            throw exception(CONCERT_RECORD_NOT_EXISTS);
        }
        return recordDO;
    }

}

