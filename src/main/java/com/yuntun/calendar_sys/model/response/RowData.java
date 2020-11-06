package com.yuntun.calendar_sys.model.response;

import java.util.List;

/**
 * <p>
 * 分页查询请求返回数据对象
 * </p>
 *
 * @author whj
 * @since 2020/9/8
 */
public class RowData<T> {
    /**
     * 集合
     */
    List<T> rows;
    /**
     * 数据总条数
     */
    long total;
    /**
     * 总页数
     */
    long totalPages;

    public RowData() {

    }

    public RowData(List<T> rows, Long total, Long totalPages) {
        this.total = total;
        this.totalPages = totalPages;
    }

    public static <T> RowData<T> of(Class<T> t){
        return new RowData<>();
    }

    public List<T> getRows() {
        return rows;
    }

    public RowData<T> setRows(List<T> rows) {
        this.rows = rows;
        return this;
    }

    public Long getTotal() {
        return total;
    }

    public RowData<T> setTotal(Long total) {
        this.total = total;
        return this;
    }

    public Long getTotalPages() {
        return totalPages;
    }

    public RowData<T> setTotalPages(Long totalPages) {
        this.totalPages = totalPages;
        return this;
    }
}
