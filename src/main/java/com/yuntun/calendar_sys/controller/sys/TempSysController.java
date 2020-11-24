package com.yuntun.calendar_sys.controller.sys;


import cn.hutool.core.date.ChineseDate;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.date.chinese.LunarFestival;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuntun.calendar_sys.constant.HeartWordsConstant;
import com.yuntun.calendar_sys.entity.SysUser;
import com.yuntun.calendar_sys.entity.Temp;
import com.yuntun.calendar_sys.exception.ServiceException;
import com.yuntun.calendar_sys.interceptor.UserIdHolder;
import com.yuntun.calendar_sys.model.bean.TempBean;
import com.yuntun.calendar_sys.model.code.HeartWordsCode;
import com.yuntun.calendar_sys.model.code.TempCode;
import com.yuntun.calendar_sys.model.code.UserCode;
import com.yuntun.calendar_sys.model.dto.TempDto;
import com.yuntun.calendar_sys.model.response.Result;
import com.yuntun.calendar_sys.model.response.RowData;
import com.yuntun.calendar_sys.service.ISysUserService;
import com.yuntun.calendar_sys.service.ITempService;
import com.yuntun.calendar_sys.util.EptUtil;
import com.yuntun.calendar_sys.util.ErrorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
@RequestMapping("/sys/temp")
public class TempSysController {

    private static final Logger log = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

    @Autowired
    ITempService iTempService;

    @Autowired
    ISysUserService iSysUserService;

    @GetMapping("/list")
    public Result<Object> getList(Integer pageSize, Integer pageNo, TempDto dto) {

        ErrorUtil.isNumberValueLt(pageSize, 0, "pageSize");
        ErrorUtil.isNumberValueLt(pageNo, 0, "pageNo");

        IPage<Temp> tempPage = iTempService.page(
                new Page<Temp>()
                        .setSize(pageSize)
                        .setCurrent(pageNo),
                new QueryWrapper<Temp>()
                        .orderByDesc("create_time")
        );

        List<TempBean> tempBeanList = tempPage.getRecords()
                .parallelStream()
                .map(i -> getTempBean(i)).collect(Collectors.toList());

        RowData<TempBean> data = RowData.of(TempBean.class)
                .setRows(tempBeanList)
                .setTotal(tempPage.getTotal())
                .setTotalPages(tempPage.getTotal());
        log.info("心语集合：{}", data);
        return Result.ok(data);
    }


    @GetMapping("/detail/{id}")
    public Result<Object> detail(@PathVariable("id") String id) {

        ErrorUtil.isObjectNull(id, "参数");
        try {
            Temp temp = iTempService.getById(id);
            if (EptUtil.isNotEmpty(temp)){
                TempBean tempBean = getTempBean(temp);
                return Result.ok(tempBean);
            }
            return Result.error(TempCode.DETAIL_TEMP_ID_DOES_NOT_EXIST);
        } catch (Exception e) {
            log.error("异常:", e);
            throw new ServiceException(TempCode.DETAIL_TEMP_FAILURE);
        }

    }

    @PostMapping("/add")
    public Result<Object> add(TempDto dto) {
        List<String> content = dto.getTempContent();
        ErrorUtil.isObjectNull(content, "图文内容");
        ErrorUtil.isObjectNull(dto.getTempPicUrl(), "图文图片");
        ErrorUtil.isObjectNull(dto.getTempSource(), "图文来源");
        ErrorUtil.isObjectNull(dto.getTempTitle(), "图文标题");
        ErrorUtil.isObjectNull(dto.getPublishTime(), "发布日期");
        //查询当天是否有模板
        List<Temp> targetTemps = iTempService.list(
                new QueryWrapper<Temp>()
                        .likeRight("publish_time", dto.getPublishTime())
        );
        if (targetTemps.size() > 0) {
            log.error(dto.getPublishTime().toString() + "官方图文已经创建");
            throw new ServiceException(TempCode.TODAY_TEMP_ALREADY_EXISTS);
        }
        //心语转为一句字符串,用"@#@"分隔
        Temp temp = new Temp();
        BeanUtils.copyProperties(dto, temp);
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : content) {
            stringBuilder
                    .append(s)
                    .append(HeartWordsConstant.CONTENT_DELIMITER);
        }
        int end = stringBuilder.length() - HeartWordsConstant.CONTENT_DELIMITER.length();
        String substring = stringBuilder.substring(0, end);
        temp.setTempContent(substring);

