package com.yuntun.calendar_sys.model.bean;

import lombok.Data;
import lombok.experimental.Accessors;

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
@Accessors(chain = true)
public class HeartWordsBean {

    /**
     * 心语id
     */
    private Integer id;

    /**
     * 信息创建用户id
     */
    private Integer userId;

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
    private List<String> contentList;

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


}
