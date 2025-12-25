package com.interest.tracker.module.match.controller.app;

import com.interest.tracker.framework.common.pojo.CommonResult;
import com.interest.tracker.module.match.controller.app.vo.*;
import com.interest.tracker.module.match.service.MatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.interest.tracker.framework.common.pojo.CommonResult.success;

/**
 * 球赛 App - 比赛管理接口
 *
 * @author interest-tracker
 */
@Tag(name = "球赛 App - 比赛管理")
@RestController
@RequestMapping("/api/matches")
@Validated
public class MatchAppController {

    @Resource
    private MatchService matchService;

    /**
     * 创建比赛记录
     */
    @PostMapping
    @Operation(summary = "创建比赛记录")
    public CommonResult<MatchCreateRespVO> createMatch(@Valid @RequestBody MatchCreateReqVO reqVO) {
        MatchCreateRespVO respVO = matchService.createMatch(reqVO);
        return success(respVO);
    }

    /**
     * 更新比赛记录
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新比赛记录")
    @Parameter(name = "id", description = "记录ID", required = true)
    public CommonResult<Boolean> updateMatch(@PathVariable("id") Long id,
                                              @Valid @RequestBody MatchUpdateReqVO reqVO) {
        reqVO.setId(id);
        matchService.updateMatch(reqVO);
        return success(true);
    }

    /**
     * 获取比赛详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取比赛详情")
    @Parameter(name = "id", description = "记录ID", required = true)
    public CommonResult<MatchRespVO> getMatch(@PathVariable("id") Long id) {
        MatchRespVO respVO = matchService.getMatch(id);
        return success(respVO);
    }

    /**
     * 获取比赛分页列表（包含统计信息）
     */
    @GetMapping
    @Operation(summary = "获取比赛分页列表（包含统计信息）")
    public CommonResult<MatchPageWithStatsRespVO> getMatchPage(@Valid MatchPageReqVO reqVO) {
        MatchPageWithStatsRespVO result = matchService.getMatchPage(reqVO);
        return success(result);
    }

    /**
     * 删除比赛记录
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除比赛记录")
    @Parameter(name = "id", description = "记录ID", required = true)
    public CommonResult<Boolean> deleteMatch(@PathVariable("id") Long id) {
        matchService.deleteMatch(id);
        return success(true);
    }

}

