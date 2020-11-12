package com.yuntun.calendar_sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuntun.calendar_sys.entity.HeartWords;
import com.yuntun.calendar_sys.mapper.HeartWordsMapper;
import com.yuntun.calendar_sys.service.IHeartWordsService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author whj
 * @since 2020-11-06
 */
@Service
public class HeartWordsServiceImpl extends ServiceImpl<HeartWordsMapper, HeartWords> implements IHeartWordsService {

    @Override
    public HeartWords selectPrevious(LocalDateTime dateTime) {
        return baseMapper.selectPrevious(dateTime);
    }
}
