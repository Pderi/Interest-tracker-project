package com.interest.tracker.module.photo.controller.app;

import com.interest.tracker.framework.common.pojo.CommonResult;
import com.interest.tracker.module.photo.controller.app.vo.PhotoCategoryCreateReqVO;
import com.interest.tracker.module.photo.controller.app.vo.PhotoCategoryRespVO;
import com.interest.tracker.module.photo.controller.app.vo.PhotoCategoryUpdateReqVO;
import com.interest.tracker.module.photo.service.PhotoCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.interest.tracker.framework.common.pojo.CommonResult.success;

/**
 * 照片分类 App - 分类管理接口
 *
 * @author interest-tracker
 */
@Tag(name = "照片分类 App - 分类管理")
@RestController
@RequestMapping("/api/photos/categories")
@Validated
public class PhotoCategoryAppController {

    @Resource
    private PhotoCategoryService photoCategoryService;

    /**
     * 创建分类
     */
    @PostMapping
    @Operation(summary = "创建照片分类")
    public CommonResult<PhotoCategoryRespVO> createCategory(
            @Valid @RequestBody PhotoCategoryCreateReqVO reqVO) {
        PhotoCategoryRespVO respVO = photoCategoryService.createCategory(reqVO);
        return success(respVO);
    }

    /**
     * 更新分类
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新照片分类")
    @Parameter(name = "id", description = "分类ID", required = true)
    public CommonResult<Boolean> updateCategory(
            @PathVariable("id") Long id,
            @Valid @RequestBody PhotoCategoryUpdateReqVO reqVO) {
        reqVO.setId(id);
        photoCategoryService.updateCategory(reqVO);
        return success(true);
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除照片分类")
    @Parameter(name = "id", description = "分类ID", required = true)
    public CommonResult<Boolean> deleteCategory(@PathVariable("id") Long id) {
        photoCategoryService.deleteCategory(id);
        return success(true);
    }

    /**
     * 获取分类详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取分类详情")
    @Parameter(name = "id", description = "分类ID", required = true)
    public CommonResult<PhotoCategoryRespVO> getCategory(@PathVariable("id") Long id) {
        PhotoCategoryRespVO respVO = photoCategoryService.getCategory(id);
        return success(respVO);
    }

    /**
     * 获取用户的所有分类列表
     */
    @GetMapping
    @Operation(summary = "获取用户的所有分类列表")
    public CommonResult<List<PhotoCategoryRespVO>> getCategoryList() {
        List<PhotoCategoryRespVO> result = photoCategoryService.getCategoryList();
        return success(result);
    }

    /**
     * 更新分类排序
     */
    @PutMapping("/sort")
    @Operation(summary = "更新分类排序")
    public CommonResult<Boolean> updateCategorySort(@RequestBody List<Long> categoryIds) {
        photoCategoryService.updateCategorySort(categoryIds);
        return success(true);
    }

}

