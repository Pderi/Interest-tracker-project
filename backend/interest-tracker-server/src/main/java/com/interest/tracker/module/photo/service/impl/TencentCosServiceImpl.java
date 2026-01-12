package com.interest.tracker.module.photo.service.impl;

import com.interest.tracker.module.photo.config.TencentCosProperties;
import com.interest.tracker.module.photo.service.TencentCosService;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static com.interest.tracker.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.interest.tracker.module.photo.constants.PhotoErrorCodeConstants.PHOTO_UPLOAD_FAILED;

/**
 * 腾讯云COS服务实现
 *
 * @author interest-tracker
 */
@Service
@Slf4j
public class TencentCosServiceImpl implements TencentCosService {

    @Resource
    private TencentCosProperties cosProperties;

    private COSClient cosClient;

    @PostConstruct
    public void init() {
        log.info("开始初始化腾讯云COS客户端...");
        log.debug("COS配置信息 - Region: {}, BucketName: {}, SecretId: {}, Domain: {}", 
                cosProperties.getRegion(), 
                cosProperties.getBucketName(),
                cosProperties.getSecretId() != null && !cosProperties.getSecretId().isEmpty() ? "已配置" : "未配置",
                cosProperties.getDomain());
        
        // 校验配置
        if (cosProperties.getSecretId() == null || cosProperties.getSecretId().isEmpty()) {
            log.error("腾讯云COS SecretId未配置，COS功能将不可用！请检查 application-cos.yml 或环境变量");
            return;
        }
        if (cosProperties.getSecretKey() == null || cosProperties.getSecretKey().isEmpty()) {
            log.error("腾讯云COS SecretKey未配置，COS功能将不可用！请检查 application-cos.yml 或环境变量");
            return;
        }
        if (cosProperties.getBucketName() == null || cosProperties.getBucketName().isEmpty()) {
            log.error("腾讯云COS BucketName未配置，COS功能将不可用！请检查 application-cos.yml 或环境变量");
            return;
        }
        if (cosProperties.getRegion() == null || cosProperties.getRegion().isEmpty()) {
            log.error("腾讯云COS Region未配置，COS功能将不可用！请检查 application-cos.yml 或环境变量");
            return;
        }

        try {
            // 初始化COS客户端
            COSCredentials cred = new BasicCOSCredentials(
                    cosProperties.getSecretId(),
                    cosProperties.getSecretKey());
            Region region = new Region(cosProperties.getRegion());
            ClientConfig clientConfig = new ClientConfig(region);
            clientConfig.setConnectionTimeout(cosProperties.getConnectionTimeout());
            clientConfig.setSocketTimeout(cosProperties.getSocketTimeout());

            cosClient = new COSClient(cred, clientConfig);
            log.info("腾讯云COS客户端初始化成功，Bucket: {}, Region: {}, Domain: {}", 
                    cosProperties.getBucketName(), 
                    cosProperties.getRegion(),
                    cosProperties.getDomain());
        } catch (Exception e) {
            log.error("腾讯云COS客户端初始化失败，Region: {}, Bucket: {}", 
                    cosProperties.getRegion(), cosProperties.getBucketName(), e);
        }
    }

