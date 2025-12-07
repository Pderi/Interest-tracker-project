package com.interest.tracker.framework.common.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页结果
 *
 * @param <T> 数据泛型
 */
@Data
@Schema(description = "分页结果")
public class PageResult<T> implements Serializable {

    @Schema(description = "数据", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<T> list;

    @Schema(description = "总量", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long total;

    public PageResult() {
    }

    public PageResult(List<T> list, Long total) {
        this.list = list;
        this.total = total;
    }

    public PageResult(Long total) {
        this.list = new ArrayList<>();
        this.total = total;
    }

    @JsonIgnore
    public boolean isEmpty() {
        return total == null || total == 0;
    }

    @JsonIgnore
    public boolean isNotEmpty() {
        return !isEmpty();
    }
}

