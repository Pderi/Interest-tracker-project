package com.interest.tracker.module.music.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.interest.tracker.framework.mybatis.core.dataobject.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 专辑 DO
 *
 * 对应表：album
 */
@TableName("album")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AlbumDO extends BaseDO {

    /**
     * 专辑ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 专辑名称
     */
    private String title;

    /**
     * 艺术家/乐队
     */
    private String artist;

    /**
     * 发行年份
     */
    private Integer releaseYear;

    /**
     * 音乐类型（摇滚、流行等，逗号分隔）
     */
    private String genre;

    /**
     * 专辑简介
     */
    private String description;

    /**
     * 封面URL
     */
    private String coverUrl;

    /**
     * 总曲目数
     */
    private Integer totalTracks;

    /**
     * 总时长（秒）
     */
    private Integer duration;

}

