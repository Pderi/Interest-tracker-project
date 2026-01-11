package com.interest.tracker.module.photo.service.impl;

import com.interest.tracker.framework.common.util.object.BeanUtils;
import com.interest.tracker.framework.security.core.UserContext;
import com.interest.tracker.module.photo.controller.app.vo.PhotoCategoryCreateReqVO;
import com.interest.tracker.module.photo.controller.app.vo.PhotoCategoryRespVO;
import com.interest.tracker.module.photo.controller.app.vo.PhotoCategoryUpdateReqVO;
import com.interest.tracker.module.photo.dal.dataobject.PhotoCategoryDO;
import com.interest.tracker.module.photo.dal.mysql.PhotoCategoryMapper;
import com.interest.tracker.module.photo.service.PhotoCategoryService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

import static com.interest.tracker.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.interest.tracker.framework.common.exception.enums.GlobalErrorCodeConstants.UNAUTHORIZED;
import static com.interest.tracker.module.photo.constants.PhotoErrorCodeConstants.*;

/**
 * 照片分类服务实现
 *
 * @author interest-tracker
 */
@Service
@Validated
@Slf4j
public class PhotoCategoryServiceImpl implements PhotoCategoryService {

    /**
     * 每个用户最多创建的分类数量
     */
    private static final int MAX_CATEGORY_COUNT = 20;

    @Resource
    private PhotoCategoryMapper photoCategoryMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PhotoCategoryRespVO createCategory(PhotoCategoryCreateReqVO reqVO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        // 1. 检查分类数量限制
        long categoryCount = photoCategoryMapper.countByUserId(userId);
        if (categoryCount >= MAX_CATEGORY_COUNT) {
            throw exception(PHOTO_CATEGORY_LIMIT_EXCEEDED);
        }

        // 2. 检查分类名称是否已存在
        PhotoCategoryDO existCategory = photoCategoryMapper.selectByUserIdAndName(userId, reqVO.getName());
        if (existCategory != null) {
            throw exception(PHOTO_CATEGORY_NAME_EXISTS);
        }

        // 3. 创建分类
        PhotoCategoryDO categoryDO = BeanUtils.toBean(reqVO, PhotoCategoryDO.class);
        categoryDO.setUserId(userId);

        // 4. 设置排序顺序
        if (categoryDO.getSortOrder() == null) {
            Integer maxSortOrder = photoCategoryMapper.selectMaxSortOrder(userId);
            categoryDO.setSortOrder(maxSortOrder != null ? maxSortOrder + 1 : 0);
        }

        categoryDO.setPhotoCount(0);
        photoCategoryMapper.insert(categoryDO);

        return BeanUtils.toBean(categoryDO, PhotoCategoryRespVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategory(PhotoCategoryUpdateReqVO reqVO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        // 1. 校验分类存在
        PhotoCategoryDO categoryDO = validateCategoryExists(reqVO.getId());

        // 2. 校验权限
        if (!categoryDO.getUserId().equals(userId)) {
            throw exception(PHOTO_CATEGORY_NOT_OWNER);
        }

        // 3. 如果更新了分类名称，检查是否重复
        if (reqVO.getName() != null && !reqVO.getName().equals(categoryDO.getName())) {
            PhotoCategoryDO existCategory = photoCategoryMapper.selectByUserIdAndName(userId, reqVO.getName());
            if (existCategory != null && !existCategory.getId().equals(reqVO.getId())) {
                throw exception(PHOTO_CATEGORY_NAME_EXISTS);
            }
        }

        // 4. 更新分类信息
        PhotoCategoryDO updateDO = BeanUtils.toBean(reqVO, PhotoCategoryDO.class);
        if (updateDO.getName() != null) {
            categoryDO.setName(updateDO.getName());
        }
        if (updateDO.getColor() != null) {
            categoryDO.setColor(updateDO.getColor());
        }
        if (updateDO.getIcon() != null) {
            categoryDO.setIcon(updateDO.getIcon());
        }
        if (updateDO.getDescription() != null) {
            categoryDO.setDescription(updateDO.getDescription());
        }
        if (updateDO.getSortOrder() != null) {
            categoryDO.setSortOrder(updateDO.getSortOrder());
        }

        photoCategoryMapper.updateById(categoryDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        // 1. 校验分类存在且属于当前用户
        PhotoCategoryDO categoryDO = validateCategoryExists(id);

        // 2. 校验权限
        if (!categoryDO.getUserId().equals(userId)) {
            throw exception(PHOTO_CATEGORY_NOT_OWNER);
        }

        // 3. 检查该分类下是否有照片
        if (categoryDO.getPhotoCount() != null && categoryDO.getPhotoCount() > 0) {
            throw exception(PHOTO_CATEGORY_HAS_PHOTOS);
        }

        // 4. 删除分类
        photoCategoryMapper.deleteById(id);
    }

    @Override
    public PhotoCategoryRespVO getCategory(Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        PhotoCategoryDO categoryDO = validateCategoryExists(id);

        // 校验权限
        if (!categoryDO.getUserId().equals(userId)) {
            throw exception(PHOTO_CATEGORY_NOT_OWNER);
        }

        return BeanUtils.toBean(categoryDO, PhotoCategoryRespVO.class);
    }

    @Override
    public List<PhotoCategoryRespVO> getCategoryList() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        List<PhotoCategoryDO> categoryList = photoCategoryMapper.selectListByUserId(userId);
        return categoryList.stream()
                .map(categoryDO -> BeanUtils.toBean(categoryDO, PhotoCategoryRespVO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategorySort(List<Long> categoryIds) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        if (categoryIds == null || categoryIds.isEmpty()) {
            return;
        }

        // 批量更新排序
        for (int i = 0; i < categoryIds.size(); i++) {
            PhotoCategoryDO categoryDO = photoCategoryMapper.selectById(categoryIds.get(i));
            if (categoryDO != null && categoryDO.getUserId().equals(userId)) {
                categoryDO.setSortOrder(i);
                photoCategoryMapper.updateById(categoryDO);
            }
        }
    }

    /**
     * 校验分类存在
     */
    private PhotoCategoryDO validateCategoryExists(Long id) {
        PhotoCategoryDO categoryDO = photoCategoryMapper.selectById(id);
        if (categoryDO == null) {
            throw exception(PHOTO_CATEGORY_NOT_EXISTS);
        }
        return categoryDO;
    }

}

