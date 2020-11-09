package com.yuntun.calendar_sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuntun.calendar_sys.entity.Permission;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author whj
 * @since 2020-11-06
 */
public interface IPermissionService extends IService<Permission> {

    List<Permission> getUserPermissions(Integer userId);
}
