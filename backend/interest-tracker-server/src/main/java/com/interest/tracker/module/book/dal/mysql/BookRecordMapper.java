package com.interest.tracker.module.book.dal.mysql;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseMapper;
import com.interest.tracker.framework.common.pojo.PageResult;
import com.interest.tracker.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.interest.tracker.framework.mybatis.core.query.MPJLambdaWrapperX;
import com.interest.tracker.module.book.controller.app.vo.BookPageReqVO;
import com.interest.tracker.module.book.dal.dataobject.BookDO;
import com.interest.tracker.module.book.dal.dataobject.BookRecordDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 阅读记录 Mapper
 *
 * @author interest-tracker
 */
@Mapper
public interface BookRecordMapper extends MPJBaseMapper<BookRecordDO> {

    /**
     * 分页查询阅读记录（支持关联 Book 表筛选）
     *
     * @param reqVO 分页请求参数
     * @return 分页结果
     */
    default PageResult<BookRecordDO> selectPage(BookPageReqVO reqVO) {
        MPJLambdaWrapperX<BookRecordDO> wrapper = new MPJLambdaWrapperX<BookRecordDO>();
        wrapper.leftJoin(BookDO.class, BookDO::getId, BookRecordDO::getBookId);
        wrapper.eqIfPresent(BookRecordDO::getUserId, reqVO.getUserId())
                .eqIfPresent(BookRecordDO::getReadStatus, reqVO.getReadStatus())
                .betweenIfPresent(BookRecordDO::getReadDate, reqVO.getStartReadDate(), reqVO.getEndReadDate())
                .likeIfPresent(BookDO::getTitle, reqVO.getKeyword())
                .likeIfPresent(BookDO::getAuthor, reqVO.getAuthor())
                .likeIfPresent(BookRecordDO::getTags, reqVO.getTag())
                .selectAll(BookRecordDO.class);

        // 排序
        if ("readDate".equals(reqVO.getSort())) {
            wrapper.orderByDesc(BookRecordDO::getReadDate);
        } else if ("rating".equals(reqVO.getSort())) {
            wrapper.orderByDesc(BookRecordDO::getPersonalRating);
        } else {
            // 默认按创建时间倒序
            wrapper.orderByDesc(BookRecordDO::getCreateTime);
        }

        // 分页查询
        Page<BookRecordDO> mpPage = new Page<>(reqVO.getPageNo(), reqVO.getPageSize());
        Page<BookRecordDO> page = selectJoinPage(mpPage, BookRecordDO.class, wrapper.distinct());
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    /**
     * 根据用户ID和书籍ID查询记录
     *
     * @param userId 用户ID
     * @param bookId 书籍ID
     * @return 阅读记录
     */
    default BookRecordDO selectByUserIdAndBookId(Long userId, Long bookId) {
        MPJLambdaWrapperX<BookRecordDO> wrapper = new MPJLambdaWrapperX<BookRecordDO>()
                .eqIfPresent(BookRecordDO::getUserId, userId)
                .eqIfPresent(BookRecordDO::getBookId, bookId)
                .selectAll(BookRecordDO.class);
        return selectJoinOne(BookRecordDO.class, wrapper);
    }

    /**
     * 统计各状态的数量（不包含搜索和筛选条件，只按用户ID）
     *
     * @param userId 用户ID
     * @return Map<状态值, 数量>
     */
    default java.util.Map<Integer, Long> countByStatus(Long userId) {
        java.util.Map<Integer, Long> result = new java.util.HashMap<>();
        for (int status = 1; status <= 4; status++) {
            LambdaQueryWrapperX<BookRecordDO> wrapper = new LambdaQueryWrapperX<>();
            wrapper.eq(BookRecordDO::getUserId, userId)
                    .eq(BookRecordDO::getReadStatus, status);
            long count = selectCount(wrapper);
            result.put(status, count);
        }
        return result;
    }

}

