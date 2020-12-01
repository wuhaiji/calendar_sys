package com.yuntun.calendar_sys.controller.sys;


import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.captcha.generator.MathGenerator;
import cn.hutool.crypto.SecureUtil;
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
import com.yuntun.calendar_sys.model.dto.SysUserDto;
import com.yuntun.calendar_sys.model.response.Result;
import com.yuntun.calendar_sys.model.response.RowData;
import com.yuntun.calendar_sys.service.ISysUserService;
import com.yuntun.calendar_sys.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.yuntun.calendar_sys.constant.JwtConstant.JWT_TOKEN_HEADER_KEY;
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
@RequestMapping("/sys/sysuser")
public class SysUserController {

    private static final Logger log = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
    public static final int FIVE_MINUTE = 300;

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

        ErrorUtil.isObjectNull(SysUser.getRoleId(), "角色id");
        ErrorUtil.isStringEmpty(SysUser.getPhone(), "电话");
        ErrorUtil.isStringLengthOutOfRange(SysUser.getUsername(), 2, 10, "用户名");
        ErrorUtil.isStringLengthOutOfRange(SysUser.getPassword(), 6, 16, "密码");
        String password = SysUser.getPassword();
        SysUser.setPassword(SecureUtil.md5(password));

        //校验用户名是否重复
        List<SysUser> sysUserList = iSysUserService.list(
                new QueryWrapper<SysUser>()
                        .eq("username", SysUser.getUsername())
        );
        if (sysUserList.size() > 0) {
            throw new ServiceException(SysUserCode.USERNAME_ALREADY_EXISTS);
        }

