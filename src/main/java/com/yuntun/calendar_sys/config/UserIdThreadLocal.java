package com.yuntun.calendar_sys.config;

/**
 * 用于保存用户的Id,每个请求对应一个
 * @author whj
 */
public class UserIdThreadLocal {
    private static final ThreadLocal<Long> userTl = new ThreadLocal<>();

    public static void set(Long userId) {
        userTl.set(userId);
    }

    public static Long get() {
        return userTl.get();
    }

    public static void clear(){
        userTl.remove();
    }
}
