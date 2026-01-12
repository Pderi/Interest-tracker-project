package com.interest.tracker.module.travel.controller.app;

import com.interest.tracker.framework.common.pojo.CommonResult;
import com.interest.tracker.module.travel.controller.app.vo.*;
import com.interest.tracker.module.travel.service.TravelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.interest.tracker.framework.common.pojo.CommonResult.success;

/**
 * 旅游 App - 旅游管理接口
 *
 * @author interest-tracker
 */
@Tag(name = "旅游 App - 旅游管理")
@RestController
@RequestMapping("/api/travel/places")
@Validated
public class TravelAppController {

    @Resource
    private TravelService travelService;

    /**
     * 创建旅游记录
     */
    @PostMapping
    @Operation(summary = "创建旅游记录")
    public CommonResult<TravelCreateRespVO> createTravel(@Valid @RequestBody TravelCreateReqVO reqVO) {
        TravelCreateRespVO respVO = travelService.createTravel(reqVO);
        return success(respVO);
    }

    /**
     * 更新旅游记录
     */
    @PutMapping("/records/{id}")
    @Operation(summary = "更新旅游记录")
    @Parameter(name = "id", description = "记录ID", required = true)
    public CommonResult<Boolean> updateTravelRecord(@PathVariable("id") Long id,
                                                    @Valid @RequestBody TravelRecordUpdateReqVO reqVO) {
        reqVO.setId(id);
        travelService.updateTravelRecord(reqVO);
        return success(true);
    }

    /**
     * 更新旅游地点信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新旅游地点信息")
    @Parameter(name = "id", description = "地点ID", required = true)
    public CommonResult<Boolean> updateTravelPlace(@PathVariable("id") Long id,
                                                   @Valid @RequestBody TravelPlaceUpdateReqVO reqVO) {
        reqVO.setId(id);
        travelService.updateTravelPlace(reqVO);
        return success(true);
    }

    /**
     * 获取旅游详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取旅游详情")
    @Parameter(name = "id", description = "地点ID", required = true)
    public CommonResult<TravelRespVO> getTravel(@PathVariable("id") Long id) {
        TravelRespVO respVO = travelService.getTravel(id);
        return success(respVO);
    }

    /**
     * 获取旅游分页列表
     */
    @GetMapping
    @Operation(summary = "获取旅游分页列表")
    public CommonResult<TravelPageWithStatsRespVO> getTravelPage(@Valid TravelPageReqVO reqVO) {
        TravelPageWithStatsRespVO result = travelService.getTravelPage(reqVO);
        return success(result);
    }

    /**
     * 删除旅游记录
     */
    @DeleteMapping("/records/{id}")
    @Operation(summary = "删除旅游记录")
    @Parameter(name = "id", description = "记录ID", required = true)
    public CommonResult<Boolean> deleteTravelRecord(@PathVariable("id") Long id) {
        travelService.deleteTravelRecord(id);
        return success(true);
    }

}

