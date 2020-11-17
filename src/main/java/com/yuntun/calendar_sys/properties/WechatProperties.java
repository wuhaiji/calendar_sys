package com.yuntun.calendar_sys.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * <p>
 *
 * </p>
 *
 * @author whj
 * @since 2020/11/10
 */
@ConfigurationProperties(prefix = "wechat.applets")
@Component
@Data
public class WechatProperties {

    String grant_type = "authorization_code";
    String wechatLoginUrl = "https://api.weixin.qq.com/sns/jscode2session";
    String appid;
    String secret;
}
