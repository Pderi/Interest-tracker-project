package com.interest.tracker.module.photo.service.impl;

import com.interest.tracker.framework.common.pojo.PageResult;
import com.interest.tracker.framework.common.util.object.BeanUtils;
import com.interest.tracker.framework.security.core.UserContext;
import com.interest.tracker.module.photo.controller.app.vo.*;
import com.interest.tracker.module.photo.dal.dataobject.PhotoDO;
import com.interest.tracker.module.photo.dal.dataobject.PhotoCategoryDO;
import com.interest.tracker.module.photo.dal.mysql.PhotoMapper;
import com.interest.tracker.module.photo.dal.mysql.PhotoCategoryMapper;
import com.interest.tracker.module.photo.service.PhotoService;
import com.interest.tracker.module.photo.service.TencentCosService;
import com.interest.tracker.module.photo.util.ImageUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.interest.tracker.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.interest.tracker.framework.common.exception.enums.GlobalErrorCodeConstants.UNAUTHORIZED;
import static com.interest.tracker.module.photo.constants.PhotoErrorCodeConstants.*;

/**
 * 照片服务实现
 *
 * @author interest-tracker
 */
@Service
@Validated
@Slf4j
public class PhotoServiceImpl implements PhotoService {

    @Resource
    private PhotoMapper photoMapper;

    @Resource
    private PhotoCategoryMapper photoCategoryMapper;

