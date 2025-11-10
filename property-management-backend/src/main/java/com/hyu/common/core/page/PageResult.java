package com.hyu.common.core.page;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页查询结果
 */
@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 列表数据
     */
    private List<T> rows;

    /**
     * 当前页码
     */
    private int pageNum;

    /**
     * 每页大小
     */
    private int pageSize;

    /**
     * 总页数
     */
    private int pages;

    public PageResult() {
    }

    public PageResult(long total, List<T> rows, int pageNum, int pageSize) {
        this.total = total;
        this.rows = rows;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.pages = (int) Math.ceil((double) total / pageSize);
    }

    public PageResult(long total, List<T> rows) {
        this.total = total;
        this.rows = rows;
    }

    /**
     * 构建分页结果
     */
    public static <T> PageResult<T> build(long total, List<T> rows, int pageNum, int pageSize) {
        return new PageResult<>(total, rows, pageNum, pageSize);
    }

    /**
     * 构建分页结果
     */
    public static <T> PageResult<T> build(long total, List<T> rows) {
        return new PageResult<>(total, rows);
    }
}