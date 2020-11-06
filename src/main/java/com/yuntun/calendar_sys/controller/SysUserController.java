package com.yuntun.calendar_sys.controller;


import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuntun.calendar_sys.constant.JwtConstant;
import com.yuntun.calendar_sys.constant.SysUserConstant;
import com.yuntun.calendar_sys.entity.SysUser;
import com.yuntun.calendar_sys.exception.ServiceException;
import com.yuntun.calendar_sys.model.code.CommonCode;
import com.yuntun.calendar_sys.model.code.SysUserCode;
import com.yuntun.calendar_sys.model.response.Result;
import com.yuntun.calendar_sys.model.response.RowData;
import com.yuntun.calendar_sys.service.ISysUserService;
import com.yuntun.calendar_sys.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

import static com.yuntun.calendar_sys.constant.SysUserConstant.*;
import static com.yuntun.calendar_sys.model.code.SysUserCode.LIST_SYSUSER_FAILURE;

/**
 * <p>
 * 后台管理系统用户表 前端控制器
 * </p>
 *
 * @author whj
 * @since 2020-11-05
 */
@RestController
@RequestMapping("/sysuser")
public class SysUserController {

    private static final Logger log = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

    @Autowired
    ISysUserService iSysUserService;

    @GetMapping("/list")
    public Result<RowData<SysUser>> list(Integer pageSize, Integer pageNo, SysUser sysUser) {

        ErrorUtil.isNumberValueLt(pageSize, 0, "pageSize");
        ErrorUtil.isNumberValueLt(pageNo, 0, "pageNo");

        IPage<SysUser> iPage;
        try {
            iPage = iSysUserService.page(
                    new Page<SysUser>()
                            .setSize(pageSize)
                            .setCurrent(pageNo),
                    new QueryWrapper<SysUser>()
                            .eq(EptUtil.isNotEmpty(sysUser.getUsername()), "SysUser_name", sysUser.getUsername())
                            .eq(EptUtil.isNotEmpty(sysUser.getPhone()), "create_time", sysUser.getPhone())
                            .eq(EptUtil.isNotEmpty(sysUser.getRoleId()), "create_time", sysUser.getRoleId())
                            .orderByDesc("id")
            );
        } catch (Exception e) {
            throw new ServiceException(LIST_SYSUSER_FAILURE);
        }

        RowData<SysUser> data = new RowData<SysUser>()
                .setRows(iPage.getRecords())
                .setTotal(iPage.getTotal())
                .setTotalPages(iPage.getTotal());

        return Result.ok(data);
    }

    @GetMapping("/detail/{id}")
    public Result<Object> detail(@PathVariable("id") String id) {
        ErrorUtil.isObjectNull(id, "参数");
        try {
            SysUser SysUser = iSysUserService.getById(id);
            if (EptUtil.isNotEmpty(SysUser))
                return Result.ok(SysUser);
            return Result.error(SysUserCode.DETAIL_SYSUSER_FAILURE);
        } catch (Exception e) {
            log.error("异常:", e);
            throw new ServiceException(SysUserCode.DETAIL_SYSUSER_FAILURE);
        }

    }

    @PostMapping("/add")
    public Result<Object> add(SysUser SysUser) {
        ErrorUtil.isObjectNull(SysUser, "参数");
        ErrorUtil.isObjectNull(SysUser.getRoleId(), "角色id");
        ErrorUtil.isStringEmpty(SysUser.getPhone(), "电话");
        ErrorUtil.isStringLengthOutOfRange(SysUser.getPassword(), 6, 16, "密码");
        ErrorUtil.isStringLengthOutOfRange(SysUser.getUsername(), 6, 16, "用户名");
        try {
            boolean save = iSysUserService.save(SysUser);
            if (save)
                return Result.ok();
            return Result.error(SysUserCode.ADD_SYSUSER_FAILURE);
        } catch (Exception e) {
            log.error("异常:", e);
            throw new ServiceException(SysUserCode.ADD_SYSUSER_FAILURE);
        }

    }

    @PostMapping("/update")
    public Result<Object> update(SysUser sysUser) {

        ErrorUtil.isObjectNull(sysUser, "参数");
        ErrorUtil.isObjectNull(sysUser.getId(), "角色id");

        try {
            boolean save = iSysUserService.updateById(sysUser);
            if (save)
                return Result.ok();
            return Result.error(SysUserCode.UPDATE_SYSUSER_FAILURE);
        } catch (Exception e) {
            log.error("异常:", e);
            throw new ServiceException(SysUserCode.UPDATE_SYSUSER_FAILURE);
        }

    }

    @PostMapping("/delete/{id}")
    public Result<Object> delete(@PathVariable("id") Integer id) {
        ErrorUtil.isObjectNull(id, "信息id");
        try {
            boolean b = iSysUserService.removeById(id);
            if (b)
                return Result.ok();
            return Result.error(SysUserCode.DELETE_SYSUSER_FAILURE);
        } catch (Exception e) {
            log.error("异常:", e);
            throw new ServiceException(SysUserCode.DELETE_SYSUSER_FAILURE);
        }
    }

