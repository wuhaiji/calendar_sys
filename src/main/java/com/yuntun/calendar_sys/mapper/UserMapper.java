package com.yuntun.calendar_sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuntun.calendar_sys.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
* <p>
    * 小程序用户表 Mapper 接口
    * </p>
*
* @author whj
* @since 2020-11-06
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
