package com.yuntun.calendar_sys.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuntun.calendar_sys.entity.Role;
import com.yuntun.calendar_sys.exception.ServiceException;
import com.yuntun.calendar_sys.model.code.RoleCode;
import com.yuntun.calendar_sys.model.response.Result;
import com.yuntun.calendar_sys.model.response.RowData;
import com.yuntun.calendar_sys.service.IRoleService;
import com.yuntun.calendar_sys.util.EptUtil;
import com.yuntun.calendar_sys.util.ErrorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author whj
 * @since 2020-11-05
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    private static final Logger log = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

    @Autowired
    IRoleService iRoleService;

    @GetMapping("/list")
    public Result<RowData<Role>> list(Integer pageSize, Integer pageNo, Role role) {
        ErrorUtil.isNumberValueLt(pageSize, 0, "pageSize");
        ErrorUtil.isNumberValueLt(pageNo, 0, "pageNo");
        IPage<Role> iPage = iRoleService.page(
                new Page<Role>()
                        .setSize(pageSize)
                        .setCurrent(pageNo),
                new QueryWrapper<Role>()
                        .eq(EptUtil.isNotEmpty(role.getRoleName()), "role_name", role.getRoleName())
                        .eq(EptUtil.isNotEmpty(role.getCreateTime()), "create_time", role.getCreateTime())
                        .orderByDesc("id")
        );

        return Result.ok(
                new RowData<Role>()
                        .setRows(iPage.getRecords())
                        .setTotal(iPage.getTotal())
                        .setTotalPages(iPage.getTotal())
        );
    }

    @GetMapping("/detail/{id}")
    public Result<Object> detail(@PathVariable("id") String id) {
        ErrorUtil.isObjectNull(id, "参数");
        try {
            Role role = iRoleService.getById(id);
            if (EptUtil.isNotEmpty(role))
                return Result.ok(role);
            return Result.error(RoleCode.DETAIL_ROLE_FAILURE);
        } catch (Exception e) {
            log.error("异常:", e);
            throw new ServiceException(RoleCode.DETAIL_ROLE_FAILURE);
        }

    }

    @PostMapping("/add")
    public Result<Object> add(Role role) {
        ErrorUtil.isObjectNull(role, "参数");
        ErrorUtil.isStringEmpty(role.getRoleName(), "角色名");
        try {
            boolean save = iRoleService.save(role);
            if (save)
                return Result.ok();
            return Result.error(RoleCode.ADD_ROLE_FAILURE);
        } catch (Exception e) {
            log.error("异常:", e);
            throw new ServiceException(RoleCode.ADD_ROLE_FAILURE);
        }

    }

    @PostMapping("/update")
    public Result<Object> update(Role role) {

        ErrorUtil.isObjectNull(role, "参数");
        ErrorUtil.isObjectNull(role.getId(), "角色id");

        try {
            boolean save = iRoleService.updateById(role);
            if (save)
                return Result.ok();
            return Result.error(RoleCode.UPDATE_ROLE_FAILURE);
        } catch (Exception e) {
            log.error("异常:", e);
            throw new ServiceException(RoleCode.UPDATE_ROLE_FAILURE);
        }

    }

    @PostMapping("/delete/{id}")
    public Result<Object> delete(@PathVariable("id") Integer id) {
        ErrorUtil.isObjectNull(id, "信息id");
        try {
            boolean b = iRoleService.removeById(id);
            if (b)
                return Result.ok();
            return Result.error(RoleCode.DELETE_ROLE_FAILURE);
        } catch (Exception e) {
            log.error("异常:", e);
            throw new ServiceException(RoleCode.DELETE_ROLE_FAILURE);
        }
    }
}