        //校验手机号是否重复
        List<SysUser> phoneUserList = iSysUserService.list(
                new QueryWrapper<SysUser>()
                        .eq("phone", SysUser.getPhone())
        );
        if (phoneUserList.size() > 0) {
            throw new ServiceException(SysUserCode.PHONE_NUMBER_ALREADY_EXISTS);
        }

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
    public Result<Object> update(SysUserDto sysUserDto, String publickey) {

        ErrorUtil.isObjectNull(sysUserDto.getId(), "角色id");
        ErrorUtil.isStringEmpty(publickey, "公钥");

        String passwordDecrypt = getPasswordDecrypt(sysUserDto.getPassword(), publickey);

        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(sysUserDto, sysUser);
        sysUser.setPassword(SecureUtil.md5(passwordDecrypt));
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

    @PostMapping("/password/update")
    public Result<Object> update(String username, String oldPassword, String newPassword, String publickey) {

        ErrorUtil.isStringEmpty(username, "用户名");

        ErrorUtil.isStringEmpty(publickey, "公钥");

        //先查询用户是否存在
        SysUser targetSysUser = iSysUserService.getOne(new QueryWrapper<SysUser>().eq("username", username));
        if (targetSysUser == null) {
            log.error("用户不存在");
            throw new ServiceException(SysUserCode.LOGIN_FAILED_USERNAME_INCORRECT);
        }

        //判断旧密码是否正确
        String oldPasswordDecrypt = SecureUtil.md5(getPasswordDecrypt(oldPassword, publickey));
        if (!targetSysUser.getPassword().equals(oldPasswordDecrypt)) {
            log.error("密码不正确");
            throw new ServiceException(SysUserCode.LOGIN_FAILED_PASSWORD_INCORRECT);
        }

        //解密新密码
        String newPasswordDecrypt = getPasswordDecrypt(newPassword, publickey);
        ErrorUtil.isStringLengthOutOfRange(newPasswordDecrypt, 6, 16, "新密码");
        String newPasswordMd5 = SecureUtil.md5(newPasswordDecrypt);
        targetSysUser.setPassword(newPasswordMd5);
        try {
            boolean save = iSysUserService.updateById(targetSysUser);
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

    @PostMapping("/disable/{id}")
    public Result<Object> disable(@PathVariable("id") Integer id) {
        ErrorUtil.isObjectNull(id, "用户id");
        try {
            SysUser sysUser = new SysUser().setDisable(IS_DISABLED).setId(id);
            boolean b = iSysUserService.updateById(sysUser);
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
            String username,
            String password,
            String code,
            String publickey,
            String captchaId,
            HttpSession session,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        ErrorUtil.isStringEmpty(code, "图形验证码");
        ErrorUtil.isStringEmpty(publickey, "密匙");
        ErrorUtil.isStringEmpty(password, "密码");
        ErrorUtil.isStringEmpty(username, "账号");

        //先验证图形验证码
        String totalCode = RedisUtils.getString(SysUserConstant.CAPTCHA_ID_REDIS_KEY + captchaId);
        if (totalCode == null || !new MathGenerator().verify(totalCode, code)) {
            return Result.error(SysUserCode.LOGIN_CAPTCHA_ERROR);
        }
        //再验证账号
        SysUser targetUser;
        try {
            targetUser = iSysUserService.getOne(
                    new QueryWrapper<SysUser>()
                            .eq("username", username)
                            .or()
                            .eq("phone", username)
            );
        } catch (Exception e) {
            log.error("login error:", e);
            throw new ServiceException(SysUserCode.LOGIN_EXCEPTION);
        }
        if (targetUser == null) {
            return Result.error(SysUserCode.LOGIN_FAILED_USERNAME_INCORRECT);
        }

        if (targetUser.getDisable().equals(IS_DISABLED)) {
            return Result.error(SysUserCode.LOGIN_FAILED_ERROR_ACCOUNT_IS_DISABLED);
        }

        //最后验证密码
        String passwordEncrypt = getPasswordDecrypt(password, publickey);
        if (!targetUser.getPassword().equals(SecureUtil.md5(passwordEncrypt))) {
            return Result.error(SysUserCode.LOGIN_FAILED_PASSWORD_INCORRECT);
        }
        //获取用户客户端信息，防止跨域
        String userAgent = request.getHeader(JwtConstant.USER_AGENT_HEADER_KEY);
        //生成jwt jwtToken
        String jwtToken;
        try {
            jwtToken = JwtHelper.generateJWT("" + targetUser.getId(), targetUser.getUsername(), userAgent);
            //存入redis
            RedisUtils.setValueTimeoutSeconds(USER_TOKEN_REDIS_KEY + SecureUtil.md5(jwtToken), jwtToken, USER_TOKEN_REDIS_EXPIRE);
        } catch (Exception e) {
            log.error("生成token异常:", e);
            throw new ServiceException(CommonCode.SERVER_ERROR);
        }
        //token放置在请求头中
        response.setHeader(JwtConstant.JWT_TOKEN_HEADER_KEY, jwtToken);

        //更新用户登录时间
        targetUser.setLastLoginTime(LocalDateTime.now());
        iSysUserService.updateById(targetUser);
        targetUser.setPassword(null);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", targetUser.getUsername());
        jsonObject.put("phone", targetUser.getPhone());
        jsonObject.put("id", targetUser.getId());
        jsonObject.put("token", jwtToken);
        return Result.ok(jsonObject);
    }

    @PostMapping("/logout")
    public Result<Object> logout(HttpServletRequest request) {
        String jwtToken = request.getHeader(JWT_TOKEN_HEADER_KEY);
        if(!EptUtil.isEmpty(jwtToken)){
            RedisUtils.delKey(USER_TOKEN_REDIS_KEY + SecureUtil.md5(jwtToken));
        }
        return Result.ok();
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
        //5分钟过期
        RedisUtils.setValueTimeoutSeconds(RSA_KEYPAIR_REDIS_KEY + SecureUtil.md5(publicKey), privateKey, 300);
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
            // 自定义验证码内容为四则运算方式
            circleCaptcha.setGenerator(new MathGenerator(1));
            // 重新生成code
            circleCaptcha.createCode();
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
     * 获取验证码
     *
     * @param session session
     * @return 图形验证码图片base64
     */
    @GetMapping("/captcha/base64")
    public Result<Object> captcha(HttpSession session) throws IOException {
        CircleCaptcha circleCaptcha = CaptchaUtil.createCircleCaptcha(200, 100, 4, 20);
        // 自定义验证码内容为四则运算方式
        circleCaptcha.setGenerator(new MathGenerator(1));
        // 重新生成code
        circleCaptcha.createCode();
        String code = circleCaptcha.getCode();
        log.info("code:{}", code);
        session.setAttribute(SysUserConstant.CAPTCHA_SESSION_KEY, code);
        String imageBase64 = circleCaptcha.getImageBase64();
        String captchaId = JwtHelper.getRandomString(16);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("base64Img", imageBase64);
        jsonObject.put("captchaId", captchaId);
        //5分钟
        RedisUtils.setValueTimeoutSeconds(SysUserConstant.CAPTCHA_ID_REDIS_KEY + captchaId, code, FIVE_MINUTE);
        return Result.ok(jsonObject);
    }

    /**
     * 通过redis解密密码
     *
     * @param password
     * @param publicKey
     * @return
     */
    private String getPasswordDecrypt(String password, String publicKey) {
        String privateKey = RedisUtils.getString(RSA_KEYPAIR_REDIS_KEY + SecureUtil.md5(publicKey));
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
