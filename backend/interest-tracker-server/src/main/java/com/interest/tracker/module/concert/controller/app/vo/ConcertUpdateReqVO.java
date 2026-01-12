package com.interest.tracker.module.concert.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 更新演唱会信息请求 VO
 *
 * @author interest-tracker
 */
@Schema(description = "更新演唱会信息请求 VO")
@Data
public class ConcertUpdateReqVO {

    @Schema(description = "演唱会ID", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "艺术家/乐队", example = "The Beatles")
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

