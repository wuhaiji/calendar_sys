package com.yuntun.calendar_sys.controller.wechat;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuntun.calendar_sys.entity.HeartWords;
import com.yuntun.calendar_sys.exception.ServiceException;
import com.yuntun.calendar_sys.model.response.Result;
import com.yuntun.calendar_sys.model.response.RowData;
import com.yuntun.calendar_sys.service.IHeartWordsService;
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
@RequestMapping("/wechat/heart-words")
public class HeartWordsController {

    @Autowired
    IHeartWordsService iHeartWordsService;

    @GetMapping("/list")
    public Result<RowData<HeartWords>> getheartWordsList(Integer pageSize, Integer pageNo, HeartWords dto) {
        ErrorUtil.isNumberValueLt(pageSize, 0, "pageSize");
        ErrorUtil.isNumberValueLt(pageNo, 0, "pageNo");


        IPage<HeartWords> heartWordsIPage = iHeartWordsService.page(
                new Page<HeartWords>()
                        .setSize(pageSize)
                        .setCurrent(pageNo),
                new QueryWrapper<HeartWords>()
                        .eq(EptUtil.isNotEmpty(dto.getUsername()), "username", dto.getUsername())
                        .eq(EptUtil.isNotEmpty(dto.getUserId()), "user_id", dto.getUserId())
                        .eq(EptUtil.isNotEmpty(dto.getTempId()), "temp_id", dto.getTempId())
                        .eq(EptUtil.isNotEmpty(dto.getCreateTime()), "create_time", dto.getCreateTime())
                        .orderByDesc("id")
        );

        RowData<HeartWords> data = RowData.of(HeartWords.class)
                .setRows(heartWordsIPage.getRecords())
                .setTotal(heartWordsIPage.getTotal())
                .setTotalPages(heartWordsIPage.getTotal());
        return Result.ok(data);
    }

    @PostMapping("/add")
    public Result<Object> add(HeartWords HeartWords) {
        ErrorUtil.isObjectNull(HeartWords, "参数");
        ErrorUtil.isStringEmpty(HeartWords.getPicUrl(), "图片地址");
        ErrorUtil.isStringEmpty(HeartWords.getUsername(), "用户名称");
        ErrorUtil.isObjectNull(HeartWords.getUserId(), "用户id");
        ErrorUtil.isObjectNull(HeartWords.getTempId(), "模板id");
        ErrorUtil.isStringEmpty(HeartWords.getContent(), "内容不能为空");
        ErrorUtil.isStringEmpty(HeartWords.getSource(), "来源不能为空，用户自建的用用户的名称");

        boolean save = iHeartWordsService.save(HeartWords);

        return Result.ok();
    }

    @PostMapping("/update")
    public Result<Object> update(HeartWords HeartWords) {
        ErrorUtil.isObjectNull(HeartWords, "参数");
        ErrorUtil.isObjectNull(HeartWords.getId(), "心语id不能为空");
        try {
            boolean save = iHeartWordsService.updateById(HeartWords);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException();

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
