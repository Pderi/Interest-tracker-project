package com.interest.tracker.module.book.controller.app;

import com.interest.tracker.framework.common.pojo.CommonResult;
import com.interest.tracker.module.book.controller.app.vo.*;
import com.interest.tracker.module.book.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.interest.tracker.framework.common.pojo.CommonResult.success;

/**
 * 图书 App - 图书管理接口
 *
 * @author interest-tracker
 */
@Tag(name = "图书 App - 图书管理")
@RestController
@RequestMapping("/api/books")
@Validated
public class BookAppController {

    @Resource
    private BookService bookService;

    /**
     * 创建图书记录
     */
    @PostMapping
    @Operation(summary = "创建图书记录")
    public CommonResult<BookCreateRespVO> createBook(@Valid @RequestBody BookCreateReqVO reqVO) {
        BookCreateRespVO respVO = bookService.createBook(reqVO);
        return success(respVO);
    }

    /**
     * 更新阅读记录
     */
    @PutMapping("/records/{id}")
    @Operation(summary = "更新阅读记录")
    @Parameter(name = "id", description = "记录ID", required = true)
    public CommonResult<Boolean> updateBookRecord(@PathVariable("id") Long id,
                                                   @Valid @RequestBody BookRecordUpdateReqVO reqVO) {
        reqVO.setId(id);
        bookService.updateBookRecord(reqVO);
        return success(true);
    }

    /**
     * 获取图书详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取图书详情")
    @Parameter(name = "id", description = "书籍ID", required = true)
    public CommonResult<BookRespVO> getBook(@PathVariable("id") Long id) {
        BookRespVO respVO = bookService.getBook(id);
        return success(respVO);
    }

    /**
     * 获取图书分页列表
     */
    @GetMapping
    @Operation(summary = "获取图书分页列表")
    public CommonResult<BookPageWithStatsRespVO> getBookPage(@Valid BookPageReqVO reqVO) {
        BookPageWithStatsRespVO result = bookService.getBookPage(reqVO);
        return success(result);
    }

    /**
     * 删除阅读记录
     */
    @DeleteMapping("/records/{id}")
    @Operation(summary = "删除阅读记录")
    @Parameter(name = "id", description = "记录ID", required = true)
    public CommonResult<Boolean> deleteBookRecord(@PathVariable("id") Long id) {
        bookService.deleteBookRecord(id);
        return success(true);
    }

}

