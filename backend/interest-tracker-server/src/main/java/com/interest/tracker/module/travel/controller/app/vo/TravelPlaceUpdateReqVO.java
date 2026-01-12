package com.interest.tracker.module.travel.controller.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 更新旅游地点信息请求 VO
 *
 * @author interest-tracker
 */
@Schema(description = "更新旅游地点信息请求 VO")
@Data
public class TravelPlaceUpdateReqVO {

    @Schema(description = "地点ID", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "地点名称", example = "北京")
    private String name;

    @Schema(description = "国家/地区", example = "中国")
    private String country;

    @Schema(description = "城市", example = "北京")
    private String city;

    @Schema(description = "详细地址", example = "北京市东城区天安门广场")
    private String address;

    @Schema(description = "纬度", example = "39.9042")
    private BigDecimal latitude;

    @Schema(description = "经度", example = "116.4074")
    private BigDecimal longitude;

    @Schema(description = "地点类型：1-城市 2-景点 3-国家 4-其他", example = "1")
    private Integer placeType;

    @Schema(description = "描述", example = "首都北京...")
    private String description;

    @Schema(description = "封面URL", example = "https://example.com/cover.jpg")
    private String coverUrl;

}

