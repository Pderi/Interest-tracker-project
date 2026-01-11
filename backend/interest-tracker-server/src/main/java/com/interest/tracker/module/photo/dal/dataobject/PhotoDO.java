package com.interest.tracker.module.photo.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.interest.tracker.framework.mybatis.core.dataobject.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 照片 DO
 *
 * 对应表：photo
 *
 * @author interest-tracker
 */
@TableName("photo")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PhotoDO extends BaseDO {

    /**
     * 照片ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 照片标题
     */
    private String title;

    /**
     * 照片描述
     */
    private String description;

    /**
     * 文件路径（COS URL）
     */
    @TableField("file_path")
    private String filePath;

    /**
     * 缩略图路径（COS URL）
     */
    @TableField("thumbnail_path")
    private String thumbnailPath;

    /**
     * 文件大小（字节）
     */
    @TableField("file_size")
    private Long fileSize;

    /**
     * 图片宽度（像素）
     */
    private Integer width;

    /**
     * 图片高度（像素）
     */
    private Integer height;

    /**
     * MIME类型
     */
    @TableField("mime_type")
    private String mimeType;

    /**
     * 存储类型：1-COS 2-本地
     */
    @TableField("storage_type")
    private Integer storageType;

    /**
     * EXIF数据（JSON格式）
     */
    @TableField("exif_data")
    private String exifData;

    /**
     * 拍摄地点
     */
    private String location;

    /**
     * 标签（逗号分隔）
     */
    private String tags;

    /**
     * 分类（冗余字段，便于查询）
     */
    private String category;

    /**
     * 分类ID（关联photo_category表）
     */
    @TableField("category_id")
    private Long categoryId;

    /**
     * 拍摄时间
     */
    @TableField("shoot_time")
    private LocalDateTime shootTime;

    /**
     * 关联的旅游记录ID
     */
    @TableField("travel_record_id")
    private Long travelRecordId;

    /**
     * 关联的观演记录ID
     */
    @TableField("concert_record_id")
    private Long concertRecordId;

    /**
     * 查看次数
     */
    @TableField("view_count")
    private Integer viewCount;

    /**
     * 点赞次数
     */
    @TableField("like_count")
    private Integer likeCount;

}

