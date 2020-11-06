package com.yuntun.calendar_sys.util;

import java.util.Collection;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author whj
 * @since 2020/9/23
 */
public class EptUtil {

    /**
     * 判断对象是否为空
     * 如果是集合还会判读集合中是否有元素
     * 如果是map还会判读map中是否有键值对
     * 如果是String还会判读是否是空字符串
     *
     * @param object 对象
     * @return
     */
    public static boolean isEmpty(Object object) {
        if (object instanceof String)
            return "".equals(object);
        else if (object instanceof Collection)
            return ((Collection) object).isEmpty();
        else if (object instanceof Map)
            return ((Map) object).entrySet().isEmpty();
        else
            return object == null;
    }

    /**
     * 判断对象是否不为空
     * 如果是集合还会判读集合中是否有元素
     * 如果是map还会判读map中是否有键值对
     * 如果是String还会判读是否是空字符串
     *
     * @param object 对象
     * @return
     */
    public static boolean isNotEmpty(Object object) {
        return !isEmpty(object);
    }

}
