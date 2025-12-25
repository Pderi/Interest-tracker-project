package com.interest.tracker.module.book.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.interest.tracker.framework.mybatis.core.dataobject.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 书籍 DO
 *
 * 对应表：book
 */
@TableName("book")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BookDO extends BaseDO {

    /**
     * 书籍ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 豆瓣ID（用于关联豆瓣数据）
     */
    @TableField("douban_id")
    private String doubanId;

    /**
     * 书名
     */
    private String title;

    /**
     * 作者（逗号分隔）
     */
    private String author;

    /**
     * 出版社
     */
    private String publisher;

    /**
     * 出版年份
     */
    @TableField("publish_year")
    private Integer publishYear;

    /**
     * ISBN
     */
    private String isbn;

    /**
     * 类型（小说、历史等，逗号分隔）
     */
    private String genre;

    /**
     * 简介
     */
    private String description;

    /**
     * 封面URL
     */
    @TableField("cover_url")
    private String coverUrl;

    /**
     * 页数
     */
    @TableField("page_count")
    private Integer pageCount;

    /**
     * 语言
     */
    private String language;

}

