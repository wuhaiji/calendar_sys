package com.yuntun.calendar_sys.model.bean;

import com.yuntun.calendar_sys.constant.HeartWordsConstant;
import com.yuntun.calendar_sys.entity.Temp;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author whj
 * @since 2020/11/18
 */
@Accessors(chain = true)
@Data
public class TempBean {

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
     * 创建时间
     */
    private LocalDateTime createTime;

    private LocalDate publishTime;

    /**
     * 农历
     */
    private String lunar;

    /**
     * 模板id
     */
    private Integer tempId;

    public static TempBean of() {
        return new TempBean();
    }

}