    @PostMapping("/login")
    public Result<Object> login(
            SysUser sysUser,
            String code,
            String publickey,
            HttpSession session,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        ErrorUtil.isStringEmpty(code, "图形验证码");
        ErrorUtil.isStringEmpty(publickey, "密匙");
        ErrorUtil.isStringEmpty(sysUser.getPassword(), "密码");
        if (EptUtil.isEmpty(sysUser.getUsername()) && EptUtil.isEmpty(sysUser.getPhone())) {
            throw new ServiceException("PARAM_ERROR", "账号不能为空");
        }
        //先验证图形验证码
        String totalCode = (String) session.getAttribute(SysUserConstant.CAPTCHA_SESSION_KEY);
        if (!code.equals(totalCode)) {
            return Result.error(SysUserCode.LOGIN_CAPTCHA_ERROR);
        }
        //再验证账号
        SysUser totalUser;
        try {
            totalUser = iSysUserService.getOne(
                    new QueryWrapper<SysUser>()
                            .eq("username", sysUser.getUsername())
                            .or()
                            .eq("phone", sysUser.getPhone())
            );
        } catch (Exception e) {
            log.error("login error:", e);
            throw new ServiceException(SysUserCode.LOGIN_EXCEPTION);
        }
        if (totalUser == null) {
            return Result.error(SysUserCode.LOGIN_FAILED_USERNAME_INCORRECT);
        }

        //最后验证密码
        String passwordEncrypt = getPasswordDecrypt(sysUser.getPassword(), publickey);
        if (!totalUser.getPassword().equals(passwordEncrypt)) {
            return Result.error(SysUserCode.LOGIN_FAILED_PASSWORD_INCORRECT);
        }
        //获取用户客户端信息，防止跨域
        String userAgent = request.getHeader(JwtConstant.USER_AGENT_HEADER_KEY);
        //生成jwt token
        String token;
        try {
            token = JwtHelper.generateJWT(""+totalUser.getId(), totalUser.getUsername(), userAgent);
            //存入redis
            RedisUtils.setValueTimeout(USER_TOKEN_REDIS_KEY, token, USER_TOKEN_REDIS_EXPIRE);
        } catch (Exception e) {
            log.error("生成token异常:", e);
            throw new ServiceException(CommonCode.SERVER_ERROR);
        }
        //token放置在请求头中
        response.setHeader(JwtConstant.JWT_TOKEN_HEADER_KEY, token);

        //更新用户登录时间
        totalUser.setLastLoginTime(LocalDateTime.now());
        iSysUserService.updateById(totalUser);
        totalUser.setPassword(null);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", totalUser.getUsername());
        jsonObject.put("phone", totalUser.getPhone());
        jsonObject.put("id", totalUser.getId());
        jsonObject.put("token", token);
        return Result.ok(jsonObject);
    }

    /**
     * 获取public key
     *
     * @return 公钥
     */
    @GetMapping("/publickey")
    public Result<String> getPublicKey(HttpSession session) {
        long timeMillis1 = System.currentTimeMillis();
        Map<String, String> map = RSAUtils.genKeyPair();
        long timeMillis2 = System.currentTimeMillis();
        System.out.println("生成密匙对时间：" + (timeMillis2 - timeMillis1));
        String publicKey = map.get(RSAUtils.PUBLIC_KEY_STR);
        String privateKey = map.get(RSAUtils.PRIVATE_KEY_STR);
        RedisUtils.hashPut(RSA_KEYPAIR_REDIS_KEY, publicKey, privateKey);
        return Result.ok(publicKey);
    }

    /**
     * 获取验证码
     *
     * @param session session
     * @return 图形验证码图片base64
     */
    @GetMapping("/captcha")
    public void captcha(HttpSession session, HttpServletResponse response) throws IOException {
        try {
            CircleCaptcha circleCaptcha = CaptchaUtil.createCircleCaptcha(200, 100, 4, 20);
            String code = circleCaptcha.getCode();
            log.info("code:{}", code);

            response.setContentType("image/jpeg");//设置相应类型,告诉浏览器输出的内容为图片
            response.setHeader("Pragma", "No-cache");//设置响应头信息，告诉浏览器不要缓存此内容
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expire", 0);

            session.setAttribute(SysUserConstant.CAPTCHA_SESSION_KEY, code);
            response.getOutputStream().write(circleCaptcha.getImageBytes());
        } catch (IOException e) {
            log.error("captcha error:", e);
        }
    }

    /**
     * 通过redis解密密码
     *
     * @param password
     * @param publicKey
     * @return
     */
    private String getPasswordDecrypt(String password, String publicKey) {
        String privateKey = (String) RedisUtils.hashGet(RSA_KEYPAIR_REDIS_KEY, publicKey);
        if (privateKey == null) {
            throw new ServiceException(SysUserCode.LOGIN_FAILED_PUBLICKEY_INCORRECT);
        }
        //解密
        String passwordDecrypt;
        try {
            passwordDecrypt = RSAUtils.decrypt(password, privateKey);
        } catch (Exception e) {
            log.error("解密错误：", e);
            //解密出錯，返回登录异常
            throw new ServiceException(SysUserCode.LOGIN_FAILED_PUBLICKEY_INCORRECT);
        }
        return passwordDecrypt;
    }


}
