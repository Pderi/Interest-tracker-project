package com.interest.tracker.module.movie.controller.app;

import com.interest.tracker.framework.common.pojo.CommonResult;
import com.interest.tracker.framework.common.pojo.PageResult;
import com.interest.tracker.module.movie.controller.app.vo.*;
import com.interest.tracker.module.movie.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.interest.tracker.framework.common.pojo.CommonResult.success;

/**
 * 影视 App - 影视管理接口
 *
 * @author interest-tracker
 */
@Tag(name = "影视 App - 影视管理")
@RestController
@RequestMapping("/api/movies")
@Validated
public class MovieAppController {

    @Resource
    private MovieService movieService;

    /**
     * 创建影视记录
     */
    @PostMapping
    @Operation(summary = "创建影视记录")
    public CommonResult<MovieCreateRespVO> createMovie(@Valid @RequestBody MovieCreateReqVO reqVO) {
        MovieCreateRespVO respVO = movieService.createMovie(reqVO);
        return success(respVO);
    }

    /**
     * 更新观看记录
     */
    @PutMapping("/records/{id}")
    @Operation(summary = "更新观看记录")
    @Parameter(name = "id", description = "记录ID", required = true)
    public CommonResult<Boolean> updateMovieRecord(@PathVariable("id") Long id,
                                                    @Valid @RequestBody MovieRecordUpdateReqVO reqVO) {
        reqVO.setId(id);
        movieService.updateMovieRecord(reqVO);
        return success(true);
    }

    /**
     * 获取影视详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取影视详情")
    @Parameter(name = "id", description = "影视ID", required = true)
    public CommonResult<MovieRespVO> getMovie(@PathVariable("id") Long id) {
        MovieRespVO respVO = movieService.getMovie(id);
        return success(respVO);
    }

    /**
     * 获取影视分页列表
     */
    @GetMapping
    @Operation(summary = "获取影视分页列表")
    public CommonResult<PageResult<MoviePageRespVO>> getMoviePage(@Valid MoviePageReqVO reqVO) {
        PageResult<MoviePageRespVO> pageResult = movieService.getMoviePage(reqVO);
        return success(pageResult);
    }

    /**
     * 删除观看记录
     */
    @DeleteMapping("/records/{id}")
    @Operation(summary = "删除观看记录")
    @Parameter(name = "id", description = "记录ID", required = true)
    public CommonResult<Boolean> deleteMovieRecord(@PathVariable("id") Long id) {
        movieService.deleteMovieRecord(id);
        return success(true);
    }

    /**
     * 搜索影视（TMDB）
     */
    @GetMapping("/search")
    @Operation(summary = "搜索影视（TMDB）")
    @Parameter(name = "keyword", description = "搜索关键词", required = true)
    @Parameter(name = "page", description = "页码", example = "1")
    public CommonResult<PageResult<MovieSearchRespVO>> searchMovies(
            @RequestParam("keyword") String keyword,
            @RequestParam(value = "page", defaultValue = "1") Integer page) {
        PageResult<MovieSearchRespVO> result = movieService.searchMovies(keyword, page);
        return success(result);
    }

}

