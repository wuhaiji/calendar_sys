package com.yuntun.calendar_sys.model.response;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author whj
 * @since 2020/11/5
 */
@Data
@Accessors(chain = true)
@RequiredArgsConstructor(staticName = "of")
public class PageBean<T> {

    /**
     * 总数据量
     */
    private Long total;
    /**
     * 总页数
     */
    private Long totalPages;
    /**
     * 数据列表
     */
    private List<T> list;
}
