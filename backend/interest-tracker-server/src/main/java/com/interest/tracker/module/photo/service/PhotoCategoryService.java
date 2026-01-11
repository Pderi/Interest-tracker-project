package com.interest.tracker.module.photo.service;

import com.interest.tracker.module.photo.controller.app.vo.PhotoCategoryCreateReqVO;
import com.interest.tracker.module.photo.controller.app.vo.PhotoCategoryRespVO;
import com.interest.tracker.module.photo.controller.app.vo.PhotoCategoryUpdateReqVO;

import java.util.List;

/**
 * 照片分类服务接口
 *
 * @author interest-tracker
 */
public interface PhotoCategoryService {

    /**
     * 创建分类
     *
     * @param reqVO 创建请求
     * @return 分类信息
     */
    PhotoCategoryRespVO createCategory(PhotoCategoryCreateReqVO reqVO);

    /**
     * 更新分类
     *
     * @param reqVO 更新请求
     */
    void updateCategory(PhotoCategoryUpdateReqVO reqVO);

    /**
     * 删除分类
     *
     * @param id 分类ID
     */
    void deleteCategory(Long id);

    /**
     * 获取分类详情
     *
     * @param id 分类ID
     * @return 分类信息
     */
    PhotoCategoryRespVO getCategory(Long id);

    /**
     * 获取用户的所有分类列表
     *
     * @return 分类列表
     */
    List<PhotoCategoryRespVO> getCategoryList();

    /**
     * 更新分类排序
     *
     * @param categoryIds 分类ID列表（按顺序排列）
     */
    void updateCategorySort(List<Long> categoryIds);

}

