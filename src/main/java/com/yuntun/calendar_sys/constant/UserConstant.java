package com.yuntun.calendar_sys.constant;

/**
 * <p>
 *
 * </p>
 *
 * @author whj
 * @since 2020/11/6
 */
public interface UserConstant {
    /**
     * 小程序用户创建分布式锁名
     */
    String INSERT_USER_DISTRIBUTED_LOCK_PREFIX = "wechat_login_redis_lock";
    /**
     * 小程序token放在redis中的key
     */
    String WECHAT_TOKEN_REDIS_KEY = "wechat_token";
    /**
     * 用户token放在redis超时时间 毫秒
     */
    long USER_TOKEN_REDIS_EXPIRE = 3600_000;

}
