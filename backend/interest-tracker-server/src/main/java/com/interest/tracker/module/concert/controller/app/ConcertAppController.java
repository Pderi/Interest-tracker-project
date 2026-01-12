package com.interest.tracker.module.concert.controller.app;

import com.interest.tracker.framework.common.pojo.CommonResult;
import com.interest.tracker.module.concert.controller.app.vo.*;
import com.interest.tracker.module.concert.service.ConcertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.interest.tracker.framework.common.pojo.CommonResult.success;

/**
 * 演唱会 App - 演唱会管理接口
 *
 * @author interest-tracker
 */
@Tag(name = "演唱会 App - 演唱会管理")
@RestController
@RequestMapping("/api/concert/concerts")
@Validated
public class ConcertAppController {

    @Resource
    private ConcertService concertService;

    /**
     * 创建演唱会记录
     */
    @PostMapping
    @Operation(summary = "创建演唱会记录")
    public CommonResult<ConcertCreateRespVO> createConcert(@Valid @RequestBody ConcertCreateReqVO reqVO) {
        ConcertCreateRespVO respVO = concertService.createConcert(reqVO);
        return success(respVO);
    }

    /**
     * 更新观演记录
     */
    @PutMapping("/records/{id}")
    @Operation(summary = "更新观演记录")
    @Parameter(name = "id", description = "记录ID", required = true)
    public CommonResult<Boolean> updateConcertRecord(@PathVariable("id") Long id,
                                                     @Valid @RequestBody ConcertRecordUpdateReqVO reqVO) {
        reqVO.setId(id);
        concertService.updateConcertRecord(reqVO);
        return success(true);
    }

    /**
     * 更新演唱会信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新演唱会信息")
    @Parameter(name = "id", description = "演唱会ID", required = true)
    public CommonResult<Boolean> updateConcert(@PathVariable("id") Long id,
                                               @Valid @RequestBody ConcertUpdateReqVO reqVO) {
        reqVO.setId(id);
        concertService.updateConcert(reqVO);
        return success(true);
    }

    /**
     * 获取演唱会详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取演唱会详情")
    @Parameter(name = "id", description = "演唱会ID", required = true)
    public CommonResult<ConcertRespVO> getConcert(@PathVariable("id") Long id) {
        ConcertRespVO respVO = concertService.getConcert(id);
        return success(respVO);
    }

    /**
     * 获取演唱会分页列表
     */
    @GetMapping
    @Operation(summary = "获取演唱会分页列表")
    public CommonResult<ConcertPageWithStatsRespVO> getConcertPage(@Valid ConcertPageReqVO reqVO) {
        ConcertPageWithStatsRespVO result = concertService.getConcertPage(reqVO);
        return success(result);
    }

    /**
     * 删除观演记录
     */
    @DeleteMapping("/records/{id}")
    @Operation(summary = "删除观演记录")
    @Parameter(name = "id", description = "记录ID", required = true)
    public CommonResult<Boolean> deleteConcertRecord(@PathVariable("id") Long id) {
        concertService.deleteConcertRecord(id);
        return success(true);
    }

}

