package com.interest.tracker.module.travel.dal.dataobject;

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
 * 旅游记录 DO
 *
 * 对应表：travel_record
 */
@TableName("travel_record")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TravelRecordDO extends BaseDO {

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
     * 地点ID
     */
    private Long placeId;

    /**
     * 旅游状态：1-想去 2-计划中 3-已去
     * 枚举 {@link com.interest.tracker.module.travel.enums.TravelStatusEnum}
     */
    private Integer travelStatus;

    /**
     * 个人评分（0-10）
     */
    private BigDecimal personalRating;

    /**
     * 旅游日期
     */
    private LocalDate travelDate;

    /**
     * 旅游天数
     */
    private Integer travelDuration;

    /**
     * 费用
     */
    private BigDecimal expense;

    /**
     * 评价
     */
    private String comment;

    /**
     * 标签（逗号分隔）
     */
    private String tags;

}

