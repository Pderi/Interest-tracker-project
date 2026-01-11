package com.interest.tracker.module.photo.service;

import com.interest.tracker.framework.common.pojo.PageResult;
import com.interest.tracker.module.photo.controller.app.vo.*;
import com.interest.tracker.module.photo.dal.dataobject.PhotoDO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 照片服务接口
 *
 * @author interest-tracker
 */
public interface PhotoService {

    /**
     * 上传照片（包含原图和缩略图）
     *
     * @param file 文件
     * @param reqVO 上传请求
     * @return 上传结果
     */
    PhotoUploadRespVO uploadPhoto(MultipartFile file, PhotoUploadReqVO reqVO);

    /**
     * 批量上传照片
     *
     * @param files 文件列表
     * @param reqVO 上传请求
     * @return 上传结果列表
     */
    List<PhotoUploadRespVO> batchUploadPhotos(List<MultipartFile> files, PhotoUploadReqVO reqVO);

    /**
     * 更新照片信息
     *
     * @param reqVO 更新请求
     */
    void updatePhoto(PhotoUpdateReqVO reqVO);

    /**
     * 获取照片详情
     *
     * @param id 照片ID
     * @return 照片详情
     */
    PhotoRespVO getPhoto(Long id);

    /**
     * 获取照片分页列表
     *
     * @param reqVO 分页请求
     * @return 分页结果
     */
    PageResult<PhotoPageRespVO> getPhotoPage(PhotoPageReqVO reqVO);

    /**
     * 删除照片
     *
     * @param id 照片ID
     */
    void deletePhoto(Long id);

    /**
     * 获取照片列表（按关联记录）- 供其他模块调用
     *
     * @param userId 用户ID
     * @param travelRecordId 旅游记录ID（可选）
     * @param concertRecordId 观演记录ID（可选）
     * @return 照片列表
     */
    List<PhotoDO> getPhotoListByRelation(Long userId, Long travelRecordId, Long concertRecordId);

    /**
     * 批量关联照片到旅游记录 - 供其他模块调用
     *
     * @param userId 用户ID
     * @param photoIds 照片ID列表
     * @param travelRecordId 旅游记录ID
     */
    void batchLinkToTravel(Long userId, List<Long> photoIds, Long travelRecordId);

    /**
     * 批量关联照片到观演记录 - 供其他模块调用
     *
     * @param userId 用户ID
     * @param photoIds 照片ID列表
     * @param concertRecordId 观演记录ID
     */
    void batchLinkToConcert(Long userId, List<Long> photoIds, Long concertRecordId);

    /**
     * 获取照片数量（按关联记录）- 供其他模块调用
     *
     * @param userId 用户ID
     * @param travelRecordId 旅游记录ID（可选）
     * @param concertRecordId 观演记录ID（可选）
     * @return 照片数量
     */
    long getPhotoCountByRelation(Long userId, Long travelRecordId, Long concertRecordId);

}

