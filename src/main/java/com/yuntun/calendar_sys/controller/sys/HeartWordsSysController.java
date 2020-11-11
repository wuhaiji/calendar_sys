package com.yuntun.calendar_sys.controller.sys;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuntun.calendar_sys.entity.HeartWords;
import com.yuntun.calendar_sys.exception.ServiceException;
import com.yuntun.calendar_sys.model.code.HeartWordsCode;
import com.yuntun.calendar_sys.model.response.Result;
import com.yuntun.calendar_sys.model.response.RowData;
import com.yuntun.calendar_sys.service.IHeartWordsService;
import com.yuntun.calendar_sys.util.EptUtil;
import com.yuntun.calendar_sys.util.ErrorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author whj
 * @since 2020-11-05
 */
@RestController
@RequestMapping("/sys/heartwords")
public class HeartWordsSysController {

    private static final Logger log = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());


    @Autowired
    IHeartWordsService iHeartWordsService;

    @GetMapping("/list")
    public Result<RowData<HeartWords>> getHeartWordsList(Integer pageSize, Integer pageNo, HeartWords dto) {

        ErrorUtil.isNumberValueLt(pageSize, 0, "pageSize");
        ErrorUtil.isNumberValueLt(pageNo, 0, "pageNo");

        IPage<HeartWords> HeartWordsIPage = iHeartWordsService.page(
                new Page<HeartWords>()
                        .setSize(pageSize)
                        .setCurrent(pageNo),
                new QueryWrapper<HeartWords>()
                        .eq(EptUtil.isNotEmpty(dto.getUserId()), "user_id", dto.getUserId())
                        .eq(EptUtil.isNotEmpty(dto.getTempId()), "temp_id", dto.getTempId())
                        .eq(EptUtil.isNotEmpty(dto.getCreateTime()), "create_time", dto.getCreateTime())
                        .orderByDesc("id")
        );
        List<HeartWords> records = HeartWordsIPage.getRecords();

        RowData<HeartWords> data = RowData.of(HeartWords.class)
                .setRows(records)
                .setTotal(HeartWordsIPage.getTotal())
                .setTotalPages(HeartWordsIPage.getTotal());
        log.info("心语集合：{}", data);
        return Result.ok(data);
    }

    @PostMapping("/add")
    public Result<Object> add(HeartWords HeartWords) {
        ErrorUtil.isObjectNull(HeartWords.getUserId(), "用户id");
        ErrorUtil.isStringEmpty(HeartWords.getPicUrl(), "图片地址");
        ErrorUtil.isObjectNull(HeartWords.getTempId(), "模板id");
        ErrorUtil.isStringEmpty(HeartWords.getContent(), "内容不能为空");
        ErrorUtil.isStringEmpty(HeartWords.getSource(), "来源不能为空，用户自建的用用户的名称");

        try {
            boolean save = iHeartWordsService.save(HeartWords);
            if (!save) {
                throw new Exception();
            }
        } catch (Exception e) {
            log.error("Exception:", e);
            throw new ServiceException(HeartWordsCode.ADD_HEART_WORDS_ERROR);
        }

        return Result.ok();
    }

    @PostMapping("/update")
    public Result<Object> update(HeartWords HeartWords) {
        ErrorUtil.isObjectNull(HeartWords, "参数");
        ErrorUtil.isObjectNull(HeartWords.getId(), "心语id不能为空");
        boolean save;
        try {
            save = iHeartWordsService.updateById(HeartWords);
            if (!save) {
                throw new Exception();
            }
        } catch (Exception e) {
           log.error("exception:",e);
            throw new ServiceException(HeartWordsCode.UPDATE_SYSUSER_ERROR);
        }
        return Result.ok();
    }

    @PostMapping("/delete/{id}")
    public Result<Object> delete(@PathVariable("id") Integer id) {
        ErrorUtil.isObjectNull(id, "信息id");
        boolean b = iHeartWordsService.removeById(id);
        if (b) return Result.ok();
        return Result.error("删除失败");
    }
}
