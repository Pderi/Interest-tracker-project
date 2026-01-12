package com.interest.tracker.module.travel.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.interest.tracker.framework.mybatis.core.dataobject.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 旅游地点 DO
 *
 * 对应表：travel_place
 */
@TableName("travel_place")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TravelPlaceDO extends BaseDO {

    /**
     * 地点ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 地点名称
     */
    private String name;

    /**
     * 国家/地区
     */
    private String country;

    /**
     * 城市
     */
    private String city;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 纬度
     */
    private BigDecimal latitude;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 地点类型：1-城市 2-景点 3-国家 4-其他
     * 枚举 {@link com.interest.tracker.module.travel.enums.PlaceTypeEnum}
     */
    private Integer placeType;

    /**
     * 描述
     */
    private String description;

    /**
     * 封面URL
     */
    private String coverUrl;

}

