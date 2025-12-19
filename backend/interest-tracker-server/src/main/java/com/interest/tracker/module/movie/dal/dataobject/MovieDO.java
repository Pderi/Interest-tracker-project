package com.interest.tracker.module.movie.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.interest.tracker.framework.mybatis.core.dataobject.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 影视 DO
 *
 * 对应表：movie
 */
@TableName("movie")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MovieDO extends BaseDO {

    /**
     * 影视ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * TMDB ID（用于关联TMDB数据）
     */
    private Long tmdbId;

    /**
     * 标题
     */
    private String title;

    /**
     * 类型：1-电影 2-电视剧
     * 枚举 {@link com.interest.tracker.module.movie.enums.MovieTypeEnum}
     */
    private Integer type;

    /**
     * 类型（动作、科幻等，逗号分隔）
     */
    private String genre;

    /**
     * 上映年份
     */
    private Integer releaseYear;

    /**
     * 导演
     */
    private String director;

    /**
     * 演员（逗号分隔）
     */
    private String actors;

    /**
     * 简介
     */
    private String description;

    /**
     * 海报URL
     */
    private String posterUrl;

    /**
     * 评分（0-10）
     */
    private BigDecimal rating;

    /**
     * 时长（分钟）
     */
    private Integer duration;

}

