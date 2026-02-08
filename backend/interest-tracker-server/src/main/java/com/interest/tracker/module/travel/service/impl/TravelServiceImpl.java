package com.interest.tracker.module.travel.service.impl;

import com.interest.tracker.framework.common.pojo.PageResult;
import com.interest.tracker.framework.common.util.object.BeanUtils;
import com.interest.tracker.framework.security.core.UserContext;
import com.interest.tracker.module.travel.controller.app.vo.*;
import com.interest.tracker.module.travel.dal.dataobject.TravelPlaceDO;
import com.interest.tracker.module.travel.dal.dataobject.TravelRecordDO;
import com.interest.tracker.module.travel.dal.mysql.TravelPlaceMapper;
import com.interest.tracker.module.travel.dal.mysql.TravelRecordMapper;
import com.interest.tracker.module.travel.enums.TravelStatusEnum;
import com.interest.tracker.module.travel.service.TravelService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.interest.tracker.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.interest.tracker.framework.common.exception.enums.GlobalErrorCodeConstants.UNAUTHORIZED;
import static com.interest.tracker.module.travel.constants.TravelErrorCodeConstants.*;

/**
 * 旅游服务实现
 *
 * @author interest-tracker
 */
@Service
@Validated
@Slf4j
public class TravelServiceImpl implements TravelService {

    @Resource
    private TravelPlaceMapper travelPlaceMapper;

