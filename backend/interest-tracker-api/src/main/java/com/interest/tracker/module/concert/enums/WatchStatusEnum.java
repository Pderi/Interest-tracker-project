package com.interest.tracker.module.concert.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 观演状态枚举
 *
 * @author interest-tracker
 */
@Getter
@AllArgsConstructor
public enum WatchStatusEnum {

    WANT_TO_WATCH(1, "想看"),
    WATCHED(2, "已看");

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
    public static WatchStatusEnum valueOf(Integer value) {
        for (WatchStatusEnum status : values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }

}

