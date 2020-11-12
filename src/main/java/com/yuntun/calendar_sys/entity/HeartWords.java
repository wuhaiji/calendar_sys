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
@TableName("tb_cl_heart_words")
public class HeartWords implements Serializable{

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 信息创建用户id
     */
    private String userOpenId;

    /**
     *  心语全图
     */
    private String imageUrl;

    /**
     * 用户自定义上传的图片地址
     */
    private String picUrl;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 来源
     */
    private String source;

    /**
     * 模板id
     */
    private Integer tempId;

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
     * 修改人后台id
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
     * 删除人后台id
     */
    private Integer deletedBy;

    /**
     * 审核状态 0.审核通过 1.未通过
     */
    private Integer disable;


}
