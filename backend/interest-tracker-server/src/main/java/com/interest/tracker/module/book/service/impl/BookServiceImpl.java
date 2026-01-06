package com.interest.tracker.module.book.service.impl;

import com.interest.tracker.framework.common.pojo.PageResult;
import com.interest.tracker.framework.common.util.object.BeanUtils;
import com.interest.tracker.framework.security.core.UserContext;
import com.interest.tracker.module.book.controller.app.vo.*;
import com.interest.tracker.module.book.dal.dataobject.BookDO;
import com.interest.tracker.module.book.dal.dataobject.BookRecordDO;
import com.interest.tracker.module.book.dal.mysql.BookMapper;
import com.interest.tracker.module.book.dal.mysql.BookRecordMapper;
import com.interest.tracker.module.book.enums.ReadStatusEnum;
import com.interest.tracker.module.book.service.BookService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.interest.tracker.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.interest.tracker.framework.common.exception.enums.GlobalErrorCodeConstants.UNAUTHORIZED;
import static com.interest.tracker.module.book.constants.BookErrorCodeConstants.*;

/**
 * 图书服务实现
 *
 * @author interest-tracker
 */
@Service
@Validated
@Slf4j
public class BookServiceImpl implements BookService {

    @Resource
    private BookMapper bookMapper;