    @Resource
    private TravelRecordMapper travelRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TravelCreateRespVO createTravel(TravelCreateReqVO reqVO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        // 1. 创建或查找旅游地点
        TravelPlaceDO placeDO = travelPlaceMapper.selectByNameAndCityAndCountry(
                reqVO.getName(), reqVO.getCity(), reqVO.getCountry());
        
        if (placeDO == null) {
            // 创建新地点
            placeDO = BeanUtils.toBean(reqVO, TravelPlaceDO.class);
            travelPlaceMapper.insert(placeDO);
        }

        // 2. 检查是否已存在旅游记录
        TravelRecordDO existRecord = travelRecordMapper.selectByUserIdAndPlaceId(userId, placeDO.getId());
        if (existRecord != null) {
            throw exception(TRAVEL_RECORD_ALREADY_EXISTS);
        }

        // 3. 创建旅游记录
        TravelRecordDO recordDO = BeanUtils.toBean(reqVO, TravelRecordDO.class);
        recordDO.setUserId(userId);
        recordDO.setPlaceId(placeDO.getId());
        if (reqVO.getTags() != null) {
            recordDO.setTags(String.join("|", reqVO.getTags()));
        }
        // 设置默认旅游状态
        if (recordDO.getTravelStatus() == null) {
            recordDO.setTravelStatus(1); // 默认"想去"
        }
        travelRecordMapper.insert(recordDO);

        // 4. 返回结果
        TravelCreateRespVO respVO = new TravelCreateRespVO();
        respVO.setPlaceId(placeDO.getId());
        respVO.setRecordId(recordDO.getId());
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTravelRecord(TravelRecordUpdateReqVO reqVO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        // 1. 校验记录存在
        TravelRecordDO recordDO = validateTravelRecordExists(reqVO.getId());

        // 2. 校验权限（只能更新自己的记录）
        if (!recordDO.getUserId().equals(userId)) {
            throw exception(TRAVEL_RECORD_NOT_OWNER);
        }

        // 3. 应用记录更新字段
        applyRecordUpdate(reqVO, recordDO);
        // 4. 根据业务规则自动填充字段
        autoFillTravelDateIfNeeded(reqVO, recordDO);

        travelRecordMapper.updateById(recordDO);
    }

    /**
     * 将更新请求中的非空字段合并到已有的记录上
     */
    private void applyRecordUpdate(TravelRecordUpdateReqVO reqVO, TravelRecordDO recordDO) {
        TravelRecordDO updateDO = BeanUtils.toBean(reqVO, TravelRecordDO.class);
        if (updateDO.getTravelStatus() != null) {
            recordDO.setTravelStatus(updateDO.getTravelStatus());
        }
        if (updateDO.getPersonalRating() != null) {
            recordDO.setPersonalRating(updateDO.getPersonalRating());
        }
        if (updateDO.getTravelDate() != null) {
            recordDO.setTravelDate(updateDO.getTravelDate());
        }
        if (updateDO.getTravelDuration() != null) {
            recordDO.setTravelDuration(updateDO.getTravelDuration());
        }
        if (updateDO.getExpense() != null) {
            recordDO.setExpense(updateDO.getExpense());
        }
        if (updateDO.getComment() != null) {
            recordDO.setComment(updateDO.getComment());
        }
        if (reqVO.getTags() != null) {
            recordDO.setTags(String.join("|", reqVO.getTags()));
        }
    }

    /**
     * 状态改为「已去」且未填写旅游日期时，自动补当天
     */
    private void autoFillTravelDateIfNeeded(TravelRecordUpdateReqVO reqVO, TravelRecordDO recordDO) {
        if (reqVO.getTravelStatus() != null
                && TravelStatusEnum.VISITED.getValue().equals(reqVO.getTravelStatus())
                && recordDO.getTravelDate() == null) {
            recordDO.setTravelDate(LocalDate.now());
        }
    }

    @Override
    public TravelRespVO getTravel(Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        // 1. 查询旅游地点信息
        TravelPlaceDO placeDO = validateTravelPlaceExists(id);

        // 2. 查询旅游记录
        TravelRecordDO recordDO = travelRecordMapper.selectByUserIdAndPlaceId(userId, id);
        if (recordDO == null) {
            throw exception(TRAVEL_RECORD_NOT_EXISTS);
        }

        // 3. 组装返回数据
        TravelRespVO respVO = new TravelRespVO();
        TravelRespVO.PlaceInfo placeInfo = BeanUtils.toBean(placeDO, TravelRespVO.PlaceInfo.class);
        TravelRespVO.RecordInfo recordInfo = BeanUtils.toBean(recordDO, TravelRespVO.RecordInfo.class);
        respVO.setPlace(placeInfo);
        respVO.setRecord(recordInfo);
        recordInfo.setTags(splitToList(recordDO.getTags()));

        return respVO;
    }

    @Override
    public TravelPageWithStatsRespVO getTravelPage(TravelPageReqVO reqVO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        // 设置用户ID
        reqVO.setUserId(userId);

        // 1. 分页查询旅游记录（筛选逻辑已在 Mapper 层通过 SQL 实现）
        PageResult<TravelRecordDO> pageResult = travelRecordMapper.selectPage(reqVO);

        // 2. 批量查询旅游地点信息
        List<Long> placeIds = pageResult.getList().stream()
                .map(TravelRecordDO::getPlaceId)
                .distinct()
                .collect(Collectors.toList());
        List<TravelPlaceDO> placeList = placeIds.isEmpty() ? List.of() : travelPlaceMapper.selectBatchIds(placeIds);

        // 3. 构建旅游地点信息Map
        Map<Long, TravelPlaceDO> placeMap = placeList.stream()
                .collect(Collectors.toMap(TravelPlaceDO::getId, place -> place));

        // 4. 组装返回数据
        List<TravelPageRespVO> voList = pageResult.getList().stream()
                .map(record -> {
                    TravelPageRespVO vo = BeanUtils.toBean(record, TravelPageRespVO.class);
                    // 设置记录ID和地点ID（字段名不同）
                    vo.setRecordId(record.getId());
                    vo.setPlaceId(record.getPlaceId());

                    // 填充旅游地点信息
                    TravelPlaceDO place = placeMap.get(record.getPlaceId());
                    if (place != null) {
                        vo.setName(place.getName());
                        vo.setCountry(place.getCountry());
                        vo.setCity(place.getCity());
                        vo.setAddress(place.getAddress());
                        vo.setPlaceType(place.getPlaceType());
                        vo.setCoverUrl(place.getCoverUrl());
                    }
                    // 填充评价
                    vo.setComment(record.getComment());
                    vo.setTags(splitToList(record.getTags()));

                    return vo;
                })
                .collect(Collectors.toList());

        // 5. 统计各状态数量
        Map<Integer, Long> statusCounts = travelRecordMapper.countByStatus(userId);

        // 6. 组装响应
        TravelPageWithStatsRespVO respVO = new TravelPageWithStatsRespVO();
        respVO.setPage(new PageResult<>(voList, pageResult.getTotal()));
        respVO.setStatusCounts(statusCounts);
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTravelRecord(Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        // 1. 校验记录存在
        TravelRecordDO recordDO = validateTravelRecordExists(id);

        // 2. 校验权限（只能删除自己的记录）
        if (!recordDO.getUserId().equals(userId)) {
            throw exception(TRAVEL_RECORD_NOT_OWNER);
        }

        // 3. 软删除
        travelRecordMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTravelPlace(TravelPlaceUpdateReqVO reqVO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        // 1. 校验旅游地点存在
        TravelPlaceDO placeDO = validateTravelPlaceExists(reqVO.getId());

        // 2. 应用更新字段（只更新非空字段）
        if (reqVO.getName() != null) {
            placeDO.setName(reqVO.getName());
        }
        if (reqVO.getCountry() != null) {
            placeDO.setCountry(reqVO.getCountry());
        }
        if (reqVO.getCity() != null) {
            placeDO.setCity(reqVO.getCity());
        }
        if (reqVO.getAddress() != null) {
            placeDO.setAddress(reqVO.getAddress());
        }
        if (reqVO.getLatitude() != null) {
            placeDO.setLatitude(reqVO.getLatitude());
        }
        if (reqVO.getLongitude() != null) {
            placeDO.setLongitude(reqVO.getLongitude());
        }
        if (reqVO.getPlaceType() != null) {
            placeDO.setPlaceType(reqVO.getPlaceType());
        }
        if (reqVO.getDescription() != null) {
            placeDO.setDescription(reqVO.getDescription());
        }
        if (reqVO.getCoverUrl() != null) {
            placeDO.setCoverUrl(reqVO.getCoverUrl());
        }

        // 3. 更新旅游地点
        travelPlaceMapper.updateById(placeDO);
    }

    /**
     * 校验旅游地点是否存在
     */
    private TravelPlaceDO validateTravelPlaceExists(Long id) {
        TravelPlaceDO placeDO = travelPlaceMapper.selectById(id);
        if (placeDO == null) {
            throw exception(TRAVEL_PLACE_NOT_EXISTS);
        }
        return placeDO;
    }

    /**
     * 校验旅游记录是否存在
     */
    private TravelRecordDO validateTravelRecordExists(Long id) {
        TravelRecordDO recordDO = travelRecordMapper.selectById(id);
        if (recordDO == null) {
            throw exception(TRAVEL_RECORD_NOT_EXISTS);
        }
        return recordDO;
    }

    /**
     * 将竖线分隔的标签拆成非空列表
     */
    private List<String> splitToList(String value) {
        if (value == null || value.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(value.split("\\|"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

}

