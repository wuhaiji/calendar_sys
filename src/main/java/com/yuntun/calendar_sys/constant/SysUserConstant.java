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
     * 图片验证码放在session中的key
     */
    String CAPTCHA_ID_REDIS_KEY = "hw_captcha:";

    /**
     * 密匙对redis中的key
     */
    String RSA_KEYPAIR_REDIS_KEY = "hw_login_keypair:";

    /**
     * 用户token放在redis中的key
     */
    String USER_TOKEN_REDIS_KEY = "hw_user_token:";


    /**
     * 用户token放在redis超时时间 （单位毫秒），总计1个小时
     */
    long USER_TOKEN_REDIS_EXPIRE = 3600;

    /**
     * 未禁用
     */
    Integer NOT_DISABLED = 1;

    /**
     * 未禁用
     */
    Integer IS_DISABLED = 0;
}
