package com.interest.tracker.module.concert.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 演出类型枚举
 *
 * @author interest-tracker
 */
@Getter
@AllArgsConstructor
public enum ConcertTypeEnum {

    CONCERT(1, "演唱会"),
    FESTIVAL(2, "音乐节"),
    PERFORMANCE(3, "演出"),
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
    public static ConcertTypeEnum valueOf(Integer value) {
        for (ConcertTypeEnum type : values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return null;
    }

}

