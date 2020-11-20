package com.yuntun.calendar_sys.controller.sys;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuntun.calendar_sys.constant.HeartWordsConstant;
import com.yuntun.calendar_sys.entity.HeartWords;
import com.yuntun.calendar_sys.exception.ServiceException;
import com.yuntun.calendar_sys.model.bean.HeartWordsBean;
import com.yuntun.calendar_sys.model.code.HeartWordsCode;
import com.yuntun.calendar_sys.model.dto.HeartsBatchWordsDto;
import com.yuntun.calendar_sys.model.dto.HeartsWordsDto;
import com.yuntun.calendar_sys.model.response.Result;
import com.yuntun.calendar_sys.model.response.RowData;
import com.yuntun.calendar_sys.service.IHeartWordsService;
import com.yuntun.calendar_sys.util.EptUtil;
import com.yuntun.calendar_sys.util.ErrorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
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
    public Result<RowData<HeartWordsBean>> getHeartWordsList(Integer pageSize, Integer pageNo, HeartWords dto) {

        ErrorUtil.isNumberValueLt(pageSize, 0, "pageSize");
        ErrorUtil.isNumberValueLt(pageNo, 0, "pageNo");

        IPage<HeartWords> HeartWordsIPage = null;
        try {
            HeartWordsIPage = iHeartWordsService.page(
                    new Page<HeartWords>()
                            .setSize(pageSize)
                            .setCurrent(pageNo),
                    new QueryWrapper<HeartWords>()
                            .eq(EptUtil.isNotEmpty(dto.getUserOpenId()), "user_open_id", dto.getUserOpenId())
                            .eq(EptUtil.isNotEmpty(dto.getTempId()), "temp_id", dto.getTempId())
                            .eq(EptUtil.isNotEmpty(dto.getCreateTime()), "create_time", dto.getCreateTime())
                            .orderByDesc("id")
            );
        } catch (Exception e) {
            log.error("Exception", e);
            throw new ServiceException(HeartWordsCode.LIST_ERROR);
        }

        List<HeartWordsBean> heartWordsBeanList = HeartWordsIPage.getRecords()
                .parallelStream()
                .map(i -> {
                    String content = i.getContent();
                    String[] split = content.split(HeartWordsConstant.CONTENT_DELIMITER);
                    HeartWordsBean heartWordsBean = new HeartWordsBean();
                    BeanUtils.copyProperties(i, heartWordsBean);
                    heartWordsBean.setContentList(Arrays.asList(split));
                    return heartWordsBean;
                }).collect(Collectors.toList());

        RowData<HeartWordsBean> data = RowData.of(HeartWordsBean.class)
                .setRows(heartWordsBeanList)
                .setTotal(HeartWordsIPage.getTotal())
                .setTotalPages(HeartWordsIPage.getTotal());
        return Result.ok(data);
    }


    @PostMapping("/update")
    public Result<Object> update(HeartsWordsDto heartsWordsDto) {

        ErrorUtil.isObjectNull(heartsWordsDto.getId(), "心语id不能为空");
        HeartWords heartWords = new HeartWords();
        BeanUtils.copyProperties(heartsWordsDto, heartWords);

        heartWords.setUserOpenId(null);
        List<String> content = heartsWordsDto.getContent();
        if (content != null) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String s : content) {
                stringBuilder.append(s).append(HeartWordsConstant.CONTENT_DELIMITER);
            }
            heartWords.setContent(stringBuilder.toString());

        }
        try {
            boolean save = iHeartWordsService.updateById(heartWords);
            if (save) {
                return Result.ok();
            }
            throw new Exception();
        } catch (Exception e) {
            log.error("Exception", e);
            throw new ServiceException(HeartWordsCode.UPDATE_ERROR);
        }
    }

    @PostMapping("/delete/{id}")
    public Result<Object> delete(@PathVariable("id") Integer id) {
        ErrorUtil.isObjectNull(id, "信息id");
        boolean b;
        try {
            b = iHeartWordsService.removeById(id);
            if (b) return Result.ok();
            return Result.error("删除失败");
        } catch (Exception e) {
            log.error("Exception", e);
            throw new ServiceException(HeartWordsCode.DELETE_ERROR);
        }

    }

    @PostMapping("/batch/update")
    public Result<Object> batchReview(HeartsBatchWordsDto dto) {

        ErrorUtil.isObjectNull(dto.getIds(), "心语集合");
        ErrorUtil.isObjectNull(dto.getDisable(), "心语审核状态");

        List<HeartWords> heartWords = dto.getIds().parallelStream()
                .map(i -> new HeartWords().setId(i).setDisable(dto.getDisable()))
                .collect(Collectors.toList());
        boolean b;
        try {
            b = iHeartWordsService.updateBatchById(heartWords);
            if (b) return Result.ok();
            return Result.error("审核失败");
        } catch (Exception e) {
            log.error("Exception", e);
            throw new ServiceException(HeartWordsCode.UPDATE_BATCH_ERROR);
        }
    }
}
