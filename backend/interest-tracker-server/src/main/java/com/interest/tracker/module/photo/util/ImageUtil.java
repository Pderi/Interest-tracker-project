package com.interest.tracker.module.photo.util;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 图片处理工具类
 *
 * @author interest-tracker
 */
@Slf4j
public class ImageUtil {

    /**
     * 缩略图最大宽度
     */
    private static final int THUMBNAIL_MAX_WIDTH = 800;

    /**
     * 缩略图最大高度
     */
    private static final int THUMBNAIL_MAX_HEIGHT = 800;

    /**
     * 缩略图质量（0.0-1.0）
     */
    private static final double THUMBNAIL_QUALITY = 0.85;

    /**
     * 生成缩略图
     *
     * @param file 原始图片文件
     * @return 缩略图输入流
     */
    public static InputStream generateThumbnail(MultipartFile file) {
        try {
            // 读取文件字节数组（可以多次使用）
            byte[] fileBytes = file.getBytes();
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(fileBytes));
            if (originalImage == null) {
                log.warn("无法读取图片，文件可能不是有效的图片格式");
                return null;
            }

            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();

            // 如果原图尺寸小于缩略图尺寸，直接返回原图字节数组
            if (originalWidth <= THUMBNAIL_MAX_WIDTH && originalHeight <= THUMBNAIL_MAX_HEIGHT) {
                return new ByteArrayInputStream(fileBytes);
            }

            // 计算缩略图尺寸（保持宽高比）
            int thumbnailWidth = originalWidth;
            int thumbnailHeight = originalHeight;
            
            if (originalWidth > THUMBNAIL_MAX_WIDTH || originalHeight > THUMBNAIL_MAX_HEIGHT) {
                double widthRatio = (double) THUMBNAIL_MAX_WIDTH / originalWidth;
                double heightRatio = (double) THUMBNAIL_MAX_HEIGHT / originalHeight;
                double ratio = Math.min(widthRatio, heightRatio);
                
                thumbnailWidth = (int) (originalWidth * ratio);
                thumbnailHeight = (int) (originalHeight * ratio);
            }

            // 生成缩略图
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Thumbnails.of(originalImage)
                    .size(thumbnailWidth, thumbnailHeight)
                    .outputQuality(THUMBNAIL_QUALITY)
                    .outputFormat(getImageFormat(file.getContentType()))
                    .toOutputStream(outputStream);

            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException e) {
            log.error("生成缩略图失败", e);
            return null;
        }
    }

    /**
     * 获取图片尺寸信息
     *
     * @param file 图片文件
     * @return 图片尺寸信息 [width, height]，如果获取失败返回null
     */
    public static int[] getImageDimensions(MultipartFile file) {
        try {
            byte[] fileBytes = file.getBytes();
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(fileBytes));
            if (image != null) {
                return new int[]{image.getWidth(), image.getHeight()};
            }
        } catch (IOException e) {
            log.error("获取图片尺寸失败", e);
        }
        return null;
    }

    /**
     * 获取图片格式（用于Thumbnailator）
     *
     * @param contentType MIME类型
     * @return 图片格式
     */
    private static String getImageFormat(String contentType) {
        if (contentType == null) {
            return "jpg";
        }
        
        if (contentType.contains("jpeg") || contentType.contains("jpg")) {
            return "jpg";
        } else if (contentType.contains("png")) {
            return "png";
        } else if (contentType.contains("gif")) {
            return "gif";
        } else if (contentType.contains("webp")) {
            return "webp";
        }
        
        return "jpg"; // 默认使用jpg格式
    }

}

