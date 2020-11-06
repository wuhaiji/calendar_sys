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
 * 小程序用户表
 * </p>
 *
 * @author whj
 * @since 2020-11-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_cl_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * token
     */
    private String token;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 性别  0-未知、1-男性、2-女性
     */
    private Integer gender;

    /**
     * 所在国家
     */
    private String country;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 小程序openId
     */
    private String openId;

    /**
     * 小程序unionId
     */
    private String unionId;

    /**
     * 创建人id,0表示用户自己创建的账号
     */
    private Integer creator;

    /**
     * 插入时间
     */
    private LocalDateTime createdTime;

    /**
     * 修改人id
     */
    private Integer updator;

    /**
     * 修改时间
     */
    private LocalDateTime updatedTime;

    /**
     * 0.未删除 1.删除
     */
    private Integer deleted;

    /**
     * 删除时间
     */
    private LocalDateTime deleteTime;

    /**
     * 删除人id
     */
    private Integer deletedBy;


}
