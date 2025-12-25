package com.interest.tracker.module.book.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 阅读状态枚举
 *
 * @author interest-tracker
 */
@Getter
@AllArgsConstructor
public enum ReadStatusEnum {

    WANT_TO_READ(1, "想读"),
    READING(2, "在读"),
    READ(3, "已读"),
    DROPPED(4, "弃读");

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
    public static ReadStatusEnum valueOf(Integer value) {
        for (ReadStatusEnum status : values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }

}

