package com.yuntun.calendar_sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuntun.calendar_sys.entity.SysUser;
import com.yuntun.calendar_sys.mapper.SysUserMapper;
import com.yuntun.calendar_sys.service.ISysUserService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 后台管理系统用户表 服务实现类
 * </p>
 *
 * @author whj
 * @since 2020-11-06
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

}
