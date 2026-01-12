package com.interest.tracker.module.concert.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.interest.tracker.framework.mybatis.core.dataobject.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 演唱会 DO
 *
 * 对应表：concert
 */
@TableName("concert")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ConcertDO extends BaseDO {

    /**
     * 演唱会ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 艺术家/乐队
     */
    private String artist;

    /**
     * 演出名称
     */
    private String title;

    /**
     * 演出日期
     */
    private LocalDateTime concertDate;

    /**
     * 演出场地
     */
    private String venue;

    /**
     * 城市
     */
    private String city;

    /**
     * 国家
     */
    private String country;

    /**
     * 演出类型：1-演唱会 2-音乐节 3-演出 4-其他
     * 枚举 {@link com.interest.tracker.module.concert.enums.ConcertTypeEnum}
     */
    private Integer concertType;

    /**
     * 描述
     */
    private String description;

    /**
     * 海报URL
     */
    private String posterUrl;

}

