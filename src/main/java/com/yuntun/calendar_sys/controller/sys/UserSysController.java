package com.yuntun.calendar_sys.controller.sys;


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
@RequestMapping("/sys/user")
public class UserSysController {

    private static final Logger log = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

    @Autowired
    IUserService iUserService;

    @GetMapping("/list")
    public Result<RowData<UserBean>> list(Integer pageSize, Integer pageNo, User user) {

        ErrorUtil.isNumberValueLt(pageSize, 0, "pageSize");
        ErrorUtil.isNumberValueLt(pageNo, 0, "pageNo");

        IPage<User> iPage;
        try {
            iPage = iUserService.page(
                    new Page<User>()
                            .setSize(pageSize)
                            .setCurrent(pageNo),
                    new QueryWrapper<User>()
                            .eq(EptUtil.isNotEmpty(user.getNickname()), "nick_name", user.getNickname())
                            .eq(EptUtil.isNotEmpty(user.getMobile()), "mobile", user.getMobile())
                            .eq(EptUtil.isNotEmpty(user.getAvatarUrl()), "create_time", user.getAvatarUrl())
                            .eq(EptUtil.isNotEmpty(user.getCity()), "create_time", user.getCity())
                            .eq(EptUtil.isNotEmpty(user.getCountry()), "country", user.getCountry())
                            .eq(EptUtil.isNotEmpty(user.getGender()), "gender", user.getGender())
                            .orderByDesc("id")
            );
        } catch (Exception e) {
            throw new ServiceException(UserCode.LIST_USER_FAILURE);
        }

        //转换成userBean
        List<User> records = iPage.getRecords();
        List<UserBean> userBeans = records.parallelStream().map(i -> {
            UserBean userBean = new UserBean();
            BeanUtils.copyProperties(i, userBean);
            return userBean;
        }).collect(Collectors.toList());

        RowData<UserBean> data = new RowData<UserBean>()
                .setRows(userBeans)
                .setTotal(iPage.getTotal())
                .setTotalPages(iPage.getTotal());
        return Result.ok(data);
    }

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

    @PostMapping("/add")
    public Result<Object> add(User user) {
        ErrorUtil.isObjectNull(user, "参数");
        ErrorUtil.isObjectNull(user.getUnionId(), "小程序unionId");

        try {
            boolean save = iUserService.save(user);
            if (save)
                return Result.ok();
            return Result.error(UserCode.ADD_USER_FAILURE);
        } catch (Exception e) {
            log.error("异常:", e);
            throw new ServiceException(UserCode.ADD_USER_FAILURE);
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
}
