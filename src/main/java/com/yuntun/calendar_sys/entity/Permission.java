package com.yuntun.calendar_sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author whj
 * @since 2020-11-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_cl_permission")
public class Permission implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 权限名称
     */
    private String permissionName;

    /**
     * 权限标识
     */
    private String permissionTag;

    /**
     * 权限父级id  0:顶级菜单
     */
    private Integer parentId;

    /**
     * 权限类型：1.菜单 2.按钮
     */
    private Integer permissionType;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建者后台id
     */
    private Integer creator;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 修改者后台id
     */
    private Integer updator;

    /**
     * 0.未删除 1.删除
     */
    private Integer deleted;

    /**
     * 删除者后台id
     */
    private Integer deletedBy;

    /**
     * 删除时间
     */
    private LocalDateTime deleteTime;


}
