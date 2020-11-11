package com.yuntun.calendar_sys.interceptor;

import com.yuntun.calendar_sys.entity.Permission;
import com.yuntun.calendar_sys.entity.Role;
import com.yuntun.calendar_sys.entity.SysUser;
import com.yuntun.calendar_sys.exception.ServiceException;
import com.yuntun.calendar_sys.model.code.SysUserCode;
import com.yuntun.calendar_sys.service.IPermissionService;
import com.yuntun.calendar_sys.service.IRoleService;
import com.yuntun.calendar_sys.service.ISysUserService;
import com.yuntun.calendar_sys.util.EptUtil;
import com.yuntun.calendar_sys.util.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 权限拦截器
 * </p>
 *
 * @author whj
 * @since 2020/11/9
 */
@Component
public class PermissionInterceptor extends HandlerInterceptorAdapter {

    public static final String SYS_USER_PERMISSION_LIST = "SYS_USER_PERMISSION_LIST_";

    private static final Logger log = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
    public static final String SYS_USER_ROLE_TYPE = "SYS_USER_ROLE_TYPE_";

    @Autowired
    IPermissionService iPermissionService;

    @Autowired
    ISysUserService iSysUserService;

    @Autowired
    IRoleService iRoleService;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {

        Integer userId = UserIdHolder.get();

        if (userId == null) {
            log.error("用户Id为null");
            return false;
        }
        //超级管理员不用拦截权限
        if (getUserRoleType(userId)) {
            return true;
        }

        List<String> userPermissionList = getUserPermissionList(userId);
        String requestURI = httpServletRequest.getServletPath();
        log.info("servletPath:" + requestURI);
        boolean flag = false;
        for (String s : userPermissionList) {
            if (s.contains(requestURI)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    private List<String> getUserPermissionList(Integer userId) {
        //查询用户的权限，首先从redis中获取
        List<Object> permissionRedisList = RedisUtils.listGetAll(SYS_USER_PERMISSION_LIST + userId);
        List<String> permissionList = permissionRedisList.parallelStream().map(i -> (String) i).collect(Collectors.toList());
        if (EptUtil.isEmpty(permissionList)) {
            //redis中没有权限列表，就从数据库查询
            List<Permission> userPermissions = iPermissionService.getUserPermissions(userId);
            permissionList = userPermissions.parallelStream().map(Permission::getPermissionUrl).collect(Collectors.toList());
            //存入redis
            RedisUtils.listPushAll(SYS_USER_PERMISSION_LIST + userId, permissionList);
        }
        return permissionList;
    }

    private Boolean getUserRoleType(Integer userId) {
        Boolean roleType = (Boolean) RedisUtils.getValue(SYS_USER_ROLE_TYPE + userId);
        if (roleType == null) {
            Role role;
            SysUser sysUser;
            try {
                sysUser = iSysUserService.getById(userId);
            } catch (Exception e) {
                log.error("Exception:", e);
                throw new ServiceException(SysUserCode.INSUFFICIENT_PERMISSIONS);
            }
            if (sysUser == null) {
                log.error("未查询到用户信息!");
                throw new ServiceException(SysUserCode.INSUFFICIENT_PERMISSIONS);
            }
            try {
                role = iRoleService.getById(sysUser.getRoleId());
            } catch (Exception e) {
                log.error("Exception:", e);
                throw new ServiceException(SysUserCode.INSUFFICIENT_PERMISSIONS);
            }
            if (role == null) {
                log.error("未查询到用户的角色!");
                throw new ServiceException(SysUserCode.INSUFFICIENT_PERMISSIONS);
            }
            Boolean roleTypeNow = role.getRoleType();
            RedisUtils.setValue(SYS_USER_ROLE_TYPE + userId, roleTypeNow);
            return roleTypeNow;
        }
        return roleType;
    }

}
