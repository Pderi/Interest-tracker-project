package com.interest.tracker.module.travel.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 地点类型枚举
 *
 * @author interest-tracker
 */
@Getter
@AllArgsConstructor
public enum PlaceTypeEnum {

    CITY(1, "城市"),
    ATTRACTION(2, "景点"),
    COUNTRY(3, "国家"),
    OTHER(4, "其他");

    /**
     * 类型值
     */
    private final Integer value;

    /**
     * 类型名
     */
    private final String name;

    /**
     * 根据值获取枚举
     *
     * @param value 类型值
     * @return 枚举
     */
    public static PlaceTypeEnum valueOf(Integer value) {
        for (PlaceTypeEnum type : values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return null;
    }

}

