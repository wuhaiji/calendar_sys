package com.yuntun.calendar_sys.controller.wechat;


import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuntun.calendar_sys.constant.HeartWordsConstant;
import com.yuntun.calendar_sys.entity.Temp;
import com.yuntun.calendar_sys.exception.ServiceException;
import com.yuntun.calendar_sys.model.bean.TempBean;
import com.yuntun.calendar_sys.model.code.TempCode;
import com.yuntun.calendar_sys.model.dto.TempDto;
import com.yuntun.calendar_sys.model.response.Result;
import com.yuntun.calendar_sys.model.response.RowData;
import com.yuntun.calendar_sys.service.ITempService;
import com.yuntun.calendar_sys.util.EptUtil;
import com.yuntun.calendar_sys.util.ErrorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
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
@RequestMapping("/wechat/open/temp")
public class TempController {

    private static final Logger log = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

    @Autowired
    ITempService iTempService;

    @GetMapping("/list")
    public Result<Object> getList(Integer pageSize, Integer pageNo, TempDto dto) {

        ErrorUtil.isNumberValueLt(pageSize, 0, "pageSize");
        ErrorUtil.isNumberValueLt(pageNo, 0, "pageNo");

        IPage<Temp> tempPage;
        try {
            tempPage = iTempService.page(
                    new Page<Temp>()
                            .setSize(pageSize)
                            .setCurrent(pageNo),
                    new QueryWrapper<Temp>()
                            .le("publish_time", LocalDate.now())
                            .orderByDesc("publish_time")
            );
        } catch (Exception e) {
            log.error("查询官方图文模板列表失败");
            throw new ServiceException(TempCode.LIST_TEMP_FAILURE);
        }

        List<TempBean> tempBeanList = tempPage.getRecords()
                .parallelStream()
                .map(this::getTempBean)
                .collect(Collectors.toList());

        RowData<TempBean> data = RowData.of(TempBean.class)
                .setRows(tempBeanList)
                .setTotal(tempPage.getTotal())
                .setTotalPages(tempPage.getTotal());
        return Result.ok(data);
    }

    @GetMapping("/list/months")
    public Result<RowData<JSONObject>> getListMonth(Integer pageSize, Integer pageNo, String date) {
        ErrorUtil.isNumberValueLt(pageSize, 0, "pageSize");
        ErrorUtil.isNumberValueLt(pageNo, 0, "pageNo");


        QueryWrapper<Temp> queryWrapper = new QueryWrapper<Temp>()
                .le("publish_time", LocalDate.now())
                .orderByDesc("publish_time");

        //指定月份
        if (EptUtil.isNotEmpty(date)) {

            LocalDateTime dateTimeStart = LocalDateTimeUtil.parse(date);
            LocalDateTime dateTimeEnd = dateTimeStart.plus(1, ChronoUnit.MONTHS);

            LocalDateTime start = dateTimeStart.with(TemporalAdjusters.firstDayOfMonth());
            LocalDateTime end = dateTimeEnd.with(TemporalAdjusters.firstDayOfMonth());

            queryWrapper
                    .ge("publish_time", start.toLocalDate())
                    .lt("publish_time", end.toLocalDate())
            ;
        }

        Page<Temp> page = new Page<Temp>().setSize(pageSize).setCurrent(pageNo);
        IPage<Temp> tempIPage;
        try {
            tempIPage = iTempService.page(page, queryWrapper);
        } catch (Exception e) {
            log.error("Exception", e);
            throw new ServiceException(TempCode.LIST_MONTH_FAILURE);
        }

        List<Temp> records = tempIPage.getRecords();
        Map<Integer, List<TempBean>> collect = records.parallelStream()
                .map(this::getTempBean)
                .collect(Collectors.groupingBy(i -> i.getPublishTime().getMonthValue()));

        List<JSONObject> list = collect.entrySet().parallelStream()
                .map(i -> {
                    JSONObject jsonObject = new JSONObject();
                    List<TempBean> tempBeans = i.getValue();
                    jsonObject.put("rows", tempBeans);
                    jsonObject.put("month", i.getKey());
                    jsonObject.put("year", tempBeans.get(0).getPublishTime().getYear());
                    return jsonObject;
                }).collect(Collectors.toList());

        list = list.stream().sorted((o1, o2) -> {
            if (o1.getInteger("year") > (o2.getInteger("year"))) {
                return -1;
            } else if(o1.getInteger("year").equals(o2.getInteger("year"))) {
                if (o1.getInteger("month") > (o2.getInteger("month"))) {
                    return -1;
                } else {
                    return 1;
                }
            }else{
                return 1;
            }
        }).collect(Collectors.toList());


        RowData<JSONObject> data = RowData.of(JSONObject.class)
                .setRows(list)
                .setTotal(tempIPage.getTotal())
                .setTotalPages(tempIPage.getTotal());
        log.info("返回的官方图文月份列表：{}", list);
        return Result.ok(data);
    }

    /**
     * 具体某月的心语列表30条
     */
    @GetMapping("/list/limit30")
    public Result<Object> getTempList30(String date) {

        ErrorUtil.isStringEmpty(date, "日期");

        LocalDateTime dateTimeStart = LocalDateTimeUtil.parse(date);


        //查询当前时间14条
        List<Temp> TempListPrev = iTempService.list(
                new QueryWrapper<Temp>()
                        .orderByAsc("publish_time")
                        .lt("publish_time", dateTimeStart)
                        .le("publish_time", LocalDate.now())
                        .last("limit 14")

        );

        //查询当前时间后15条
        List<Temp> TempListNext = iTempService.list(
                new QueryWrapper<Temp>()
                        .orderByAsc("publish_time")
                        .ge("publish_time", dateTimeStart)
                        .le("publish_time", LocalDate.now())
                        .last("limit 15")

        );

        ArrayList<Temp> Temp = new ArrayList<>();
        Temp.addAll(TempListPrev);
        Temp.addAll(TempListNext);
        Collections.reverse(Temp);
        List<TempBean> collect = Temp.parallelStream()
                //过滤掉不属于当月的数据
                .filter(i -> i.getCreateTime().getMonthValue() == (dateTimeStart.getMonthValue()))
                .map(this::getTempBean)
                .collect(Collectors.toList());
        log.info("30条数据返回结果：{}", JSON.toJSONString(collect));
        return Result.ok(collect);
    }


    @GetMapping("/detail/{id}")
    public Result<Object> detail(@PathVariable("id") Integer id) {

        ErrorUtil.isNumberValueLt(id, 0, "参数");
        try {
            Temp temp = iTempService.getById(id);
            if (EptUtil.isNotEmpty(temp)) {
                TempBean tempBean = getTempBean(temp);
                return Result.ok(tempBean);
            }
            return Result.error(TempCode.DETAIL_TEMP_FAILURE);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("异常:", e);
            throw new ServiceException(TempCode.DELETE_TEMP_FAILURE);
        }
    }

    private TempBean getTempBean(Temp temp) {
        TempBean tempBean = new TempBean();
        String[] split = temp.getTempContent().split(HeartWordsConstant.CONTENT_DELIMITER);
        BeanUtils.copyProperties(temp, tempBean);
        tempBean.setTempContent(Arrays.asList(split));
        tempBean.setCreateTime(temp.getPublishTime().atStartOfDay());
        return tempBean;
    }

}
