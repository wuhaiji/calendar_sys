package com.yuntun.calendar_sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuntun.calendar_sys.entity.User;
import com.yuntun.calendar_sys.model.bean.UserBean;
import com.yuntun.calendar_sys.model.dto.WechatLoginDto;

import java.util.Map;

/**
 * <p>
 * 小程序用户表 服务类
 * </p>
 *
 * @author whj
 * @since 2020-11-06
 */
public interface IUserService extends IService<User> {

    UserBean getUserInfoMap(WechatLoginDto loginRequest);
}