    @Resource
    private TencentCosService tencentCosService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PhotoUploadRespVO uploadPhoto(MultipartFile file, PhotoUploadReqVO reqVO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        // 1. 文件校验
        validateFile(file);

        // 2. 提取图片信息（宽度、高度、文件大小等）
        Long fileSize = file.getSize();
        int[] dimensions = ImageUtil.getImageDimensions(file);
        Integer width = dimensions != null ? dimensions[0] : null;
        Integer height = dimensions != null ? dimensions[1] : null;

        // 3. 生成缩略图
        InputStream thumbnailInputStream = ImageUtil.generateThumbnail(file);

        // 4. 生成文件Key并上传原图到COS
        String fileKey = tencentCosService.generateFileKey(userId, file.getOriginalFilename());
        String fileUrl;
        try {
            // 使用文件的字节数组创建输入流上传原图
            byte[] fileBytes = file.getBytes();
            fileUrl = tencentCosService.uploadFile(
                    new java.io.ByteArrayInputStream(fileBytes), 
                    fileKey, 
                    file.getContentType());
            log.info("照片上传成功，Key: {}, URL: {}", fileKey, fileUrl);
        } catch (Exception e) {
            log.error("文件上传失败，Key: {}, 文件名: {}, 文件大小: {} bytes", 
                    fileKey, file.getOriginalFilename(), file.getSize(), e);
            throw exception(PHOTO_UPLOAD_FAILED);
        }

        // 5. 上传缩略图到COS
        String thumbnailUrl = null;
        if (thumbnailInputStream != null) {
            try {
                String thumbnailKey = tencentCosService.generateThumbnailKey(fileKey);
                thumbnailUrl = tencentCosService.uploadFile(thumbnailInputStream, thumbnailKey, file.getContentType());
            } catch (Exception e) {
                log.warn("缩略图上传失败，继续使用原图", e);
            }
        }

        // 4. 创建照片记录
        PhotoDO photoDO = BeanUtils.toBean(reqVO, PhotoDO.class);
        photoDO.setUserId(userId);
        photoDO.setFilePath(fileUrl);
        photoDO.setThumbnailPath(thumbnailUrl);
        photoDO.setFileSize(fileSize);
        photoDO.setWidth(width);
        photoDO.setHeight(height);
        photoDO.setMimeType(file.getContentType());
        photoDO.setStorageType(1); // 1-COS
        photoDO.setViewCount(0);
        photoDO.setLikeCount(0);
        if (reqVO.getTags() != null) {
            photoDO.setTags(String.join("|", reqVO.getTags()));
        }

        // 5. 如果指定了分类，同步分类名称
        if (reqVO.getCategoryId() != null) {
            PhotoCategoryDO category = photoCategoryMapper.selectById(reqVO.getCategoryId());
            if (category != null && category.getUserId().equals(userId)) {
                photoDO.setCategory(category.getName());
            }
        }

        photoMapper.insert(photoDO);

        // 6. 更新分类的照片数量
        if (reqVO.getCategoryId() != null) {
            updateCategoryPhotoCount(reqVO.getCategoryId(), 1);
        }

        // 7. 返回结果
        PhotoUploadRespVO respVO = BeanUtils.toBean(photoDO, PhotoUploadRespVO.class);
        respVO.setTags(splitToList(photoDO.getTags()));
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<PhotoUploadRespVO> batchUploadPhotos(List<MultipartFile> files, PhotoUploadReqVO reqVO) {
        return files.stream()
                .map(file -> uploadPhoto(file, reqVO))
                .collect(Collectors.toList());
    }

    @Override
    public String uploadCoverImage(MultipartFile file) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        // 1. 文件校验
        validateFile(file);

        // 2. 生成文件Key并上传到COS
        String fileKey = tencentCosService.generateFileKey(userId, file.getOriginalFilename());
        String fileUrl;
        try {
            byte[] fileBytes = file.getBytes();
            fileUrl = tencentCosService.uploadFile(
                    new java.io.ByteArrayInputStream(fileBytes),
                    fileKey,
                    file.getContentType());
            log.info("封面上传成功，Key: {}, URL: {}", fileKey, fileUrl);
        } catch (Exception e) {
            log.error("封面上传失败，Key: {}, 文件名: {}, 文件大小: {} bytes",
                    fileKey, file.getOriginalFilename(), file.getSize(), e);
            throw exception(PHOTO_UPLOAD_FAILED);
        }

        return fileUrl;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePhoto(PhotoUpdateReqVO reqVO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        // 1. 校验照片存在
        PhotoDO photoDO = validatePhotoExists(reqVO.getId());

        // 2. 校验权限
        if (!photoDO.getUserId().equals(userId)) {
            throw exception(PHOTO_NOT_OWNER);
        }

        // 3. 记录旧分类ID（用于更新分类统计）
        Long oldCategoryId = photoDO.getCategoryId();

        // 4. 更新照片信息
        PhotoDO updateDO = BeanUtils.toBean(reqVO, PhotoDO.class);
        if (updateDO.getTitle() != null) {
            photoDO.setTitle(updateDO.getTitle());
        }
        if (updateDO.getDescription() != null) {
            photoDO.setDescription(updateDO.getDescription());
        }
        if (reqVO.getTags() != null) {
            photoDO.setTags(String.join("|", reqVO.getTags()));
        }
        if (updateDO.getCategoryId() != null) {
            photoDO.setCategoryId(updateDO.getCategoryId());
            // 同步分类名称
            PhotoCategoryDO category = photoCategoryMapper.selectById(updateDO.getCategoryId());
            if (category != null && category.getUserId().equals(userId)) {
                photoDO.setCategory(category.getName());
            }
        }
        if (updateDO.getTravelRecordId() != null) {
            photoDO.setTravelRecordId(updateDO.getTravelRecordId());
        }
        if (updateDO.getConcertRecordId() != null) {
            photoDO.setConcertRecordId(updateDO.getConcertRecordId());
        }
        if (updateDO.getLocation() != null) {
            photoDO.setLocation(updateDO.getLocation());
        }

        photoMapper.updateById(photoDO);

        // 5. 更新分类统计
        Long newCategoryId = updateDO.getCategoryId();
        if (oldCategoryId != null && !oldCategoryId.equals(newCategoryId)) {
            updateCategoryPhotoCount(oldCategoryId, -1);
        }
        if (newCategoryId != null && !newCategoryId.equals(oldCategoryId)) {
            updateCategoryPhotoCount(newCategoryId, 1);
        }
    }

    @Override
    public PhotoRespVO getPhoto(Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        PhotoDO photoDO = validatePhotoExists(id);

        // 校验权限
        if (!photoDO.getUserId().equals(userId)) {
            throw exception(PHOTO_NOT_OWNER);
        }

        // 增加查看次数
        photoDO.setViewCount((photoDO.getViewCount() == null ? 0 : photoDO.getViewCount()) + 1);
        photoMapper.updateById(photoDO);

        PhotoRespVO respVO = BeanUtils.toBean(photoDO, PhotoRespVO.class);
        respVO.setTags(splitToList(photoDO.getTags()));
        return respVO;
    }

    @Override
    public PageResult<PhotoPageRespVO> getPhotoPage(PhotoPageReqVO reqVO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        reqVO.setUserId(userId);
        PageResult<PhotoDO> pageResult = photoMapper.selectPage(reqVO);

        // 转换为VO
        List<PhotoPageRespVO> voList = pageResult.getList().stream()
                .map(photoDO -> {
                    PhotoPageRespVO vo = BeanUtils.toBean(photoDO, PhotoPageRespVO.class);
                    vo.setTags(splitToList(photoDO.getTags()));
                    return vo;
                })
                .collect(Collectors.toList());

        return new PageResult<>(voList, pageResult.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePhoto(Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        PhotoDO photoDO = validatePhotoExists(id);

        // 校验权限
        if (!photoDO.getUserId().equals(userId)) {
            throw exception(PHOTO_NOT_OWNER);
        }

        // 删除COS文件
        if (photoDO.getFilePath() != null) {
            String fileKey = tencentCosService.extractFileKeyFromUrl(photoDO.getFilePath());
            if (fileKey != null) {
                tencentCosService.deleteFile(fileKey);
            }
        }
        if (photoDO.getThumbnailPath() != null) {
            String thumbnailKey = tencentCosService.extractFileKeyFromUrl(photoDO.getThumbnailPath());
            if (thumbnailKey != null) {
                tencentCosService.deleteFile(thumbnailKey);
            }
        }

        // 更新分类统计
        if (photoDO.getCategoryId() != null) {
            updateCategoryPhotoCount(photoDO.getCategoryId(), -1);
        }

        // 删除照片记录
        photoMapper.deleteById(id);
    }

    @Override
    public List<PhotoDO> getPhotoListByRelation(Long userId, Long travelRecordId, Long concertRecordId) {
        return photoMapper.selectListByRelation(userId, travelRecordId, concertRecordId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchLinkToTravel(Long userId, List<Long> photoIds, Long travelRecordId) {
        for (Long photoId : photoIds) {
            PhotoDO photoDO = photoMapper.selectById(photoId);
            if (photoDO != null && photoDO.getUserId().equals(userId)) {
                photoDO.setTravelRecordId(travelRecordId);
                photoMapper.updateById(photoDO);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchLinkToConcert(Long userId, List<Long> photoIds, Long concertRecordId) {
        for (Long photoId : photoIds) {
            PhotoDO photoDO = photoMapper.selectById(photoId);
            if (photoDO != null && photoDO.getUserId().equals(userId)) {
                photoDO.setConcertRecordId(concertRecordId);
                photoMapper.updateById(photoDO);
            }
        }
    }

    @Override
    public long getPhotoCountByRelation(Long userId, Long travelRecordId, Long concertRecordId) {
        return photoMapper.countByRelation(userId, travelRecordId, concertRecordId);
    }

    /**
     * 校验照片存在
     */
    private PhotoDO validatePhotoExists(Long id) {
        PhotoDO photoDO = photoMapper.selectById(id);
        if (photoDO == null) {
            throw exception(PHOTO_NOT_EXISTS);
        }
        return photoDO;
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

    /**
     * 校验文件
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw exception(PHOTO_UPLOAD_FAILED);
        }

        // 文件大小限制（10MB）
        long maxSize = 50 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw exception(PHOTO_FILE_TOO_LARGE);
        }

        // 文件类型校验
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw exception(PHOTO_FILE_TYPE_INVALID);
        }

        // 允许的图片格式
        String[] allowedTypes = {"image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"};
        boolean isAllowed = false;
        for (String allowedType : allowedTypes) {
            if (allowedType.equals(contentType)) {
                isAllowed = true;
                break;
            }
        }
        if (!isAllowed) {
            throw exception(PHOTO_FILE_TYPE_INVALID);
        }
    }

    /**
     * 更新分类的照片数量
     */
    private void updateCategoryPhotoCount(Long categoryId, int delta) {
        PhotoCategoryDO category = photoCategoryMapper.selectById(categoryId);
        if (category != null) {
            int newCount = (category.getPhotoCount() == null ? 0 : category.getPhotoCount()) + delta;
            category.setPhotoCount(Math.max(0, newCount));
            photoCategoryMapper.updateById(category);
        }
    }

}

