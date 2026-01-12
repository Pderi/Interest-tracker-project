package com.interest.tracker.module.concert.dal.dataobject;

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
 * 观演记录 DO
 *
 * 对应表：concert_record
 */
@TableName("concert_record")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ConcertRecordDO extends BaseDO {

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
     * 演唱会ID
     */
    private Long concertId;

    /**
     * 观演状态：1-想看 2-已看
     * 枚举 {@link com.interest.tracker.module.concert.enums.WatchStatusEnum}
     */
    private Integer watchStatus;

    /**
     * 个人评分（0-10）
     */
    private BigDecimal personalRating;

    /**
     * 观演日期
     */
    private LocalDate watchDate;

    /**
     * 票价
     */
    private BigDecimal ticketPrice;

    /**
     * 座位信息
     */
    private String seatInfo;

    /**
     * 评价
     */
    private String comment;

    /**
     * 标签（逗号分隔）
     */
    private String tags;

}

