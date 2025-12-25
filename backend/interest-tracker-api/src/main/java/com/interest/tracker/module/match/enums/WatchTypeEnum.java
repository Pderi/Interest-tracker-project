package com.interest.tracker.module.match.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 观赛方式枚举
 *
 * @author interest-tracker
 */
@Getter
@AllArgsConstructor
public enum WatchTypeEnum {

    LIVE(1, "现场"),
    STREAMING(2, "直播"),
    REPLAY(3, "回放");

    /**
     * 方式值
     */
    private final Integer value;

    /**
     * 方式名
     */
    private final String name;

    /**
     * 根据值获取枚举
     *
     * @param value 方式值
     * @return 枚举
     */
    public static WatchTypeEnum valueOf(Integer value) {
        for (WatchTypeEnum type : values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return null;
    }

}

