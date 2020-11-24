package com.yuntun.calendar_sys.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author whj
 * @since 2020/11/10
 */
@Data
public class HeartsWordsDto  {

    Integer pageSize;

    Integer pageNo;

    /**
     * 心语id
     */
    Integer id;

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
    private List<String> content;

    /**
     * 来源
     */
    private String source;

    /**
     * 模板id
     */
    private Integer tempId;

    /**
     * 审核状态 0.审核通过 1.未通过
     */
    private Integer disable;

    private Long month;

    private String date;


}
