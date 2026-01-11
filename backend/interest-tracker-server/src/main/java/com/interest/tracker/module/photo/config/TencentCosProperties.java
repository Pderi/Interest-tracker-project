package com.interest.tracker.module.photo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 腾讯云COS配置
 * 
 * 配置说明：
 * 1. 在 application.yml 中配置 tencent.cos 相关参数
 * 2. 具体配置步骤请参考：backend/docs/腾讯云COS配置指南.md
 *
 * @author interest-tracker
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "tencent.cos")
public class TencentCosProperties {

    /**
     * 地域
     */
    private String region;

    /**
     * SecretId
     */
    private String secretId;

    /**
     * SecretKey
     */
    private String secretKey;

    /**
     * 存储桶名称
     */
    private String bucketName;

    /**
     * 访问域名
     */
    private String domain;

    /**
     * 文件路径前缀
     */
    private String pathPrefix = "photos/";

    /**
     * 缩略图路径前缀
     */
    private String thumbnailPrefix = "thumbnails/";

    /**
     * 连接超时时间（毫秒）
     */
    private Integer connectionTimeout = 30000;

    /**
     * Socket超时时间（毫秒）
     */
    private Integer socketTimeout = 30000;

}

