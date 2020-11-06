package com.yuntun.calendar_sys.model.dto;

import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author whj
 * @since 2020/11/6
 */
@Data
public class WechatLoginDto {
    // 微信code
    private String code;
    // 用户非敏感字段
    private UserInfo userInfo;
    // 签名
    private String signature;
    // 用户敏感字段
    private String encryptedData;
    // 解密向量
    private String iv;

    @Data
    public static class UserInfo {
        //别称
        private String nickName;
        //头像地址
        private String avatarUrl;
        //性别
        private Integer gender;
        //城市
        private String city;
        //国际
        private String country;
        //省
        private String province;
    }
}
