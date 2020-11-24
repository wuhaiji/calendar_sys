package com.yuntun.calendar_sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
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
@TableName("tb_cl_temp")
public class Temp implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 模板图片地址
     */
    private String tempPicUrl;

    /**
     * 模板标题
     */
    private String tempTitle;

    /**
     * 模板内容
     */
    private String tempContent;

    /**
     * 模板内容引用来源
     */
    private String tempSource;

    /**
     * 模板内容引用来源
     */
    private Integer tempId;

    /**
     * 农历
     */
    private String lunar;

    /**
     * 心语发布时间
     */
    private LocalDate publishTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建者管理员id
     */
    private Integer creator;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 修改者管理员id
     */
    private Integer updator;

    /**
     * 删除状态 0.未删除 1.删除
     */
    private Integer deleted;

    /**
     * 删除时间
     */
    private LocalDateTime deleteTime;

    /**
     * 删除者管理员id
     */
    private Integer deletedBy;


}
