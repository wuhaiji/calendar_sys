package com.yuntun.calendar_sys.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author whj
 * @since 2020/11/5
 */
@Data
@RequiredArgsConstructor(staticName = "of")
@Accessors(chain = true)
public class InfoDto {
    /**
     * 信息创建用户id
     */
    private Long userId;

    /**
     * 信息创建用户名
     */
    private String username;

    /**
     * 模板id
     */
    private Long tempId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
