package com.interest.tracker.module.music.service;

import com.interest.tracker.framework.common.pojo.PageResult;
import com.interest.tracker.module.music.controller.app.vo.*;

/**
 * 音乐服务接口
 *
 * @author interest-tracker
 */
public interface MusicService {

    /**
     * 创建专辑记录（手动）
     *
     * @param reqVO 创建请求
     * @return 创建的专辑ID和记录ID
     */
    AlbumCreateRespVO createAlbum(AlbumCreateReqVO reqVO);

    /**
     * 更新听歌记录
     *
     * @param reqVO 更新请求
     */
    void updateAlbumRecord(AlbumRecordUpdateReqVO reqVO);

    /**
     * 获取专辑详情
     *
     * @param id 专辑ID
     * @return 专辑详情
     */
    AlbumRespVO getAlbum(Long id);

    /**
     * 获取专辑分页列表（包含统计信息）
     *
     * @param reqVO 分页请求
     * @return 分页结果和统计信息
     */
    AlbumPageWithStatsRespVO getAlbumPage(AlbumPageReqVO reqVO);

    /**
     * 删除听歌记录
     *
     * @param id 记录ID
     */
    void deleteAlbumRecord(Long id);

    /**
     * 更新专辑信息
     *
     * @param reqVO 更新请求
     */
    void updateAlbum(AlbumUpdateReqVO reqVO);

}

