package com.interest.tracker.module.book.dal.dataobject;

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
 * 阅读记录 DO
 *
 * 对应表：book_record
 */
@TableName("book_record")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BookRecordDO extends BaseDO {

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
     * 书籍ID
     */
    private Long bookId;

    /**
     * 阅读状态：1-想读 2-在读 3-已读 4-弃读
     * 枚举 {@link com.interest.tracker.module.book.enums.ReadStatusEnum}
     */
    private Integer readStatus;

    /**
     * 个人评分（0-10）
     */
    private BigDecimal personalRating;

    /**
     * 阅读日期
     */
    private LocalDate readDate;

    /**
     * 阅读进度（0-100）
     */
    private BigDecimal readProgress;

    /**
     * 评价
     */
    private String comment;

    /**
     * 标签（逗号分隔）
     */
    private String tags;

}

