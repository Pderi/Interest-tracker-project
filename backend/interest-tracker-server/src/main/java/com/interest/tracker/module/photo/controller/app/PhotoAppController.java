package com.interest.tracker.module.photo.controller.app;

import com.interest.tracker.framework.common.pojo.CommonResult;
import com.interest.tracker.framework.common.pojo.PageResult;
import com.interest.tracker.module.photo.controller.app.vo.*;
import com.interest.tracker.module.photo.service.PhotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

import static com.interest.tracker.framework.common.pojo.CommonResult.success;

/**
 * 照片 App - 照片管理接口
 *
 * @author interest-tracker
 */
@Tag(name = "照片 App - 照片管理")
@RestController
@RequestMapping("/api/photos")
@Validated
public class PhotoAppController {

    @Resource
    private PhotoService photoService;

    /**
     * 上传照片
     */
    @PostMapping("/upload")
    @Operation(summary = "上传照片")
    public CommonResult<PhotoUploadRespVO> uploadPhoto(
            @RequestParam("file") MultipartFile file,
            @ModelAttribute @Valid PhotoUploadReqVO reqVO) {
        PhotoUploadRespVO respVO = photoService.uploadPhoto(file, reqVO);
        return success(respVO);
    }

    /**
     * 批量上传照片
     */
    @PostMapping("/batch-upload")
    @Operation(summary = "批量上传照片")
    public CommonResult<List<PhotoUploadRespVO>> batchUploadPhotos(
            @RequestParam("files") MultipartFile[] files,
            @ModelAttribute @Valid PhotoUploadReqVO reqVO) {
        List<PhotoUploadRespVO> result = photoService.batchUploadPhotos(
                Arrays.asList(files), reqVO);
        return success(result);
    }

    /**
     * 获取照片详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取照片详情")
    @Parameter(name = "id", description = "照片ID", required = true)
    public CommonResult<PhotoRespVO> getPhoto(@PathVariable("id") Long id) {
        PhotoRespVO respVO = photoService.getPhoto(id);
        return success(respVO);
    }

    /**
     * 更新照片信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新照片信息")
    @Parameter(name = "id", description = "照片ID", required = true)
    public CommonResult<Boolean> updatePhoto(
            @PathVariable("id") Long id,
            @Valid @RequestBody PhotoUpdateReqVO reqVO) {
        reqVO.setId(id);
        photoService.updatePhoto(reqVO);
        return success(true);
    }

    /**
     * 删除照片
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除照片")
    @Parameter(name = "id", description = "照片ID", required = true)
    public CommonResult<Boolean> deletePhoto(@PathVariable("id") Long id) {
        photoService.deletePhoto(id);
        return success(true);
    }

    /**
     * 获取照片列表（分页，支持跨模块筛选）
     */
    @GetMapping
    @Operation(summary = "获取照片列表")
    public CommonResult<PageResult<PhotoPageRespVO>> getPhotoPage(@Valid PhotoPageReqVO reqVO) {
        PageResult<PhotoPageRespVO> result = photoService.getPhotoPage(reqVO);
        return success(result);
    }

    /**
     * 上传封面图片（仅上传文件，不创建照片记录）
     * 供其他模块（影视、音乐、图书）使用
     */
    @PostMapping("/upload-cover")
    @Operation(summary = "上传封面图片")
    public CommonResult<String> uploadCoverImage(@RequestParam("file") MultipartFile file) {
        String fileUrl = photoService.uploadCoverImage(file);
        return success(fileUrl);
    }

}