        //设置创建者
        Integer userId = UserIdHolder.get();
        log.info("openId:{}", userId);
        if (EptUtil.isEmpty(userId)) {
            throw new ServiceException("获取登录人id异常");
        }
        SysUser sysUser = iSysUserService.getOne(
                new QueryWrapper<SysUser>()
                        .eq(
                                "id",
                                userId
                        )
        );
        if (sysUser == null) {
            throw new ServiceException(TempCode.ADD_TEMP_FAILURE);
        }
        temp.setCreator(sysUser.getId());

        //默认为模板1
        if(temp.getTempId()==null){

            temp.setTempId(1);
        }

        //设置发布日期
        ChineseDate chineseDate = new ChineseDate(DateUtil.parseDate(dto.getPublishTime().toString()));
        temp.setLunar(chineseDate.getChineseDay());

        //清空没用信息
        temp.setId(null);

        try {
            boolean save = iTempService.save(temp);
            if (save)
                return Result.ok();
            return Result.error(TempCode.ADD_TEMP_FAILURE);
        } catch (Exception e) {
            log.error("异常:", e);
            throw new ServiceException(TempCode.ADD_TEMP_FAILURE);
        }
    }

    /**
     * 查询当天是否已经创建官方图文模板，提示管理员不要重复创建
     *
     * @return
     */
    @GetMapping("/date")
    public Result<Object> todayTempExist(String date) {

        QueryWrapper<Temp> query = new QueryWrapper<>();

        if (EptUtil.isNotEmpty(date)) {
            //根据发送的日期查询
            LocalDate beginDay = LocalDateTimeUtil.parseDate(date);
            query.likeRight("create_time", beginDay);
        }
        try {
            List<Temp> list = iTempService.list(query);
            if (EptUtil.isNotEmpty(list)) {
                return Result.ok(false);
            } else {
                return Result.ok(true);
            }
        } catch (Exception e) {
            log.error("异常:", e);
            throw new ServiceException(TempCode.LIST_DAY_FAILURE);
        }
    }


    @PostMapping("/update")
    public Result<Object> update(TempDto dto) {

        ErrorUtil.isObjectNull(dto.getId(), "图文模板id");
        Temp temp = new Temp();
        BeanUtils.copyProperties(dto, temp);
        List<String> content = dto.getTempContent();
        if (content != null) {
            temp.setTempContent(String.join(HeartWordsConstant.CONTENT_DELIMITER, content));
        }
        try {
            boolean save = iTempService.updateById(temp);
            if (save)
                return Result.ok();
            return Result.error(TempCode.UPDATE_TEMP_FAILURE);
        } catch (Exception e) {
            log.error("异常:", e);
            throw new ServiceException(TempCode.UPDATE_TEMP_FAILURE);
        }
    }

    @PostMapping("/delete/{id}")
    public Result<Object> delete(@PathVariable("id") Integer id) {
        ErrorUtil.isObjectNull(id, "图文模板id");
        try {
            boolean b = iTempService.removeById(id);
            if (b){
                return Result.ok();
            }
            return Result.error(TempCode.DELETE_TEMP_FAILURE);
        } catch (Exception e) {
            log.error("异常:", e);
            throw new ServiceException(TempCode.DELETE_TEMP_FAILURE);
        }
    }


    private TempBean getTempBean(Temp i) {
        String content = i.getTempContent();
        String[] split = content.split(HeartWordsConstant.CONTENT_DELIMITER);
        TempBean tempBean = TempBean.of();
        BeanUtils.copyProperties(i, tempBean);
        tempBean.setTempContent(Arrays.asList(split));
        return tempBean;
    }
}
