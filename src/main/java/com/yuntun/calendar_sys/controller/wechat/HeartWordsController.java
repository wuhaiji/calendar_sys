package com.yuntun.calendar_sys.controller.wechat;


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
@RequestMapping("/wechat/heartwords")
public class HeartWordsController {

    private static final Logger log = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

    @Autowired
    IHeartWordsService iHeartWordsService;
    @Autowired
    IUserService iUserService;

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
                        .orderByDesc("id")
        );

        List<HeartWords> records = heartWordsIPage.getRecords();
        List<HeartWordsBean> heartWordsBeanList = records.parallelStream().map(i -> {
            String content = i.getContent();
            String[] split = content.split("@#@");
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

    @PostMapping("/add")
    public Result<Object> add(@RequestBody HeartsWordsDto dto) {
        ErrorUtil.isStringEmpty(dto.getPicUrl(), "图片地址");
        ErrorUtil.isObjectNull(dto.getUserOpenId(), "用户id");
        ErrorUtil.isObjectNull(dto.getTempId(), "模板id");
        ErrorUtil.isStringEmpty(dto.getContent(), "内容不能为空");
        ErrorUtil.isStringEmpty(dto.getSource(), "来源不能为空，用户自建的用用户的名称");


        HeartWords heartWords = new HeartWords();
        BeanUtils.copyProperties(dto, heartWords);

        User user = iUserService.getOne(
                new QueryWrapper<User>()
                        .eq(
                                EptUtil.isEmpty(WechatOpenIdHolder.get()),
                                "open_id",
                                WechatOpenIdHolder.get()
                        )
        );
        if (user == null) {
            throw new ServiceException(HeartWordsCode.ADD_HEART_WORDS_ERROR);
        }
        heartWords.setCreator(user.getId());

        try {
            boolean save = iHeartWordsService.save(heartWords);
            if (save) {
                return Result.ok(heartWords);
            }
            throw new Exception();
        } catch (Exception e) {
            log.error("Exception", e);
            throw new ServiceException(HeartWordsCode.ADD_HEART_WORDS_ERROR);
        }

    }

    @PostMapping("/update")
    public Result<Object> update(HeartWords HeartWords) {
        ErrorUtil.isObjectNull(HeartWords.getId(), "心语id不能为空");
        try {
            boolean save = iHeartWordsService.updateById(HeartWords);
            if (save) {
                return Result.ok();
            }
            throw new Exception();
        } catch (Exception e) {
            log.error("Exception", e);
            throw new ServiceException(HeartWordsCode.UPDATE_SYSUSER_ERROR);
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
