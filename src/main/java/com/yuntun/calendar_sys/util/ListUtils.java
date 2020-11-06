package com.yuntun.calendar_sys.util;

import java.util.List;

public class ListUtils {
    /**
     * 判断list是否为空对象或者空集合
     *
     * @param list
     * @return
     */
    public static boolean isEmpty(List list) {
        return (list == null || list.size() <= 0);
    }

    /**
     * 判断list是否不是空对象或者空集合
     *
     * @param list
     * @return
     */
    public static boolean isNotEmpty(List list) {
        return (list != null && list.size() > 0);
    }
}
