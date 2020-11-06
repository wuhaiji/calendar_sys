package com.yuntun.calendar_sys.model.bean;

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
public class WechatLoginBean {
    String openid;
    String session_key;
    String unionid;
    Integer errcode;
    String errmsg;
}
