package com.yuntun.calendar_sys.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
    ValidateLoginInterceptor validateLoginInterceptor;
    @Autowired
    ApiInterceptor apiInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiInterceptor)
                .addPathPatterns("/**");
        registry.addInterceptor(validateLoginInterceptor)
                .excludePathPatterns(
                        "/calendar-sys/sysuser/captcha",
                        "/calendar-sys/sysuser/login",
                        "/calendar-sys/sysuser/publickey"
                ).addPathPatterns("/**")
        ;
    }


}
