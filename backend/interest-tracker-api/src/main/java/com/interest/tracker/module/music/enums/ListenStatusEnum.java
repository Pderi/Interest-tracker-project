package com.interest.tracker.module.music.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 听歌状态枚举
 *
 * @author interest-tracker
 */
@Getter
@AllArgsConstructor
public enum ListenStatusEnum {

    WANT_TO_LISTEN(1, "想听"),
    LISTENING(2, "在听"),
    LISTENED(3, "已听"),
    DROPPED(4, "弃听");

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
    public static ListenStatusEnum valueOf(Integer value) {
        for (ListenStatusEnum status : values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }

}

