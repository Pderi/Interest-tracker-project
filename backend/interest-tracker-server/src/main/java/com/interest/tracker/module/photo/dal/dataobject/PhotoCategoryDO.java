package com.interest.tracker.module.photo.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.interest.tracker.framework.mybatis.core.dataobject.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 照片分类 DO
 *
 * 对应表：photo_category
 *
 * @author interest-tracker
 */
@TableName("photo_category")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PhotoCategoryDO extends BaseDO {

    /**
     * 分类ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类颜色（十六进制，如：#FF5733）
     */
    private String color;

    /**
     * 分类图标（可选，如：camera、nature等）
     */
    private String icon;

    /**
     * 分类描述
     */
    private String description;

    /**
     * 排序顺序（数字越小越靠前）
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 该分类下的照片数量（冗余字段，便于统计）
     */
    @TableField("photo_count")
    private Integer photoCount;

}

