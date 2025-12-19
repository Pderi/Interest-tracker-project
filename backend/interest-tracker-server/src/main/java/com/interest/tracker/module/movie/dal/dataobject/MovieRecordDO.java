package com.interest.tracker.module.movie.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.interest.tracker.framework.mybatis.core.dataobject.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 观看记录 DO
 *
 * 对应表：movie_record
 */
@TableName("movie_record")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MovieRecordDO extends BaseDO {

    /**
     * 记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 影视ID
     */
    private Long movieId;

    /**
     * 观看状态：1-想看 2-在看 3-已看 4-弃剧
     * 枚举 {@link com.interest.tracker.module.movie.enums.WatchStatusEnum}
     */
    private Integer watchStatus;

    /**
     * 个人评分（0-10）
     */
    private BigDecimal personalRating;

    /**
     * 观看日期
     */
    private LocalDate watchDate;

    /**
     * 观看时长（分钟）
     */
    private Integer watchDuration;

    /**
     * 观看进度（0-100）
     */
    private BigDecimal progress;

    /**
     * 评价
     */
    private String comment;

    /**
     * 标签（逗号分隔）
     */
    private String tags;

}

