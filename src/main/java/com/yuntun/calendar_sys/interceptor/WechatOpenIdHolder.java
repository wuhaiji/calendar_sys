package com.yuntun.calendar_sys.interceptor;

/**
 * <p>
 *   小程序用户openId holder
 * </p>
 *
 * @author whj
 * @since 2020/11/9
 */
public class WechatOpenIdHolder {

    private static final ThreadLocal<String> openIdThreadLocal = new ThreadLocal<>();

    public static void set(String openId) {
        openIdThreadLocal.set(openId);
    }

    public static String get() {
        return openIdThreadLocal.get();
    }

    public static void clear() {
        openIdThreadLocal.remove();
    }
}
