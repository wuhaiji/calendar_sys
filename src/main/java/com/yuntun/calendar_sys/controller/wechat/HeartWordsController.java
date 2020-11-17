package com.yuntun.calendar_sys.controller.wechat;


import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuntun.calendar_sys.constant.HeartWordsConstant;
import com.yuntun.calendar_sys.entity.HeartWords;
import com.yuntun.calendar_sys.entity.User;
import com.yuntun.calendar_sys.exception.ServiceException;
import com.yuntun.calendar_sys.interceptor.WechatOpenIdHolder;
import com.yuntun.calendar_sys.model.bean.HeartWordsBean;
import com.yuntun.calendar_sys.model.code.HeartWordsCode;
import com.yuntun.calendar_sys.model.dto.HeartsWordsDto;
import com.yuntun.calendar_sys.model.response.Result;
import com.yuntun.calendar_sys.model.response.RowData;
import com.yuntun.calendar_sys.service.IHeartWordsService;
import com.yuntun.calendar_sys.service.IUserService;
import com.yuntun.calendar_sys.util.EptUtil;
import com.yuntun.calendar_sys.util.ErrorUtil;
import com.yuntun.calendar_sys.util.LDTUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/wechat/heartwords")
public class HeartWordsController {

    private static final Logger log = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());


    @Autowired
    IHeartWordsService iHeartWordsService;
    @Autowired
    IUserService iUserService;

    /**
     * 首页用户心语列表
     *
     * @param dto
     * @return
     */
    @GetMapping("/list")
    public Result<RowData<HeartWordsBean>> getHeartWordsList(HeartsWordsDto dto) {

        ErrorUtil.isNumberValueLt(dto.getPageSize(), 0, "pageSize");
        ErrorUtil.isNumberValueLt(dto.getPageNo(), 0, "pageNo");
        String openId = WechatOpenIdHolder.get();
        IPage<HeartWords> heartWordsIPage = iHeartWordsService.page(
                new Page<HeartWords>()
                        .setSize(dto.getPageSize())
                        .setCurrent(dto.getPageNo()),
                new QueryWrapper<HeartWords>()
                        //只给用户看见审核通过的心语
                        .eq("disable", HeartWordsConstant.EXAMINATION_PASSED)
                        .eq(openId != null, "user_open_id", openId)
                        .orderByDesc("create_time")
        );

        List<HeartWords> records = heartWordsIPage.getRecords();
        List<HeartWordsBean> heartWordsBeanList = records.parallelStream().map(i -> {
            String content = i.getContent();
            String[] split = content.split(HeartWordsConstant.CONTENT_DELIMITER);
            HeartWordsBean heartWordsBean = new HeartWordsBean();
            BeanUtils.copyProperties(i, heartWordsBean);
            heartWordsBean.setContentList(Arrays.asList(split));
            return heartWordsBean;

        }).collect(Collectors.toList());


        RowData<HeartWordsBean> data = RowData.of(HeartWordsBean.class)
                .setRows(heartWordsBeanList)
                .setTotal(heartWordsIPage.getTotal())
                .setTotalPages(heartWordsIPage.getTotal());
        return Result.ok(data);
    }

    /**
     * 具体某月的心语列表30条
     */
    @GetMapping("/list/limit30")
    public Result<Object> getHeartWordsList30(HeartsWordsDto dto) {

        ErrorUtil.isStringEmpty(dto.getDate(), "日期");

        String openId = WechatOpenIdHolder.get();

        LocalDateTime dateTimeStart = LocalDateTimeUtil.parse(dto.getDate());

        //查询前15条
        List<HeartWords> heartWordsListPrev = iHeartWordsService.list(
                new QueryWrapper<HeartWords>()
                        //只给用户看见审核通过的心语
                        .eq("disable", HeartWordsConstant.EXAMINATION_PASSED)
                        .eq(openId != null, "user_open_id", openId)
                        .orderByDesc("create_time")
                        .ge("create_time", dateTimeStart)
                        .last("limit 15")

        );
        //查询后14条
        List<HeartWords> heartWordsListNext = iHeartWordsService.list(
                new QueryWrapper<HeartWords>()
                        //只给用户看见审核通过的心语
                        .eq("disable", HeartWordsConstant.EXAMINATION_PASSED)
                        .eq(openId != null, "user_open_id", openId)
                        .orderByDesc("create_time")
                        .lt("create_time", dateTimeStart)
                        .last("limit 14")

        );
        ArrayList<HeartWords> heartWords = new ArrayList<>();
        heartWords.addAll(heartWordsListPrev);
        heartWords.addAll(heartWordsListNext);
        List<HeartWordsBean> collect = heartWords.parallelStream()
                //过滤掉不属于当月的数据
                .filter(i -> i.getCreateTime().getMonthValue() == (dateTimeStart.getMonthValue()))
                .map(i -> {
                    HeartWordsBean heartWordsBean = new HeartWordsBean();
                    heartWordsBean.setId(i.getId());
                    heartWordsBean.setImageUrl(i.getImageUrl());
                    heartWordsBean.setCreateTime(i.getCreateTime());
                    heartWordsBean.setDayOfMonth(i.getCreateTime().getDayOfMonth());
                    BeanUtils.copyProperties(i, heartWordsBean);
                    String content = i.getContent();
                    String[] split = content.split(HeartWordsConstant.CONTENT_DELIMITER);
                    heartWordsBean.setContentList(Arrays.asList(split));
                    return heartWordsBean;
                })
                .collect(Collectors.toList());
        return Result.ok(collect);
    }

    @GetMapping("/list/months")
    public Result<RowData<JSONObject>> getHeartWordsListMonth(HeartsWordsDto dto) {

        String openId = WechatOpenIdHolder.get();

        QueryWrapper<HeartWords> queryWrapper = new QueryWrapper<HeartWords>()
                //只给用户看见审核通过的心语
                .eq("disable", HeartWordsConstant.EXAMINATION_PASSED)
                .eq(openId != null, "user_open_id", openId)
                .orderByDesc("create_time");

        Long monthTimeStamp = dto.getMonth();
        String date = dto.getDate();
        if (monthTimeStamp != null) {
            LocalDateTime dateTimeStart = LDTUtils.timestampToLocalDatetime(monthTimeStamp);
            log.info("要查询的月份：" + dateTimeStart);
            LocalDateTime dateTimeEnd = dateTimeStart.plus(1, ChronoUnit.MONTHS);
            LocalDateTime start = dateTimeStart.with(TemporalAdjusters.firstDayOfMonth());
            LocalDateTime end = dateTimeEnd.with(TemporalAdjusters.firstDayOfMonth());
            queryWrapper.ge("create_time", start).lt("create_time", end);
        }

        //指定月份
        if (EptUtil.isNotEmpty(date)) {

            LocalDateTime dateTimeStart = LocalDateTimeUtil.parse(date);
            LocalDateTime dateTimeEnd = dateTimeStart.plus(1, ChronoUnit.MONTHS);

            LocalDateTime start = dateTimeStart.with(TemporalAdjusters.firstDayOfMonth());
            LocalDateTime end = dateTimeEnd.with(TemporalAdjusters.firstDayOfMonth());

            queryWrapper.ge("create_time", start.toLocalDate()).lt("create_time", end.toLocalDate());
        }

        IPage<HeartWords> heartWordsIPage = iHeartWordsService.page(
                new Page<HeartWords>()
                        .setSize(dto.getPageSize())
                        .setCurrent(dto.getPageNo()),
                queryWrapper
        );

        List<HeartWords> records = heartWordsIPage.getRecords();
        Map<Integer, List<HeartWordsBean>> collect = records.parallelStream()
                .map(i -> {
                    HeartWordsBean heartWordsBean = new HeartWordsBean();
                    heartWordsBean.setId(i.getId());
                    heartWordsBean.setImageUrl(i.getImageUrl());
                    heartWordsBean.setCreateTime(i.getCreateTime());
                    heartWordsBean.setDayOfMonth(i.getCreateTime().getDayOfMonth());
                    BeanUtils.copyProperties(i, heartWordsBean);
                    String content = i.getContent();
                    String[] split = content.split(HeartWordsConstant.CONTENT_DELIMITER);
                    heartWordsBean.setContentList(Arrays.asList(split));
                    return heartWordsBean;
                })
                .collect(Collectors.groupingBy(i -> i.getCreateTime().getMonthValue()));

        List<JSONObject> list = collect.entrySet().parallelStream()
                .map(i -> {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("rows", i.getValue());
                    jsonObject.put("month", i.getKey());
                    return jsonObject;
                }).collect(Collectors.toList());

        Collections.reverse(list);

        RowData<JSONObject> data = RowData.of(JSONObject.class)
                .setRows(list)
                .setTotal(heartWordsIPage.getTotal())
                .setTotalPages(heartWordsIPage.getTotal());
        return Result.ok(data);
    }

    public static void main(String[] args) {

    }

    @PostMapping("/add")
    public Result<Object> add(@RequestBody HeartsWordsDto dto) {

        List<String> content = dto.getContent();

        ErrorUtil.isStringEmpty(dto.getPicUrl(), "心语内容图片");
        ErrorUtil.isStringEmpty(dto.getImageUrl(), "心语概览图");
        ErrorUtil.isObjectNull(dto.getUserOpenId(), "用户id");
        ErrorUtil.isObjectNull(dto.getTempId(), "模板id");
        ErrorUtil.isListEmpty(content, "内容不能为空");
        ErrorUtil.isStringEmpty(dto.getSource(), "来源不能为空，用户自建的用用户的名称");


        //心语转为一句字符串,用"@#@"分隔
        HeartWords heartWords = new HeartWords();
        BeanUtils.copyProperties(dto, heartWords);
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : content) {
            stringBuilder.append(s).append(HeartWordsConstant.CONTENT_DELIMITER);
        }
        String substring = stringBuilder.substring(0, stringBuilder.length() - HeartWordsConstant.CONTENT_DELIMITER.length());
        heartWords.setContent(substring);

        String openId = WechatOpenIdHolder.get();
        log.info("openId:{}", openId);
        if (EptUtil.isEmpty(openId)) {
            throw new ServiceException("获取openId异常");
        }
        //查询用户id
        User user = iUserService.getOne(
                new QueryWrapper<User>()
                        .eq(
                                "open_id",
                                openId
                        )
        );

        if (user == null) {
            throw new ServiceException(HeartWordsCode.ADD_HEART_WORDS_ERROR);
        }
        heartWords.setCreator(user.getId());
        LocalDateTime now = LocalDateTime.now();
        System.out.println("心语创建时间:" + now);
        heartWords.setCreateTime(now);

        //保存
        try {
            boolean save = iHeartWordsService.save(heartWords);
            if (save) {
                HeartWords byId = iHeartWordsService.getById(heartWords.getId());

                HeartWordsBean heartWordsBean = new HeartWordsBean();
                heartWordsBean.setId(byId.getId());
                heartWordsBean.setCreateTime(byId.getCreateTime());
                heartWordsBean.setImageUrl(byId.getImageUrl());
                log.info("HeartWords:{}", byId);
                return Result.ok(heartWordsBean);
            }
            throw new Exception();
        } catch (Exception e) {
            log.error("Exception", e);
            throw new ServiceException(HeartWordsCode.ADD_HEART_WORDS_ERROR);
        }

    }

    @PostMapping("/delete/{id}")
    public Result<Object> delete(@PathVariable("id") Integer id) {
        ErrorUtil.isObjectNull(id, "信息id");
        boolean b = iHeartWordsService.removeById(id);
        if (b) return Result.ok();
        return Result.error("删除失败");
    }
}
