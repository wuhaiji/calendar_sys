package com.yuntun.calendar_sys.constant;

/**
 * <p>
 *
 * </p>
 *
 * @author whj
 * @since 2020/11/6
 */
public interface SysUserConstant {
    /**
     * 图片验证码放在session中的key
     */
    String CAPTCHA_SESSION_KEY = "captcha";

    /**
     * 密匙对redis中的key
     */
    String RSA_KEYPAIR_REDIS_KEY = "login_keypair";

    /**
     * 用户token放在redis中的key
     */
    String USER_TOKEN_REDIS_KEY = "user_token:";


    /**
     * 用户token放在redis超时时间 （单位毫秒），总计半个小时
     */
    long USER_TOKEN_REDIS_EXPIRE = 1800_000;


}
