package com.yuntun.calendar_sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuntun.calendar_sys.entity.RolePermission;
import org.apache.ibatis.annotations.Mapper;

/**
* <p>
    * 角色权限关联表 Mapper 接口
    * </p>
*
* @author whj
* @since 2020-11-09
*/
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {

}
