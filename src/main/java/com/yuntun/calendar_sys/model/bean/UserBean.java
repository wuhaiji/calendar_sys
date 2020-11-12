package com.yuntun.calendar_sys.model.bean;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author whj
 * @since 2020/11/6
 */
@Data
@Accessors(chain = true)
public class UserBean {

    private Integer id;

    /**
     * token
     */
    private String token;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 性别  0-未知、1-男性、2-女性
     */
    private Integer gender;

    /**
     * 所在国家
     */
    private String country;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 小程序openId
     */
    private String openId;

    /**
     * 小程序unionId
     */
    private String unionId;




}
