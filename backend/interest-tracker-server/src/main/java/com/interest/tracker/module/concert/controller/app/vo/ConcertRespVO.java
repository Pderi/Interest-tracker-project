package com.interest.tracker.module.concert.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 演唱会详情响应 VO
 *
 * @author interest-tracker
 */
@Schema(description = "演唱会详情响应 VO")
@Data
public class ConcertRespVO {

    @Schema(description = "演唱会信息")
    private ConcertInfo concert;

    @Schema(description = "观演记录")
    private RecordInfo record;

    /**
     * 演唱会信息
     */
    @Data
    @Schema(description = "演唱会信息")
    public static class ConcertInfo {
        @Schema(description = "演唱会ID", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "1")
        private Long id;

        @Schema(description = "艺术家/乐队", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "The Beatles")
        private String artist;

        @Schema(description = "演出名称", example = "Abbey Road Tour")
        private String title;

        @Schema(description = "演出日期", example = "2025-06-15T20:00:00")
        private LocalDateTime concertDate;

        @Schema(description = "演出场地", example = "国家体育场")
        private String venue;

        @Schema(description = "城市", example = "北京")
        private String city;

        @Schema(description = "国家", example = "中国")
        private String country;

        @Schema(description = "演出类型：1-演唱会 2-音乐节 3-演出 4-其他", example = "1")
        private Integer concertType;

        @Schema(description = "描述", example = "经典演唱会...")
        private String description;

        @Schema(description = "海报URL", example = "https://example.com/poster.jpg")
        private String posterUrl;
    }

    /**
     * 观演记录信息
     */
    @Data
    @Schema(description = "观演记录信息")
    public static class RecordInfo {
        @Schema(description = "记录ID", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "1")
        private Long id;

        @Schema(description = "观演状态：1-想看 2-已看", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "2")
        private Integer watchStatus;

        @Schema(description = "个人评分（0-10）", example = "8.5")
        private BigDecimal personalRating;

        @Schema(description = "观演日期", example = "2025-06-15")
        private LocalDate watchDate;

        @Schema(description = "票价", example = "580.00")
        private BigDecimal ticketPrice;

        @Schema(description = "座位信息", example = "A区1排1号")
        private String seatInfo;

        @Schema(description = "评价", example = "很棒的演出")
        private String comment;

        @Schema(description = "标签（逗号分隔）", example = "摇滚,经典")
        private String tags;
    }

}