    @Resource
    private BookRecordMapper bookRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BookCreateRespVO createBook(BookCreateReqVO reqVO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        // 1. 创建 Book
        BookDO bookDO = BeanUtils.toBean(reqVO, BookDO.class);
        bookMapper.insert(bookDO);

        // 2. 检查是否已存在阅读记录
        BookRecordDO existRecord = bookRecordMapper.selectByUserIdAndBookId(userId, bookDO.getId());
        if (existRecord != null) {
            throw exception(BOOK_RECORD_ALREADY_EXISTS);
        }

        // 3. 创建阅读记录
        BookRecordDO recordDO = BeanUtils.toBean(reqVO, BookRecordDO.class);
        recordDO.setUserId(userId);
        recordDO.setBookId(bookDO.getId());
        // 设置阅读状态，默认为"想读"
        if (recordDO.getReadStatus() == null) {
            recordDO.setReadStatus(ReadStatusEnum.WANT_TO_READ.getValue()); // 默认"想读"
        }
        bookRecordMapper.insert(recordDO);

        // 4. 返回结果
        BookCreateRespVO respVO = new BookCreateRespVO();
        respVO.setBookId(bookDO.getId());
        respVO.setRecordId(recordDO.getId());
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBookRecord(BookRecordUpdateReqVO reqVO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        // 1. 校验记录存在
        BookRecordDO recordDO = validateBookRecordExists(reqVO.getId());

        // 2. 校验权限（只能更新自己的记录）
        if (!recordDO.getUserId().equals(userId)) {
            throw exception(BOOK_RECORD_NOT_OWNER);
        }

        // 3. 如果请求中携带了封面地址，顺带更新书籍基础信息的封面
        if (reqVO.getCoverUrl() != null) {
            BookDO bookDO = validateBookExists(recordDO.getBookId());
            bookDO.setCoverUrl(reqVO.getCoverUrl());
            bookMapper.updateById(bookDO);
        }

        // 4. 应用记录更新字段
        applyRecordUpdate(reqVO, recordDO);
        // 5. 根据业务规则自动填充字段
        autoFillReadDateIfNeeded(reqVO, recordDO);

        bookRecordMapper.updateById(recordDO);
    }

    /**
     * 将更新请求中的非空字段合并到已有的记录上
     */
    private void applyRecordUpdate(BookRecordUpdateReqVO reqVO, BookRecordDO recordDO) {
        if (reqVO.getReadStatus() != null) {
            recordDO.setReadStatus(reqVO.getReadStatus());
        }
        if (reqVO.getPersonalRating() != null) {
            recordDO.setPersonalRating(reqVO.getPersonalRating());
        }
        if (reqVO.getReadDate() != null) {
            recordDO.setReadDate(reqVO.getReadDate());
        }
        if (reqVO.getReadProgress() != null) {
            recordDO.setReadProgress(reqVO.getReadProgress());
        }
        if (reqVO.getComment() != null) {
            recordDO.setComment(reqVO.getComment());
        }
        if (reqVO.getTags() != null) {
            recordDO.setTags(reqVO.getTags());
        }
    }

    /**
     * 状态改为「已读」且未填写阅读日期时，自动补当天
     */
    private void autoFillReadDateIfNeeded(BookRecordUpdateReqVO reqVO, BookRecordDO recordDO) {
        if (reqVO.getReadStatus() != null
                && ReadStatusEnum.READ.getValue().equals(reqVO.getReadStatus())
                && recordDO.getReadDate() == null) {
            recordDO.setReadDate(LocalDate.now());
        }
    }

    @Override
    public BookRespVO getBook(Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        // 1. 查询书籍信息
        BookDO bookDO = validateBookExists(id);

        // 2. 查询阅读记录
        BookRecordDO recordDO = bookRecordMapper.selectByUserIdAndBookId(userId, id);
        if (recordDO == null) {
            throw exception(BOOK_RECORD_NOT_EXISTS);
        }

        // 3. 组装返回数据
        BookRespVO respVO = new BookRespVO();
        BookRespVO.BookInfo bookInfo = BeanUtils.toBean(bookDO, BookRespVO.BookInfo.class);
        BookRespVO.RecordInfo recordInfo = BeanUtils.toBean(recordDO, BookRespVO.RecordInfo.class);
        respVO.setBook(bookInfo);
        respVO.setRecord(recordInfo);

        return respVO;
    }

    @Override
    public BookPageWithStatsRespVO getBookPage(BookPageReqVO reqVO) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        // 设置用户ID
        reqVO.setUserId(userId);

        // 1. 分页查询阅读记录（筛选逻辑已在 Mapper 层通过 SQL 实现）
        PageResult<BookRecordDO> pageResult = bookRecordMapper.selectPage(reqVO);

        // 2. 批量查询书籍信息
        List<Long> bookIds = pageResult.getList().stream()
                .map(BookRecordDO::getBookId)
                .distinct()
                .collect(Collectors.toList());
        List<BookDO> bookList = bookIds.isEmpty() ? List.of() : bookMapper.selectBatchIds(bookIds);

        // 3. 构建书籍信息Map
        java.util.Map<Long, BookDO> bookMap = bookList.stream()
                .collect(Collectors.toMap(BookDO::getId, book -> book));

        // 4. 组装返回数据
        List<BookPageRespVO> voList = pageResult.getList().stream()
                .map(record -> {
                    BookPageRespVO vo = BeanUtils.toBean(record, BookPageRespVO.class);
                    // 设置记录ID和书籍ID（字段名不同）
                    vo.setRecordId(record.getId());
                    vo.setBookId(record.getBookId());

                    // 填充书籍信息
                    BookDO book = bookMap.get(record.getBookId());
                    if (book != null) {
                        vo.setTitle(book.getTitle());
                        vo.setAuthor(book.getAuthor());
                        vo.setGenre(book.getGenre());
                        vo.setCoverUrl(book.getCoverUrl());
                    }
                    // 填充评价
                    vo.setComment(record.getComment());

                    return vo;
                })
                .collect(Collectors.toList());

        // 5. 统计各状态数量
        java.util.Map<Integer, Long> statusCounts = bookRecordMapper.countByStatus(userId);

        // 6. 组装响应
        BookPageWithStatsRespVO respVO = new BookPageWithStatsRespVO();
        respVO.setPage(new PageResult<>(voList, pageResult.getTotal()));
        respVO.setStatusCounts(statusCounts);
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBookRecord(Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw exception(UNAUTHORIZED);
        }

        // 1. 校验记录存在
        BookRecordDO recordDO = validateBookRecordExists(id);

        // 2. 校验权限（只能删除自己的记录）
        if (!recordDO.getUserId().equals(userId)) {
            throw exception(BOOK_RECORD_NOT_OWNER);
        }

        // 3. 删除（软删除）
        bookRecordMapper.deleteById(id);
    }

    /* ================== 私有方法 ================== */

    /**
     * 校验书籍是否存在
     */
    private BookDO validateBookExists(Long id) {
        BookDO bookDO = bookMapper.selectById(id);
        if (bookDO == null) {
            throw exception(BOOK_NOT_EXISTS);
        }
        return bookDO;
    }

    /**
     * 校验阅读记录是否存在
     */
    private BookRecordDO validateBookRecordExists(Long id) {
        BookRecordDO recordDO = bookRecordMapper.selectById(id);
        if (recordDO == null) {
            throw exception(BOOK_RECORD_NOT_EXISTS);
        }
        return recordDO;
    }

}

