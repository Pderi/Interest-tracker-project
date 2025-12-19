package com.interest.tracker.module.music.dal.dataobject;

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
 * 听歌记录 DO
 *
 * 对应表：album_record
 */
@TableName("album_record")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AlbumRecordDO extends BaseDO {

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
     * 专辑ID
     */
    private Long albumId;

    /**
     * 听歌状态：1-想听 2-在听 3-已听 4-弃听
     * 枚举 {@link com.interest.tracker.module.music.enums.ListenStatusEnum}
     */
    private Integer listenStatus;

    /**
     * 个人评分（0-10）
     */
    private BigDecimal personalRating;

    /**
     * 听歌日期
     */
    private LocalDate listenDate;

    /**
     * 听歌次数
     */
    private Integer listenCount;

    /**
     * 评价
     */
    private String comment;

    /**
     * 标签（逗号分隔）
     */
    private String tags;

}

