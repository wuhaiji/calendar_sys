package com.yuntun.calendar_sys.controller.wechat;


import com.alibaba.fastjson.JSON;
import com.yuntun.calendar_sys.entity.User;
import com.yuntun.calendar_sys.exception.ServiceException;
import com.yuntun.calendar_sys.model.bean.UserBean;
import com.yuntun.calendar_sys.model.code.UserCode;
import com.yuntun.calendar_sys.model.dto.WechatLoginDto;
import com.yuntun.calendar_sys.model.response.Result;
import com.yuntun.calendar_sys.service.IUserService;
import com.yuntun.calendar_sys.util.EptUtil;
import com.yuntun.calendar_sys.util.ErrorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


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
        ErrorUtil.isObjectNull(id, "用户id");
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
        log.info("用户登录参数:{}", JSON.toJSONString(loginRequest));
        UserBean userInfoMap = iUserService.getUserInfoMap(loginRequest);
        log.info("小程序登录返回用户信息：{}", JSON.toJSONString(userInfoMap));
        return Result.ok(userInfoMap);


    }

}
