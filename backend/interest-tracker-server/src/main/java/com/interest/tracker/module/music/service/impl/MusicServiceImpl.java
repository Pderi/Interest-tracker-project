package com.interest.tracker.module.music.service.impl;

import com.interest.tracker.framework.common.pojo.PageResult;
import com.interest.tracker.framework.common.util.object.BeanUtils;
import com.interest.tracker.framework.security.core.UserContext;
import com.interest.tracker.module.music.controller.app.vo.*;
import com.interest.tracker.module.music.dal.dataobject.AlbumDO;
import com.interest.tracker.module.music.dal.dataobject.AlbumRecordDO;
import com.interest.tracker.module.music.dal.mysql.AlbumMapper;
import com.interest.tracker.module.music.dal.mysql.AlbumRecordMapper;
import com.interest.tracker.module.music.enums.ListenStatusEnum;
import com.interest.tracker.module.music.service.MusicService;
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
import static com.interest.tracker.module.music.constants.MusicErrorCodeConstants.*;

/**
 * 音乐服务实现
 *
 * @author interest-tracker
 */
@Service
@Validated
@Slf4j
public class MusicServiceImpl implements MusicService {

    @Resource
    private AlbumMapper albumMapper;

    @Resource
    private AlbumRecordMapper albumRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AlbumCreateRespVO createAlbum(AlbumCreateReqVO reqVO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        // 1. 创建专辑
        AlbumDO albumDO = BeanUtils.toBean(reqVO, AlbumDO.class);
        albumMapper.insert(albumDO);

        // 2. 检查是否已存在听歌记录
        AlbumRecordDO existRecord = albumRecordMapper.selectByUserIdAndAlbumId(userId, albumDO.getId());
        if (existRecord != null) {
            throw exception(ALBUM_RECORD_ALREADY_EXISTS);
        }

        // 3. 创建听歌记录
        AlbumRecordDO recordDO = BeanUtils.toBean(reqVO, AlbumRecordDO.class);
        recordDO.setUserId(userId);
        recordDO.setAlbumId(albumDO.getId());
        // 设置默认听歌状态
        if (recordDO.getListenStatus() == null) {
            recordDO.setListenStatus(1); // 默认"想听"
        }
        albumRecordMapper.insert(recordDO);

        // 4. 返回结果
        AlbumCreateRespVO respVO = new AlbumCreateRespVO();
        respVO.setAlbumId(albumDO.getId());
        respVO.setRecordId(recordDO.getId());
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAlbumRecord(AlbumRecordUpdateReqVO reqVO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        // 1. 校验记录存在
        AlbumRecordDO recordDO = validateAlbumRecordExists(reqVO.getId());

        // 2. 校验权限（只能更新自己的记录）
        if (!recordDO.getUserId().equals(userId)) {
            throw exception(ALBUM_RECORD_NOT_OWNER);
        }

        // 3. 应用记录更新字段
        applyRecordUpdate(reqVO, recordDO);
        // 4. 根据业务规则自动填充字段
        autoFillListenDateIfNeeded(reqVO, recordDO);

        albumRecordMapper.updateById(recordDO);
    }

    /**
     * 将更新请求中的非空字段合并到已有的记录上
     */
    private void applyRecordUpdate(AlbumRecordUpdateReqVO reqVO, AlbumRecordDO recordDO) {
        AlbumRecordDO updateDO = BeanUtils.toBean(reqVO, AlbumRecordDO.class);
        if (updateDO.getListenStatus() != null) {
            recordDO.setListenStatus(updateDO.getListenStatus());
        }
        if (updateDO.getPersonalRating() != null) {
            recordDO.setPersonalRating(updateDO.getPersonalRating());
        }
        if (updateDO.getListenDate() != null) {
            recordDO.setListenDate(updateDO.getListenDate());
        }
        if (updateDO.getListenCount() != null) {
            recordDO.setListenCount(updateDO.getListenCount());
        }
        if (updateDO.getComment() != null) {
            recordDO.setComment(updateDO.getComment());
        }
    }

    /**
     * 状态改为「已听」且未填写听歌日期时，自动补当天
     */
    private void autoFillListenDateIfNeeded(AlbumRecordUpdateReqVO reqVO, AlbumRecordDO recordDO) {
        if (reqVO.getListenStatus() != null
                && ListenStatusEnum.LISTENED.getValue().equals(reqVO.getListenStatus())
                && recordDO.getListenDate() == null) {
            recordDO.setListenDate(LocalDate.now());
        }
    }

    @Override
    public AlbumRespVO getAlbum(Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        // 1. 查询专辑信息
        AlbumDO albumDO = validateAlbumExists(id);

        // 2. 查询听歌记录
        AlbumRecordDO recordDO = albumRecordMapper.selectByUserIdAndAlbumId(userId, id);
        if (recordDO == null) {
            throw exception(ALBUM_RECORD_NOT_EXISTS);
        }

        // 3. 组装返回数据
        AlbumRespVO respVO = new AlbumRespVO();
        AlbumRespVO.AlbumInfo albumInfo = BeanUtils.toBean(albumDO, AlbumRespVO.AlbumInfo.class);
        AlbumRespVO.RecordInfo recordInfo = BeanUtils.toBean(recordDO, AlbumRespVO.RecordInfo.class);
        respVO.setAlbum(albumInfo);
        respVO.setRecord(recordInfo);

        return respVO;
    }

    @Override
    public AlbumPageWithStatsRespVO getAlbumPage(AlbumPageReqVO reqVO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        // 设置用户ID
        reqVO.setUserId(userId);

        // 1. 分页查询听歌记录（筛选逻辑已在 Mapper 层通过 SQL 实现）
        PageResult<AlbumRecordDO> pageResult = albumRecordMapper.selectPage(reqVO);

        // 2. 批量查询专辑信息
        List<Long> albumIds = pageResult.getList().stream()
                .map(AlbumRecordDO::getAlbumId)
                .distinct()
                .collect(Collectors.toList());
        List<AlbumDO> albumList = albumIds.isEmpty() ? List.of() : albumMapper.selectBatchIds(albumIds);

        // 3. 构建专辑信息Map
        Map<Long, AlbumDO> albumMap = albumList.stream()
                .collect(Collectors.toMap(AlbumDO::getId, album -> album));

        // 4. 组装返回数据
        List<AlbumPageRespVO> voList = pageResult.getList().stream()
                .map(record -> {
                    AlbumPageRespVO vo = BeanUtils.toBean(record, AlbumPageRespVO.class);
                    // 设置记录ID和专辑ID（字段名不同）
                    vo.setRecordId(record.getId());
                    vo.setAlbumId(record.getAlbumId());
                    // comment字段会自动从record中映射

                    // 填充专辑信息
                    AlbumDO album = albumMap.get(record.getAlbumId());
                    if (album != null) {
                        vo.setTitle(album.getTitle());
                        vo.setArtist(album.getArtist());
                        vo.setReleaseYear(album.getReleaseYear());
                        vo.setCoverUrl(album.getCoverUrl());
                        vo.setGenre(album.getGenre());
                    }
                    // 填充评价
                    vo.setComment(record.getComment());

                    return vo;
                })
                .collect(Collectors.toList());

        // 5. 统计各状态数量
        java.util.Map<Integer, Long> statusCounts = albumRecordMapper.countByStatus(userId);

        // 6. 组装响应
        AlbumPageWithStatsRespVO respVO = new AlbumPageWithStatsRespVO();
        respVO.setPage(new PageResult<>(voList, pageResult.getTotal()));
        respVO.setStatusCounts(statusCounts);
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAlbumRecord(Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        // 1. 校验记录存在
        AlbumRecordDO recordDO = validateAlbumRecordExists(id);

        // 2. 校验权限（只能删除自己的记录）
        if (!recordDO.getUserId().equals(userId)) {
            throw exception(ALBUM_RECORD_NOT_OWNER);
        }

        // 3. 软删除
        albumRecordMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAlbum(AlbumUpdateReqVO reqVO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        // 1. 校验专辑存在
        AlbumDO albumDO = validateAlbumExists(reqVO.getId());

        // 2. 应用更新字段（只更新非空字段）
        if (reqVO.getTitle() != null) {
            albumDO.setTitle(reqVO.getTitle());
        }
        if (reqVO.getArtist() != null) {
            albumDO.setArtist(reqVO.getArtist());
        }
        if (reqVO.getReleaseYear() != null) {
            albumDO.setReleaseYear(reqVO.getReleaseYear());
        }
        if (reqVO.getGenre() != null) {
            albumDO.setGenre(reqVO.getGenre());
        }
        if (reqVO.getDescription() != null) {
            albumDO.setDescription(reqVO.getDescription());
        }
        if (reqVO.getCoverUrl() != null) {
            albumDO.setCoverUrl(reqVO.getCoverUrl());
        }
        if (reqVO.getTotalTracks() != null) {
            albumDO.setTotalTracks(reqVO.getTotalTracks());
        }
        if (reqVO.getDuration() != null) {
            albumDO.setDuration(reqVO.getDuration());
        }

        // 3. 更新专辑
        albumMapper.updateById(albumDO);
    }

    /**
     * 校验专辑是否存在
     */
    private AlbumDO validateAlbumExists(Long id) {
        AlbumDO albumDO = albumMapper.selectById(id);
        if (albumDO == null) {
            throw exception(ALBUM_NOT_EXISTS);
        }
        return albumDO;
    }

    /**
     * 校验听歌记录是否存在
     */
    private AlbumRecordDO validateAlbumRecordExists(Long id) {
        AlbumRecordDO recordDO = albumRecordMapper.selectById(id);
        if (recordDO == null) {
            throw exception(ALBUM_RECORD_NOT_EXISTS);
        }
        return recordDO;
    }

}

