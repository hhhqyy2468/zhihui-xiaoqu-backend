package com.hyu.common.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 分页响应结果
 *
 * @author hyu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    /** 总记录数 */
    private Long total;

    /** 列表数据 */
    private List<T> rows;

    public PageResult(List<T> rows, Long total) {
        this.rows = rows;
        this.total = total;
    }

    public static <T> PageResult<T> build(List<T> rows, Long total) {
        return new PageResult<>(rows, total);
    }
}