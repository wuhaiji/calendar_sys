package com.yuntun.calendar_sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuntun.calendar_sys.entity.HeartWords;

import java.time.LocalDateTime;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author whj
 * @since 2020-11-06
 */
public interface IHeartWordsService extends IService<HeartWords> {

    /**
     * 查询上一条记录
     * @return
     */
    HeartWords selectPrevious(LocalDateTime dateTime);
}
