package com.interest.tracker.module.concert.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

/**
 * 创建演唱会记录请求 VO
 *
 * @author interest-tracker
 */
@Schema(description = "创建演唱会记录请求 VO")
@Data
public class ConcertCreateReqVO {

    @Schema(description = "艺术家/乐队", requiredMode = REQUIRED, example = "The Beatles")
    @NotBlank(message = "艺术家不能为空")
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

    @Schema(description = "观演状态：1-想看 2-已看", example = "1")
    private Integer watchStatus;

    @Schema(description = "个人评分（0-10）", example = "8.5")
    private BigDecimal personalRating;

    @Schema(description = "观演日期", example = "2025-06-15")
    private LocalDate watchDate;

    @Schema(description = "票价", example = "580.00")
    private BigDecimal ticketPrice;

    @Schema(description = "座位信息", example = "A区1排1号")
    private String seatInfo;

    @Schema(description = "标签（逗号分隔）", example = "摇滚,经典")
    private String tags;

    @Schema(description = "评价", example = "很棒的演出")
    private String comment;

}

