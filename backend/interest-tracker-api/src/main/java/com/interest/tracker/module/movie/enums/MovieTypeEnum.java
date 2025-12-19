package com.interest.tracker.module.movie.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 影视类型枚举
 *
 * @author interest-tracker
 */
@Getter
@AllArgsConstructor
public enum MovieTypeEnum {

    MOVIE(1, "电影"),
    TV_SERIES(2, "电视剧");

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
    public static MovieTypeEnum valueOf(Integer value) {
        for (MovieTypeEnum type : values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return null;
    }

}

