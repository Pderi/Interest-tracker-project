package com.interest.tracker.module.photo.service;

import java.io.InputStream;

/**
 * 腾讯云COS服务接口
 *
 * @author interest-tracker
 */
public interface TencentCosService {

    /**
     * 上传文件到COS
     *
     * @param inputStream 文件输入流
     * @param fileKey 文件Key（路径）
     * @param contentType 文件类型
     * @return 文件访问URL
     */
    String uploadFile(InputStream inputStream, String fileKey, String contentType);

    /**
     * 删除COS文件
     *
     * @param fileKey 文件Key（路径）
     */
    void deleteFile(String fileKey);

    /**
     * 生成文件Key（路径）
     *
     * @param userId 用户ID
     * @param originalFileName 原始文件名
     * @return 文件Key
     */
    String generateFileKey(Long userId, String originalFileName);

    /**
     * 生成缩略图Key（路径）
     *
     * @param fileKey 原图Key
     * @return 缩略图Key
     */
    String generateThumbnailKey(String fileKey);

    /**
     * 从文件URL中提取文件Key
     *
     * @param fileUrl 文件URL
     * @return 文件Key
     */
    String extractFileKeyFromUrl(String fileUrl);

}

