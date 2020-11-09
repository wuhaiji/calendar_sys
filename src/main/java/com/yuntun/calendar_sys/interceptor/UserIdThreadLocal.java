package com.yuntun.calendar_sys.interceptor;

/**
 * 用于保存管理系统用户的Id,每个请求对应一个
 *
 * @author whj
 */
public class UserIdThreadLocal {
    private static final ThreadLocal<Integer> userTl = new ThreadLocal<>();

    public static void set(Integer userId) {
        userTl.set(userId);
    }

    public static Integer get() {
        return userTl.get();
    }

    public static void clear() {
        userTl.remove();
    }
}
