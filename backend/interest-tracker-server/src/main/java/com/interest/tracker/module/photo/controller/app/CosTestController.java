package com.interest.tracker.module.photo.controller.app;

import com.interest.tracker.framework.common.pojo.CommonResult;
import com.interest.tracker.module.photo.config.TencentCosProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static com.interest.tracker.framework.common.pojo.CommonResult.success;

/**
 * COS配置测试接口（用于诊断配置问题）
 *
 * @author interest-tracker
 */
@Tag(name = "COS测试 - 配置诊断")
@RestController
@RequestMapping("/api/test/cos")
public class CosTestController {

    @Resource
    private TencentCosProperties cosProperties;

    /**
     * 检查COS配置
     */
    @GetMapping("/config")
    @Operation(summary = "检查COS配置（不返回敏感信息）")
    public CommonResult<Map<String, Object>> checkCosConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("region", cosProperties.getRegion());
        config.put("bucketName", cosProperties.getBucketName());
        config.put("domain", cosProperties.getDomain());
        config.put("pathPrefix", cosProperties.getPathPrefix());
        config.put("thumbnailPrefix", cosProperties.getThumbnailPrefix());
        config.put("connectionTimeout", cosProperties.getConnectionTimeout());
        config.put("socketTimeout", cosProperties.getSocketTimeout());
        
        // 检查配置是否完整（不返回实际值）
        config.put("secretIdConfigured", cosProperties.getSecretId() != null && !cosProperties.getSecretId().isEmpty());
        config.put("secretKeyConfigured", cosProperties.getSecretKey() != null && !cosProperties.getSecretKey().isEmpty());
        config.put("allConfigured", 
                cosProperties.getSecretId() != null && !cosProperties.getSecretId().isEmpty() &&
                cosProperties.getSecretKey() != null && !cosProperties.getSecretKey().isEmpty() &&
                cosProperties.getBucketName() != null && !cosProperties.getBucketName().isEmpty() &&
                cosProperties.getRegion() != null && !cosProperties.getRegion().isEmpty());
        
        return success(config);
    }

}

