package com.interest.tracker.framework.mybatis.core.dataobject;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础实体对象
 */
@Data
public abstract class BaseDO implements Serializable {

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 最后更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建者，目前使用 SysUser 的 id 编号
     */
    @TableField(fill = FieldFill.INSERT)
    private String creator;

    /**
     * 更新者，目前使用 SysUser 的 id 编号
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updater;

    /**
     * 是否删除
     */
    @TableLogic
    private Boolean deleted;

}

