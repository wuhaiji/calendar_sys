package com.yuntun.calendar_sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuntun.calendar_sys.entity.Permission;
import com.yuntun.calendar_sys.entity.Role;
import com.yuntun.calendar_sys.entity.RolePermission;
import com.yuntun.calendar_sys.entity.SysUser;
import com.yuntun.calendar_sys.exception.ServiceException;
import com.yuntun.calendar_sys.mapper.PermissionMapper;
import com.yuntun.calendar_sys.mapper.RoleMapper;
import com.yuntun.calendar_sys.mapper.RolePermissionMapper;
import com.yuntun.calendar_sys.mapper.SysUserMapper;
import com.yuntun.calendar_sys.model.code.PermissionCode;
import com.yuntun.calendar_sys.service.IPermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author whj
 * @since 2020-11-06
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements IPermissionService {

    private static final Logger log = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());


    @Autowired
    SysUserMapper sysUserMapper;

    @Autowired
    RoleMapper roleMapper;

    @Autowired
    RolePermissionMapper rolePermissionMapper;

    @Autowired
    PermissionMapper permissionMapper;

    @Override
    public List<Permission> getUserPermissions(Integer userId) {
        List<Permission> permissionList = null;
        try {
            SysUser sysUser = sysUserMapper.selectById(userId);
            Integer roleId = sysUser.getRoleId();
            Role role = roleMapper.selectById(roleId);
            List<RolePermission> rolePermissionList = rolePermissionMapper.selectList(new QueryWrapper<RolePermission>().eq("role_id", role.getId()));
            List<Integer> permissionIds = rolePermissionList.parallelStream().map(RolePermission::getPermissionId).collect(Collectors.toList());
            permissionList = permissionMapper.selectList(new QueryWrapper<Permission>().in("id", permissionIds));
        } catch (Exception e) {
            log.error("查询用户权限异常：", e);
            throw new ServiceException(PermissionCode.LIST_PERMISSION_BY_USERID_ERROR);
        }
        return permissionList;
    }
}
