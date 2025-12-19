package com.interest.tracker.module.music.dal.mysql;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseMapper;
import com.interest.tracker.framework.common.pojo.PageResult;
import com.interest.tracker.framework.mybatis.core.query.MPJLambdaWrapperX;
import com.interest.tracker.module.music.controller.app.vo.AlbumPageReqVO;
import com.interest.tracker.module.music.dal.dataobject.AlbumDO;
import com.interest.tracker.module.music.dal.dataobject.AlbumRecordDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 听歌记录 Mapper
 *
 * @author interest-tracker
 */
@Mapper
public interface AlbumRecordMapper extends MPJBaseMapper<AlbumRecordDO> {

    /**
     * 分页查询听歌记录（支持关联 Album 表筛选）
     *
     * @param reqVO 分页请求参数
     * @return 分页结果
     */
    default PageResult<AlbumRecordDO> selectPage(AlbumPageReqVO reqVO) {
        MPJLambdaWrapperX<AlbumRecordDO> wrapper = new MPJLambdaWrapperX<AlbumRecordDO>();
        wrapper.leftJoin(AlbumDO.class, AlbumDO::getId, AlbumRecordDO::getAlbumId);
        wrapper.eqIfPresent(AlbumRecordDO::getUserId, reqVO.getUserId())
                .eqIfPresent(AlbumRecordDO::getListenStatus, reqVO.getStatus())
                .betweenIfPresent(AlbumRecordDO::getListenDate, reqVO.getStartListenDate(), reqVO.getEndListenDate())
                .eqIfPresent(AlbumDO::getArtist, reqVO.getArtist())
                .eqIfPresent(AlbumDO::getGenre, reqVO.getGenre())
                .likeIfPresent(AlbumDO::getTitle, reqVO.getKeyword())
                .likeIfPresent(AlbumDO::getArtist, reqVO.getKeyword())
                .likeIfPresent(AlbumRecordDO::getTags, reqVO.getTag())
                .selectAll(AlbumRecordDO.class);

        // 排序
        if ("listenDate".equals(reqVO.getSort())) {
            wrapper.orderByDesc(AlbumRecordDO::getListenDate);
        } else if ("rating".equals(reqVO.getSort())) {
            wrapper.orderByDesc(AlbumRecordDO::getPersonalRating);
        } else {
            // 默认按创建时间倒序
            wrapper.orderByDesc(AlbumRecordDO::getCreateTime);
        }

        // 分页查询
        Page<AlbumRecordDO> mpPage = new Page<>(reqVO.getPageNo(), reqVO.getPageSize());
        Page<AlbumRecordDO> page = selectJoinPage(mpPage, AlbumRecordDO.class, wrapper.distinct());
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    /**
     * 根据用户ID和专辑ID查询记录
     *
     * @param userId 用户ID
     * @param albumId 专辑ID
     * @return 听歌记录
     */
    default AlbumRecordDO selectByUserIdAndAlbumId(Long userId, Long albumId) {
        MPJLambdaWrapperX<AlbumRecordDO> wrapper = new MPJLambdaWrapperX<AlbumRecordDO>()
                .eqIfPresent(AlbumRecordDO::getUserId, userId)
                .eqIfPresent(AlbumRecordDO::getAlbumId, albumId)
                .selectAll(AlbumRecordDO.class);
        return selectJoinOne(AlbumRecordDO.class, wrapper);
    }

}

