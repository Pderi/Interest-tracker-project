package com.interest.tracker.module.music.controller.app;

import com.interest.tracker.framework.common.pojo.CommonResult;
import com.interest.tracker.framework.common.pojo.PageResult;
import com.interest.tracker.module.music.controller.app.vo.*;
import com.interest.tracker.module.music.service.MusicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.interest.tracker.framework.common.pojo.CommonResult.success;

/**
 * 音乐 App - 音乐管理接口
 *
 * @author interest-tracker
 */
@Tag(name = "音乐 App - 音乐管理")
@RestController
@RequestMapping("/api/music/albums")
@Validated
public class MusicAppController {

    @Resource
    private MusicService musicService;

    /**
     * 创建专辑记录
     */
    @PostMapping
    @Operation(summary = "创建专辑记录")
    public CommonResult<AlbumCreateRespVO> createAlbum(@Valid @RequestBody AlbumCreateReqVO reqVO) {
        AlbumCreateRespVO respVO = musicService.createAlbum(reqVO);
        return success(respVO);
    }

    /**
     * 更新听歌记录
     */
    @PutMapping("/records/{id}")
    @Operation(summary = "更新听歌记录")
    @Parameter(name = "id", description = "记录ID", required = true)
    public CommonResult<Boolean> updateAlbumRecord(@PathVariable("id") Long id,
                                                    @Valid @RequestBody AlbumRecordUpdateReqVO reqVO) {
        reqVO.setId(id);
        musicService.updateAlbumRecord(reqVO);
        return success(true);
    }

    /**
     * 获取专辑详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取专辑详情")
    @Parameter(name = "id", description = "专辑ID", required = true)
    public CommonResult<AlbumRespVO> getAlbum(@PathVariable("id") Long id) {
        AlbumRespVO respVO = musicService.getAlbum(id);
        return success(respVO);
    }

    /**
     * 获取专辑分页列表
     */
    @GetMapping
    @Operation(summary = "获取专辑分页列表")
    public CommonResult<AlbumPageWithStatsRespVO> getAlbumPage(@Valid AlbumPageReqVO reqVO) {
        AlbumPageWithStatsRespVO result = musicService.getAlbumPage(reqVO);
        return success(result);
    }

    /**
     * 删除听歌记录
     */
    @DeleteMapping("/records/{id}")
    @Operation(summary = "删除听歌记录")
    @Parameter(name = "id", description = "记录ID", required = true)
    public CommonResult<Boolean> deleteAlbumRecord(@PathVariable("id") Long id) {
        musicService.deleteAlbumRecord(id);
        return success(true);
    }

}

