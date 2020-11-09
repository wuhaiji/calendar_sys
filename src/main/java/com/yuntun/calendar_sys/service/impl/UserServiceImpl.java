package com.yuntun.calendar_sys.service.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuntun.calendar_sys.constant.JwtConstant;
import com.yuntun.calendar_sys.constant.UserConstant;
import com.yuntun.calendar_sys.entity.User;
import com.yuntun.calendar_sys.exception.ServiceException;
import com.yuntun.calendar_sys.mapper.UserMapper;
import com.yuntun.calendar_sys.model.bean.UserBean;
import com.yuntun.calendar_sys.model.bean.WechatLoginBean;
import com.yuntun.calendar_sys.model.code.UserCode;
import com.yuntun.calendar_sys.model.dto.WechatLoginDto;
import com.yuntun.calendar_sys.service.IUserService;
import com.yuntun.calendar_sys.util.EptUtil;
import com.yuntun.calendar_sys.util.JwtHelper;
import com.yuntun.calendar_sys.util.RedisUtils;
import com.yuntun.calendar_sys.util.ServletUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.Security;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 小程序用户表 服务实现类
 * </p>
 *
 * @author whj
 * @since 2020-11-06
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private static final Logger log = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

    public static final String wechatLoginUrl = "https://api.weixin.qq.com/sns/jscode2session";
    public static final String appid = "wx6c889de5602cdf0c";
    public static final String secret = "f8499a4d5ce441267a07ab792086a08c";
    public static final String grant_type = "authorization_code";
    public static final int SUCCESS_CODE = 0;

    @Override
    public UserBean getUserInfoMap(WechatLoginDto loginRequest) {

        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        WechatLoginBean wechatLoginBean;
        try {
            HashMap<String, Object> paramMap = new HashMap<>();
            paramMap.put("appid", appid);
            paramMap.put("secret", secret);
            paramMap.put("grant_type", grant_type);
            paramMap.put("js_code", loginRequest.getCode());
            String body = HttpUtil.get(wechatLoginUrl, paramMap);
            wechatLoginBean = JSONObject.parseObject(body, WechatLoginBean.class);
        } catch (Exception e) {
            log.error("请求微信平台异常:", e);
            throw new ServiceException(UserCode.WECHAT_USER_LOGIN_ERROR);
        }

        String sessionKey = wechatLoginBean.getSession_key();
        String openId = wechatLoginBean.getOpenid();
        Integer errcode = wechatLoginBean.getErrcode();
        //校验返回值
        if (EptUtil.isEmpty(errcode)
                || errcode != SUCCESS_CODE
                || EptUtil.isEmpty(sessionKey)
                || EptUtil.isEmpty(openId)

        ) {
            log.error("微信平台报错：{}", wechatLoginBean.getErrmsg());
            throw new ServiceException(UserCode.WECHAT_USER_LOGIN_ERROR);
        }


        WechatLoginDto.UserInfo userInfo = loginRequest.getUserInfo();
        User insertOrUpdateUser = new User();
        insertOrUpdateUser.setOpenId(openId);
        insertOrUpdateUser.setNickname(userInfo.getNickName());
        insertOrUpdateUser.setAvatarUrl(userInfo.getAvatarUrl());
        insertOrUpdateUser.setGender(userInfo.getGender());
        insertOrUpdateUser.setCity(userInfo.getCity());
        insertOrUpdateUser.setCountry(userInfo.getCountry());
        insertOrUpdateUser.setProvince(userInfo.getProvince());


        JSONObject encryptedData = getEncryptedData(loginRequest.getEncryptedData(), sessionKey, loginRequest.getIv());
        if (encryptedData != null) {
            String unionId = encryptedData.getString("unionId");
            insertOrUpdateUser.setUnionId(unionId);
        }

        // 根据openid查询用户
        User targetUser = baseMapper.selectOne(new QueryWrapper<User>().eq("open_id", openId));
        //生成token
        String wechatToken;
        try {
            String userAgent = ServletUtil.getRequest().getHeader(JwtConstant.USER_AGENT_HEADER_KEY);
            wechatToken = JwtHelper.generateJWT(openId, insertOrUpdateUser.getNickname(), userAgent);
        } catch (Exception e) {
            log.error("生成小程序端token异常:", e);
            throw new ServiceException(UserCode.WECHAT_USER_LOGIN_ERROR);
        }
        insertOrUpdateUser.setToken(wechatToken);


        if (targetUser == null) {
            // 用户不存在，insert用户，这里加了个分布式锁，防止insert重复用户，看自己的业务，决定要不要这段代码
            if (RedisUtils.setLockNx(UserConstant.INSERT_USER_DISTRIBUTED_LOCK_PREFIX, 10L, TimeUnit.SECONDS)) {
                // 用户入库
                baseMapper.insert(insertOrUpdateUser);
            }
        } else {
            // 已存在，做已存在的处理，如更新用户的头像，昵称等，根据openID更新，这里代码自己写
            baseMapper.updateById(insertOrUpdateUser);
        }
        UserBean userBean = new UserBean();
        BeanUtils.copyProperties(insertOrUpdateUser, userBean);
        return userBean;
    }

    private JSONObject getEncryptedData(String encryptedData, String sessionKey, String iv) {
        // 被加密的数据
        byte[] dataByte = Base64.getDecoder().decode(encryptedData);
        // 加密秘钥
        byte[] keyByte = Base64.getDecoder().decode(sessionKey);
        // 偏移量
        byte[] ivByte = Base64.getDecoder().decode(iv);
        try {
            // 如果密钥不足16位，那么就补足.这个if中的内容很重要
            int base = 16;
            if (keyByte.length % base != 0) {
                int groups = keyByte.length / base + 1;
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
                keyByte = temp;
            }
            // 初始化
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
            SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(ivByte));
            cipher.init(Cipher.DECRYPT_MODE, spec, parameters);// 初始化
            byte[] resultByte = cipher.doFinal(dataByte);
            if (null != resultByte && resultByte.length > 0) {
                String result = new String(resultByte, StandardCharsets.UTF_8);
                return JSONObject.parseObject(result);
            }
        } catch (Exception e) {
            log.error("解密加密信息报错:", e);
        }
        return null;
    }
}
