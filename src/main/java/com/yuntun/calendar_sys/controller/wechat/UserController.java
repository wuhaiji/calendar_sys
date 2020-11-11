package com.yuntun.calendar_sys.controller.wechat;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuntun.calendar_sys.entity.User;
import com.yuntun.calendar_sys.exception.ServiceException;
import com.yuntun.calendar_sys.model.bean.UserBean;
import com.yuntun.calendar_sys.model.code.UserCode;
import com.yuntun.calendar_sys.model.dto.WechatLoginDto;
import com.yuntun.calendar_sys.model.response.Result;
import com.yuntun.calendar_sys.model.response.RowData;
import com.yuntun.calendar_sys.service.IUserService;
import com.yuntun.calendar_sys.util.EptUtil;
import com.yuntun.calendar_sys.util.ErrorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


/**
 * <p>
 * 小程序用户表 前端控制器
 * </p>
 *
 * @author whj
 * @since 2020-11-05
 */
@RestController
@RequestMapping("/wechat/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

    @Autowired
    IUserService iUserService;

    @GetMapping("/detail/{id}")
    public Result<Object> detail(@PathVariable("id") String id) {
        ErrorUtil.isObjectNull(id, "参数");
        try {
            User User = iUserService.getById(id);
            if (EptUtil.isNotEmpty(User))
                return Result.ok(User);
            return Result.error(UserCode.DETAIL_USER_FAILURE);
        } catch (Exception e) {
            log.error("异常:", e);
            throw new ServiceException(UserCode.DETAIL_USER_FAILURE);
        }

    }

    @PostMapping("/update")
    public Result<Object> update(User User) {

        ErrorUtil.isObjectNull(User, "参数");
        ErrorUtil.isObjectNull(User.getId(), "角色id");

        try {
            boolean save = iUserService.updateById(User);
            if (save)
                return Result.ok();
            return Result.error(UserCode.UPDATE_USER_FAILURE);
        } catch (Exception e) {
            log.error("异常:", e);
            throw new ServiceException(UserCode.UPDATE_USER_FAILURE);
        }
    }

    @PostMapping("/delete/{id}")
    public Result<Object> delete(@PathVariable("id") Integer id) {
        ErrorUtil.isObjectNull(id, "信息id");
        try {
            boolean b = iUserService.removeById(id);
            if (b)
                return Result.ok();
            return Result.error(UserCode.DELETE_USER_FAILURE);
        } catch (Exception e) {
            log.error("异常:", e);
            throw new ServiceException(UserCode.DELETE_USER_FAILURE);
        }
    }

    @PostMapping("/login")
    public Result<Object> login(@RequestBody WechatLoginDto loginRequest) {
        UserBean userInfoMap = iUserService.getUserInfoMap(loginRequest);
        return Result.ok(userInfoMap);


    }

    public static void main(String[] args) {
        WechatLoginDto wechatLoginDto = new WechatLoginDto();
        wechatLoginDto.setCode("");
        wechatLoginDto.setEncryptedData("...");
        wechatLoginDto.setIv("...");
        wechatLoginDto.setSignature("...");

        WechatLoginDto.UserInfo userInfo = new WechatLoginDto.UserInfo();
        userInfo.setAvatarUrl("头像地址");
        userInfo.setCity("城市");
        userInfo.setCountry("国家");
        userInfo.setProvince("省");
        userInfo.setGender(1);
        userInfo.setNickName("昵称");
        wechatLoginDto.setUserInfo(userInfo);
        System.out.println(JSON.toJSON(wechatLoginDto));
    }
}
