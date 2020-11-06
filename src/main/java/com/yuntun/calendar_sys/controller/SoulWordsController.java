package com.yuntun.calendar_sys.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuntun.calendar_sys.entity.SoulWords;
import com.yuntun.calendar_sys.model.response.Result;
import com.yuntun.calendar_sys.model.response.RowData;
import com.yuntun.calendar_sys.service.ISoulWordsService;
import com.yuntun.calendar_sys.util.EptUtil;
import com.yuntun.calendar_sys.util.ErrorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author whj
 * @since 2020-11-05
 */
@RestController
@RequestMapping("/soul-words")
public class SoulWordsController {

    @Autowired
    ISoulWordsService iSoulWordsService;

    @GetMapping("/list")
    public Result<RowData<SoulWords>> getSoulWordsList(Integer pageSize, Integer pageNo, SoulWords dto) {
        ErrorUtil.isNumberValueLt(pageSize, 0, "pageSize");
        ErrorUtil.isNumberValueLt(pageNo, 0, "pageNo");


        IPage<SoulWords> SoulWordsIPage = iSoulWordsService.page(
                new Page<SoulWords>()
                        .setSize(pageSize)
                        .setCurrent(pageNo),
                new QueryWrapper<SoulWords>()
                        .eq(EptUtil.isNotEmpty(dto.getUsername()), "username", dto.getUsername())
                        .eq(EptUtil.isNotEmpty(dto.getUserId()), "user_id", dto.getUserId())
                        .eq(EptUtil.isNotEmpty(dto.getTempId()), "temp_id", dto.getTempId())
                        .eq(EptUtil.isNotEmpty(dto.getCreateTime()), "create_time", dto.getCreateTime())
                        .orderByDesc("id")
        );

        RowData<SoulWords> data = RowData.of(SoulWords.class)
                .setRows(SoulWordsIPage.getRecords())
                .setTotal(SoulWordsIPage.getTotal())
                .setTotalPages(SoulWordsIPage.getTotal());
        return Result.ok(data);
    }

    @PostMapping("/add")
    public Result<Object> add(SoulWords SoulWords) {
        ErrorUtil.isObjectNull(SoulWords, "参数");
        ErrorUtil.isStringEmpty(SoulWords.getPicUrl(), "图片地址");
        ErrorUtil.isStringEmpty(SoulWords.getUsername(), "用户名称");
        ErrorUtil.isObjectNull(SoulWords.getUserId(), "用户id");
        ErrorUtil.isObjectNull(SoulWords.getTempId(), "模板id");
        ErrorUtil.isStringEmpty(SoulWords.getContent(), "内容不能为空");
        ErrorUtil.isStringEmpty(SoulWords.getSource(), "来源不能为空，用户自建的用用户的名称");

        boolean save = iSoulWordsService.save(SoulWords);

        return Result.ok();
    }

    @PostMapping("/update")
    public Result<Object> update(SoulWords SoulWords) {
        ErrorUtil.isObjectNull(SoulWords, "参数");
        ErrorUtil.isObjectNull(SoulWords.getId(), "心语id不能为空");
        boolean save = iSoulWordsService.updateById(SoulWords);
        return Result.ok();
    }

    @PostMapping("/delete/{id}")
    public Result<Object> delete(@PathVariable("id") Integer id) {
        ErrorUtil.isObjectNull(id, "信息id");
        boolean b = iSoulWordsService.removeById(id);
        if (b) return Result.ok();
        return Result.error("删除失败");
    }
}
