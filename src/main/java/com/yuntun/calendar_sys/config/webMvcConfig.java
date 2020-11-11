package com.yuntun.calendar_sys.config;

import com.yuntun.calendar_sys.interceptor.ApiInterceptor;
import com.yuntun.calendar_sys.interceptor.PermissionInterceptor;
import com.yuntun.calendar_sys.interceptor.SysLoginInterceptor;
import com.yuntun.calendar_sys.interceptor.WechatLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author whj
 * @since 2020/11/6
 */
@Configuration
public class webMvcConfig implements WebMvcConfigurer {

    @Autowired
    ApiInterceptor apiInterceptor;

    @Autowired
    SysLoginInterceptor sysLoginInterceptor;

    @Autowired
    PermissionInterceptor permissionInterceptor;

    @Autowired
    WechatLoginInterceptor wechatLoginInterceptor;

    public static final List<String> SYS_LOGIN_WHITE_LIST = new ArrayList<>();
    public static final List<String> WECHAT_LOGIN_WHITE_LIST = new ArrayList<>();

    static {
        SYS_LOGIN_WHITE_LIST.add("/sys/sysuser/captcha/**");
        SYS_LOGIN_WHITE_LIST.add("/sys/sysuser/login");
        SYS_LOGIN_WHITE_LIST.add("/sys/sysuser/publickey");
        //不拦截小程序端的接口
        SYS_LOGIN_WHITE_LIST.add("/wechat/**");
        SYS_LOGIN_WHITE_LIST.add("/error/**");
        //不拦截open请求
        SYS_LOGIN_WHITE_LIST.add("/open/**");


        //不拦截后台管理系统的请求
        WECHAT_LOGIN_WHITE_LIST.add("/wechat/user/login");
        WECHAT_LOGIN_WHITE_LIST.add("/sys/**");
        SYS_LOGIN_WHITE_LIST.add("/error/**");
        //不拦截open请求
        WECHAT_LOGIN_WHITE_LIST.add("/open/**");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //拦截器添加的顺序就是拦截的顺序
        registry.addInterceptor(apiInterceptor)
                .addPathPatterns("/**")
        ;

        registry.addInterceptor(sysLoginInterceptor)
                .excludePathPatterns(SYS_LOGIN_WHITE_LIST)
                .addPathPatterns("/**")
        ;
        // registry.addInterceptor(wechatLoginInterceptor)
        //         .excludePathPatterns(WECHAT_LOGIN_WHITE_LIST)
        //         .addPathPatterns("/**")
        // ;
        registry.addInterceptor(permissionInterceptor)
                .excludePathPatterns(SYS_LOGIN_WHITE_LIST)
                .addPathPatterns("/**")
        ;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE")
                .maxAge(3600)
                .allowCredentials(true);
    }
}
