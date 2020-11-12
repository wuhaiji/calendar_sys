package com.yuntun.calendar_sys.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * <p>
 *
 * </p>
 *
 * @author whj
 * @since 2020/11/12
 */
public class LDTUtils {
    /**
     * 将LocalDateTime转为自定义的时间格式的字符串
     *
     * @param localDateTime
     * @param format
     * @return
     */
    public static String getDateTimeAsString(LocalDateTime localDateTime, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return localDateTime.format(formatter);
    }

    /**
     * 将LocalDataTime转为timestamp
     *
     * @param ldt
     * @return
     */
    public static long localDataTimeToTimestamp(LocalDateTime ldt) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = ldt.atZone(zone).toInstant();
        return instant.toEpochMilli();
    }


    /**
     * 将timestamp转为LocalDateTime
     *
     * @param timestamp
     * @return
     */
    public static LocalDateTime timestampToLocalDatetime(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }


    /**
     * 将某时间字符串转为自定义时间格式的LocalDateTime
     *
     * @param time
     * @param format
     * @return
     */
    public static LocalDateTime parseStringToDateTime(String time, String format) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(time, df);
    }
}
