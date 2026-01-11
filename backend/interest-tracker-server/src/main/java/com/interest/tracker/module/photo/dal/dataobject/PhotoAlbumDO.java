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
 * 相册 DO
 *
 * 对应表：photo_album
 *
 * @author interest-tracker
 */
@TableName("photo_album")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PhotoAlbumDO extends BaseDO {

    /**
     * 相册ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 相册名称
     */
    private String name;

    /**
     * 相册描述
     */
    private String description;

    /**
     * 封面照片ID
     */
    @TableField("cover_photo_id")
    private Long coverPhotoId;

    /**
     * 照片数量
     */
    @TableField("photo_count")
    private Integer photoCount;

}

