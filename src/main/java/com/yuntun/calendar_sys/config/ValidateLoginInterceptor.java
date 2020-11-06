package com.yuntun.calendar_sys.config;

import com.alibaba.fastjson.JSONObject;
import com.yuntun.calendar_sys.exception.ServiceException;
import com.yuntun.calendar_sys.model.code.SysUserCode;
import com.yuntun.calendar_sys.util.EptUtil;
import com.yuntun.calendar_sys.util.JwtHelper;
import com.yuntun.calendar_sys.util.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.yuntun.calendar_sys.constant.JwtConstant.*;
import static com.yuntun.calendar_sys.constant.SysUserConstant.USER_TOKEN_REDIS_EXPIRE;
import static com.yuntun.calendar_sys.constant.SysUserConstant.USER_TOKEN_REDIS_KEY;

/**
 * 校验登录拦截器
 *
 * @author whj
 */
@Component
public class ValidateLoginInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(ValidateLoginInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        //首先从请求头中获取jwt串，与页面约定好存放jwt值的请求头属性名为user-token
        String jwt = httpServletRequest.getHeader(JWT_TOKEN_HEADER_KEY);
        log.debug("[登录校验拦截器]-jwt:{}", jwt);
        //判断jwt是否有效
        if (EptUtil.isEmpty(jwt)) {
            log.info("[登录校验拦截器]-未登录");
            throw new ServiceException(SysUserCode.NOT_LOGGED_IN);
        }
        //判断token在redis中是否过期
        if (RedisUtils.getString(USER_TOKEN_REDIS_KEY) == null) {
            throw new ServiceException(SysUserCode.LOGIN_FAILED_TIME_OUT);
        }
        //校验jwt是否有效,有效则返回json信息，无效则返回空
        JSONObject retJson = JwtHelper.validateLogin(jwt);
        //retJSON为空则说明jwt超时或非法
        if (EptUtil.isEmpty(retJson)) {
            log.info("[登录校验拦截器]-JWT非法或已超时，重新登录");
            throw new ServiceException(SysUserCode.LOGIN_FAILED_TIME_OUT);
        }
        //校验浏览器客户端信息
        String userAgent = httpServletRequest.getHeader(USER_AGENT_HEADER_KEY);
        String userAgentInJSON = retJson.getString(USER_AGENT);
        if (!userAgent.equals(userAgentInJSON)) {
            log.info("[登录校验拦截器]-客户端浏览器信息与JWT中存的浏览器信息不一致。当前浏览器信息:{}", userAgent);
            throw new ServiceException(SysUserCode.USER_AGENT_EXCEPTION);
        }
        long userId = Long.parseLong(retJson.getString(USER_ID));
        //将客户Id设置到threadLocal中,方便以后使用
        UserIdThreadLocal.set(userId);
        // 重置redis中token过期时间
        RedisUtils.expire(USER_TOKEN_REDIS_KEY, USER_TOKEN_REDIS_EXPIRE);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        //清除threadLocal，保证内存不会泄露
        UserIdThreadLocal.clear();
    }
}
