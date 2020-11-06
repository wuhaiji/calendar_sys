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
     * 阳历
     */
    private String publishTime;

    /**
     * 星期
     */
    private String weekDay;

    /**
     * 农历
     */
    private String lunarCalendar;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建者管理员id
     */
    private Integer creatorId;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 修改者管理员id
     */
    private Integer updatorId;

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
    private Integer deletorId;


}
