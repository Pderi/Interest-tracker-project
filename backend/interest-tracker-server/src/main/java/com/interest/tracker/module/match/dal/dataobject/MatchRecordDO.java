package com.interest.tracker.module.match.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.interest.tracker.framework.mybatis.core.dataobject.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;

/**
 * 比赛记录 DO
 *
 * 对应表：match_record
 */
@TableName("match_record")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MatchRecordDO extends BaseDO {

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
     * 主队名称
     */
    private String homeTeamName;

    /**
     * 客队名称
     */
    private String awayTeamName;

    /**
     * 比赛日期
     */
    private LocalDate matchDate;

    /**
     * 主队得分
     */
    private Integer homeScore;

    /**
     * 客队得分
     */
    private Integer awayScore;

    /**
     * 比赛类型：1-联赛 2-杯赛 3-友谊赛
     * 枚举 {@link com.interest.tracker.module.match.enums.MatchTypeEnum}
     */
    private Integer matchType;

    /**
     * 观赛方式：1-现场 2-直播 3-回放
     * 枚举 {@link com.interest.tracker.module.match.enums.WatchTypeEnum}
     */
    private Integer watchType;

    /**
     * 比赛场地
     */
    private String venue;

    /**
     * 备注
     */
    private String notes;

}

