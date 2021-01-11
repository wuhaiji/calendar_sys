package com.yuntun.calendar_sys.controller.wechat;


import cn.hutool.core.date.ChineseDate;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.date.chinese.LunarFestival;
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
import com.yuntun.calendar_sys.model.code.UserCode;
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
        List<HeartWordsBean> heartWordsBeanList = records.parallelStream()
                .map(this::getHeartWordsBean)
                .collect(Collectors.toList());


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

        LocalDateTime dateTimeStart;
        try {
            dateTimeStart = LocalDateTimeUtil.parse(dto.getDate());
        } catch (Exception e) {
            log.error("按日期查询30天心语->日期格式不正确:{}", dto.getDate(), e);
            throw new ServiceException(HeartWordsCode.DATE_PARAM_ERROR);
        }

        //查询前15条
        List<HeartWords> heartWordsListPrev;
        List<HeartWords> heartWordsListNext;
        try {
            heartWordsListPrev = iHeartWordsService.list(
                    new QueryWrapper<HeartWords>()
                            //只给用户看见审核通过的心语
                            .eq("disable", HeartWordsConstant.EXAMINATION_PASSED)
                            .eq(openId != null, "user_open_id", openId)
                            .orderByDesc("create_time")
                            .ge("create_time", dateTimeStart)
                            .last("limit 15")

            );
            //查询后14条
            heartWordsListNext = iHeartWordsService.list(
                    new QueryWrapper<HeartWords>()
                            //只给用户看见审核通过的心语
                            .eq("disable", HeartWordsConstant.EXAMINATION_PASSED)
                            .eq(openId != null, "user_open_id", openId)
                            .orderByDesc("create_time")
                            .lt("create_time", dateTimeStart)
                            .last("limit 14")

            );
        } catch (Exception e) {
            log.error("按日期查询30天心语->查询异常", e);
            throw new ServiceException(HeartWordsCode.LIST_30_ERROR);
        }
        ArrayList<HeartWords> heartWords = new ArrayList<>();
        heartWords.addAll(heartWordsListPrev);
        heartWords.addAll(heartWordsListNext);
        List<HeartWordsBean> collect = heartWords.parallelStream()
                //过滤掉不属于当月的数据
                .filter(i -> i.getCreateTime().getMonthValue() == (dateTimeStart.getMonthValue()))
                .map(this::getHeartWordsBean)
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
                .map(this::getHeartWordsBean)
                .collect(Collectors.groupingBy(i -> i.getCreateTime().getMonthValue()));

        List<JSONObject> list = collect.entrySet().parallelStream()
                .map(i -> {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("rows", i.getValue());
                    jsonObject.put("month", i.getKey());
                    return jsonObject;
                }).collect(Collectors.toList());

        list = list.stream().sorted(
                (o1, o2) -> o2.getInteger("month") < (o1.getInteger("month")) ? 1 : -1
        ).collect(Collectors.toList());

        RowData<JSONObject> data = RowData.of(JSONObject.class)
                .setRows(list)
                .setTotal(heartWordsIPage.getTotal())
                .setTotalPages(heartWordsIPage.getTotal());
        return Result.ok(data);
    }

    @GetMapping("/detail/{id}")
    public Result<Object> detail(@PathVariable("id") String id) {
        ErrorUtil.isObjectNull(id, "参数");
        try {
            HeartWords heartWords = iHeartWordsService.getById(id);
            if (EptUtil.isNotEmpty(getHeartWordsBean(heartWords)))
                return Result.ok(heartWords);
            return Result.error(HeartWordsCode.DETAIL_ERROR);
        } catch (Exception e) {
            log.error("异常:", e);
            throw new ServiceException(UserCode.DETAIL_USER_FAILURE);
        }

    }

    @PostMapping("/add")
    public Result<Object> add(@RequestBody HeartsWordsDto dto) {

        List<String> content = dto.getContent();

        ErrorUtil.isStringEmpty(dto.getPicUrl(), "心语内容图片");
        ErrorUtil.isStringEmpty(dto.getImageUrl(), "心语概览图");
        ErrorUtil.isObjectNull(dto.getTempId(), "模板id");
        ErrorUtil.isListEmpty(content, "内容不能为空");
        ErrorUtil.isStringEmpty(dto.getSource(), "来源不能为空，用户自建的用用户的名称");

        String openId = WechatOpenIdHolder.get();

        //心语转为一句字符串,用"@#@"分隔
        HeartWords heartWords = new HeartWords();
        heartWords.setLunar(new ChineseDate(new Date()).getChineseDay());
        BeanUtils.copyProperties(dto, heartWords);

        String join = String.join(HeartWordsConstant.CONTENT_DELIMITER, content);
        heartWords.setContent(join);

        log.info("openId:{}", openId);
        if (EptUtil.isEmpty(openId)) {
            log.error("用户添加心语->获取openId异常");
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
            log.error("用户添加心语->查询当前登录用户异常");
            throw new ServiceException(HeartWordsCode.ADD_ERROR);
        }
        heartWords.setCreator(user.getId());
        heartWords.setUserOpenId(openId);
        //保存
        try {
            boolean save = iHeartWordsService.save(heartWords);
            if (save) {
                HeartWords byId = iHeartWordsService.getById(heartWords.getId());
                HeartWordsBean heartWordsBean = getHeartWordsBean(byId);
                return Result.ok(heartWordsBean);
            }
            throw new Exception();
        } catch (Exception e) {
            log.error("Exception", e);
            throw new ServiceException(HeartWordsCode.ADD_ERROR);
        }

    }

    @PostMapping("/delete/{id}")
    public Result<Object> delete(@PathVariable("id") Integer id) {
        ErrorUtil.isObjectNull(id, "信息id");
        boolean b = iHeartWordsService.removeById(id);
        if (b) return Result.ok();
        return Result.error("删除失败");
    }

    private HeartWordsBean getHeartWordsBean(HeartWords i) {
        HeartWordsBean heartWordsBean = new HeartWordsBean();
        heartWordsBean.setDayOfMonth(i.getCreateTime().getDayOfMonth());
        BeanUtils.copyProperties(i, heartWordsBean);
        String content = i.getContent();
        String[] split = content.split(HeartWordsConstant.CONTENT_DELIMITER);
        heartWordsBean.setContentList(Arrays.asList(split));
        return heartWordsBean;
    }
}
