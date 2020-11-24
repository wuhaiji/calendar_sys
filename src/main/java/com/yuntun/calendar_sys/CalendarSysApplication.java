package com.yuntun.calendar_sys;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@MapperScan(basePackages = "com.yuntun.calendar_sys.mapper")
@EnableCaching
public class CalendarSysApplication {

    public static void main(String[] args) {
        SpringApplication.run(CalendarSysApplication.class, args);
    }

}
