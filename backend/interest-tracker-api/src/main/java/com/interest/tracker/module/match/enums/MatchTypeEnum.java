package com.interest.tracker.module.match.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 比赛类型枚举
 *
 * @author interest-tracker
 */
@Getter
@AllArgsConstructor
public enum MatchTypeEnum {

    LEAGUE(1, "联赛"),
    CUP(2, "杯赛"),
    FRIENDLY(3, "友谊赛");

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
    public static MatchTypeEnum valueOf(Integer value) {
        for (MatchTypeEnum type : values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return null;
    }

}