    @Override
    public String uploadFile(InputStream inputStream, String fileKey, String contentType) {
        if (cosClient == null) {
            log.error("COS客户端未初始化，无法上传文件。请检查COS配置是否正确。");
            throw exception(PHOTO_UPLOAD_FAILED);
        }

        try {
            // 设置文件元数据
            ObjectMetadata metadata = new ObjectMetadata();
            if (contentType != null) {
                metadata.setContentType(contentType);
            }
            // 注意：对于ByteArrayInputStream，available()可以准确返回大小
            // 对于其他类型的InputStream，可能需要先读取内容到字节数组
            int contentLength = inputStream.available();
            if (contentLength > 0) {
                metadata.setContentLength(contentLength);
            }

            // 创建上传请求
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    cosProperties.getBucketName(),
                    fileKey,
                    inputStream,
                    metadata);

            // 执行上传
            cosClient.putObject(putObjectRequest);

            // 生成访问URL
            String fileUrl = generateFileUrl(fileKey);
            log.debug("文件上传成功，Key: {}, URL: {}", fileKey, fileUrl);
            return fileUrl;
        } catch (CosServiceException e) {
            log.error("COS服务异常，上传失败。Key: {}, ErrorCode: {}, ErrorMessage: {}, Bucket: {}", 
                    fileKey, e.getErrorCode(), e.getErrorMessage(), cosProperties.getBucketName(), e);
            throw exception(PHOTO_UPLOAD_FAILED);
        } catch (CosClientException e) {
            log.error("COS客户端异常，上传失败。Key: {}, Bucket: {}, Region: {}", 
                    fileKey, cosProperties.getBucketName(), cosProperties.getRegion(), e);
            throw exception(PHOTO_UPLOAD_FAILED);
        } catch (Exception e) {
            log.error("文件上传失败。Key: {}, Bucket: {}, Region: {}, ContentType: {}, 异常类型: {}", 
                    fileKey, cosProperties.getBucketName(), cosProperties.getRegion(), contentType, 
                    e.getClass().getName(), e);
            throw exception(PHOTO_UPLOAD_FAILED);
        }
    }

    @Override
    public void deleteFile(String fileKey) {
        if (cosClient == null) {
            log.warn("COS客户端未初始化，跳过删除操作。Key: {}", fileKey);
            return;
        }

        try {
            cosClient.deleteObject(cosProperties.getBucketName(), fileKey);
            log.debug("文件删除成功，Key: {}", fileKey);
        } catch (CosServiceException e) {
            log.error("COS服务异常，删除失败。Key: {}, ErrorCode: {}, ErrorMessage: {}", 
                    fileKey, e.getErrorCode(), e.getErrorMessage(), e);
        } catch (CosClientException e) {
            log.error("COS客户端异常，删除失败。Key: {}", fileKey, e);
        } catch (Exception e) {
            log.error("文件删除失败。Key: {}", fileKey, e);
        }
    }

    @Override
    public String generateFileKey(Long userId, String originalFileName) {
        // 生成文件路径：photos/{userId}/{yyyy/MM/dd}/{uuid}_{originalFileName}
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String fileName = uuid + "_" + originalFileName;
        
        return String.format("%s%d/%s/%s", 
                cosProperties.getPathPrefix(), userId, datePath, fileName);
    }

    @Override
    public String generateThumbnailKey(String fileKey) {
        // 将原图路径转换为缩略图路径
        // 例如：photos/1/2025/01/15/uuid_photo.jpg -> thumbnails/1/2025/01/15/uuid_photo.jpg
        if (fileKey.startsWith(cosProperties.getPathPrefix())) {
            return fileKey.replace(cosProperties.getPathPrefix(), cosProperties.getThumbnailPrefix());
        }
        // 如果路径格式不符合预期，直接在文件名前加前缀
        int lastSlashIndex = fileKey.lastIndexOf("/");
        if (lastSlashIndex > 0) {
            return fileKey.substring(0, lastSlashIndex + 1) + 
                   cosProperties.getThumbnailPrefix() + 
                   fileKey.substring(lastSlashIndex + 1);
        }
        return cosProperties.getThumbnailPrefix() + fileKey;
    }

    @Override
    public String extractFileKeyFromUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return null;
        }
        
        // 从URL中提取文件Key
        // 例如：https://bucket.cos.region.myqcloud.com/photos/1/2025/01/15/file.jpg
        // 提取：photos/1/2025/01/15/file.jpg
        
        String domain = cosProperties.getDomain();
        if (domain != null && fileUrl.contains(domain)) {
            int domainIndex = fileUrl.indexOf(domain);
            if (domainIndex >= 0) {
                String key = fileUrl.substring(domainIndex + domain.length());
                // 移除开头的斜杠
                if (key.startsWith("/")) {
                    key = key.substring(1);
                }
                return key;
            }
        }
        
        // 如果无法从域名提取，尝试从URL路径中提取
        int lastSlashIndex = fileUrl.lastIndexOf("/");
        if (lastSlashIndex >= 0) {
            return fileUrl.substring(lastSlashIndex + 1);
        }
        
        return fileUrl;
    }

    /**
     * 生成文件访问URL
     *
     * @param fileKey 文件Key
     * @return 文件访问URL
     */
    private String generateFileUrl(String fileKey) {
        String domain = cosProperties.getDomain();
        if (domain == null || domain.isEmpty()) {
            // 如果没有配置域名，使用默认格式
            return String.format("https://%s.cos.%s.myqcloud.com/%s",
                    cosProperties.getBucketName(),
                    cosProperties.getRegion(),
                    fileKey);
        }
        
        // 确保域名格式正确
        if (!domain.startsWith("http://") && !domain.startsWith("https://")) {
            domain = "https://" + domain;
        }
        
        // 移除域名末尾的斜杠
        if (domain.endsWith("/")) {
            domain = domain.substring(0, domain.length() - 1);
        }
        
        return domain + "/" + fileKey;
    }

}

