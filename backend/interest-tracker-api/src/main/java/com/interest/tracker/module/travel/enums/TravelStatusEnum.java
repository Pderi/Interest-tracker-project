package com.interest.tracker.module.travel.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 旅游状态枚举
 *
 * @author interest-tracker
 */
@Getter
@AllArgsConstructor
public enum TravelStatusEnum {

    WANT_TO_GO(1, "想去"),
    PLANNING(2, "计划中"),
    VISITED(3, "已去");

    /**
     * 状态值
     */
    private final Integer value;

    /**
     * 状态名
     */
    private final String name;

    /**
     * 根据值获取枚举
     *
     * @param value 状态值
     * @return 枚举
     */
    public static TravelStatusEnum valueOf(Integer value) {
        for (TravelStatusEnum status : values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }

}

