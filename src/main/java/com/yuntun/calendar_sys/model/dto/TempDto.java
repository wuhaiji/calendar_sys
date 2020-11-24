package com.yuntun.calendar_sys.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author whj
 * @since 2020/11/18
 */
@Data
@Accessors(chain = true)
public class TempDto {


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
    private List<String> tempContent;

    /**
     * 模板内容引用来源
     */
    private String tempSource;

    /**
     * 布局模板id
     */
    private Integer tempId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate publishTime;

}
